package jp.fukui.controllergenerator.fxml;

import java.io.*;
import java.util.*;
import jp.fukui.controllergenerator.pattern.*;

/** FXML解析 */
public class FxmlAnalyzer implements Pattern.Observer {
	public FxmlAnalyzer() {
		addPattern(new Pattern_Eof(this));
		addPattern(new Pattern_KeyWord(this, TokenType.SPACE, " "));
		addPattern(new Pattern_KeyWord(this, TokenType.SPACE, "\t"));
		addPattern(new Pattern_KeyWord(this, TokenType.NL, "\r\n"));
		addPattern(new Pattern_KeyWord(this, TokenType.NL, "\r"));
		addPattern(new Pattern_KeyWord(this, TokenType.NL, "\n"));
		addPattern(new Pattern_KeyWord(this, TokenType.EQ, "="));
		addPattern(new Pattern_KeyWord(this, TokenType.BRACKET, "<?"));
		addPattern(new Pattern_KeyWord(this, TokenType.BRACKET, "?>"));
		addPattern(new Pattern_KeyWord(this, TokenType.BRACKET, "</"));
		addPattern(new Pattern_KeyWord(this, TokenType.BRACKET, "/>"));
		addPattern(new Pattern_KeyWord(this, TokenType.BRACKET, "<"));
		addPattern(new Pattern_KeyWord(this, TokenType.BRACKET, ">"));
		addPattern(new Pattern_String(this));
		addPattern(new Pattern_Word(this));
		addPattern(new Pattern_UnknownChar(this));
	}

	//------------------------------------------------------------------------------
	// 字句パターン
	//------------------------------------------------------------------------------

	private ArrayList<Pattern> patList = new ArrayList<Pattern>();
	private Pattern            eofPat  = new Pattern_Eof(this);

	public void addPattern(Pattern pat) {
		this.patList.add(pat);
	}

	//------------------------------------------------------------------------------
	// 字句解析
	//------------------------------------------------------------------------------

	private ControllerHint hint;
	public ControllerHint getHint() { return(this.hint); }
	public void           setHint(ControllerHint hint) { this.hint = hint; }


	public ControllerHint analyze(File src) {
		setHint(new ControllerHint());
		getHint().controllerClass  = "";
		getHint().importList       = new ArrayList<String>();
		getHint().usedClassList    = new ArrayList<String>();
		getHint().idClassMap       = new HashMap<String, String>();
		getHint().hndlList         = new ArrayList<String[]>();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
			for(;;) {
				if(this.eofPat.comp(in))
					break;

				for(Pattern pat : this.patList) {
					if(pat.comp(in))
						break;
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		return(getHint());
	}

	//------------------------------------------------------------------------------
	// 構文解析
	//------------------------------------------------------------------------------

	private enum Mode {
		INIT,
		IN_DECL,
		WAIT_PACKAGE,
		WAIT_DECL_CLOSE,
		IN_INST_START,
		IN_INST_END,
		WAIT_ATTR,
		WAIT_EQ,
		WAIT_VALUE,
	}
	private Mode   mode      = Mode.INIT;
	private String instClassName = "";
	private String instClassPath = "";
	private String attr          = "";

	public void tokenDetected(Token t) {
		if(t.type == TokenType.EOF)
			return;

		switch(this.mode) {
		case INIT	:
			if(t.type == TokenType.BRACKET && t.data.equals("<?")) {
				this.mode = Mode.IN_DECL;
			} else if(t.type == TokenType.BRACKET && t.data.equals("<")) {
				this.mode = Mode.IN_INST_START;
			} else if(t.type == TokenType.BRACKET && t.data.equals("</")) {
				this.mode = Mode.IN_INST_END;
			}
			break;
		case IN_DECL	:
			if(t.type == TokenType.BRACKET && t.data.equals("?>")) {
				this.mode = Mode.INIT;
			} else if(t.type == TokenType.WORD && t.data.equals("import")) {
				this.mode = Mode.WAIT_PACKAGE;
			}
			break;
		case WAIT_PACKAGE	:
			if(t.type == TokenType.WORD) {
				getHint().importList.add(t.data);
				this.mode = Mode.IN_DECL;
			}
			break;
		case WAIT_DECL_CLOSE	:
			if(t.type == TokenType.BRACKET && t.data.equals("?>")) {
				this.mode = Mode.INIT;
			}
			break;
		case IN_INST_START	:
			if(t.type == TokenType.BRACKET && t.data.equals(">")) {
				this.mode = Mode.INIT;
			} else if(t.type == TokenType.BRACKET && t.data.equals("/>")) {
				this.mode = Mode.INIT;
			} else if(t.type == TokenType.WORD) {
				this.instClassName = t.data;
				this.mode = Mode.WAIT_ATTR;
			}
			break;
		case WAIT_ATTR	:
			if(t.type == TokenType.BRACKET && t.data.equals(">")) {
				this.mode = Mode.INIT;
			} else if(t.type == TokenType.BRACKET && t.data.equals("/>")) {
				this.mode = Mode.INIT;
			} else if(t.type == TokenType.WORD) {
				this.attr = t.data;
				this.mode = Mode.WAIT_EQ;
			}
			break;
		case WAIT_EQ	:
			if(t.type == TokenType.EQ) {
				this.mode = Mode.WAIT_VALUE;
			}
			break;
		case WAIT_VALUE	:
			if(t.type == TokenType.STRING) {
				// コントローラクラス指定
				if(this.attr.equals("fx:controller")) {
					// クラス名とパッケージを保存
					getHint().controllerClass = t.data;

				// インスタンスID指定
				} else if(this.attr.equals("fx:id")) {
					// IDを保存
					getHint().idClassMap.put(t.data, this.instClassName);

					// インスタンスのクラスパスをインポートから探して保存
					this.instClassPath = getClassPath(this.instClassName);
					if(getHint().usedClassList.contains(this.instClassPath) == false)
						getHint().usedClassList.add(this.instClassPath);

				// ハンドラ指定
				} else if(this.attr.startsWith("on")) {
					// 先頭の＃を除いてハンドル名を取得
					String hndl = t.data.substring(1, t.data.length());

					// インスタンスのクラスパスを保存
					this.instClassPath = getClassPath(this.instClassName);

					// イベントのクラス名とクラスパスを取得
					String eventClassPath = null;
					String eventClassName = null;
					try {
						String methodName = "getOn" + this.attr.substring(2);

						eventClassPath = Class.forName(this.instClassPath, false, this.getClass().getClassLoader()).getMethod(methodName).getGenericReturnType().getTypeName();
						int idx = eventClassPath.lastIndexOf(' ');
						if(idx == -1)
							idx = eventClassPath.lastIndexOf('<');
						eventClassPath = eventClassPath.substring(idx+1, eventClassPath.length()-1);
						eventClassName = getClassName(eventClassPath);
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}

					if(getHint().usedClassList.contains(eventClassPath) == false)
						getHint().usedClassList.add(eventClassPath);
					getHint().hndlList.add(new String[] {hndl, eventClassName});
				}

				this.mode = Mode.WAIT_ATTR;
			}
			break;
		case IN_INST_END	:
			if(t.type == TokenType.BRACKET && t.data.equals(">")) {
				this.mode = Mode.INIT;
			}
			break;
		}
	}

	private static String getClassName(String classPath) {
		return(classPath.substring(classPath.lastIndexOf('.')+1, classPath.length()));
	}

	private static String getPackage(String classPath) {
		return(classPath.substring(0, classPath.lastIndexOf('.')));
	}

	private String getClassPath(String className) {
		String classPath = null;

		for(String i : getHint().importList) {
			String path = null;

			// ワイルドカード指定のパスから探す
			if(i.endsWith(".*")) {
				try {
					path = getPackage(i) + "." + className;
					Class.forName(path, false, this.getClass().getClassLoader());
				}
				catch(Exception ex) {
					path = null;
				}
			// 一致するパスから探す
			} else {
				if(getClassName(i).equals(className))
					path = i;
			}

			// クラスパスが曖昧
			if(path != null && classPath != null)
				throw new RuntimeException("Class \"" + className + "\" path ambiguous.");

			if(path != null)
				classPath = path;
		}

		// クラスパスが見つからない
		if(classPath == null)
			throw new RuntimeException("Class \"" + className + "\" path not found.");

		return(classPath);
	}
}
