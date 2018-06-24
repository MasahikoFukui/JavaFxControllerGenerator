package jp.fukui.controllergenerator.java;

import java.io.*;
import java.util.ArrayList;
import jp.fukui.controllergenerator.pattern.*;

/** Javaソース解析クラス */
public class JavaAnalyzer implements Pattern.Observer {
	//------------------------------------------------------------------------------
	// 生成
	//------------------------------------------------------------------------------

	public JavaAnalyzer() {
		addPattern(new Pattern_Eof(this));
		addPattern(new Pattern_KeyWord(this, TokenType.SPACE, " "));
		addPattern(new Pattern_KeyWord(this, TokenType.SPACE, "\t"));
		addPattern(new Pattern_KeyWord(this, TokenType.NL, "\r\n"));
		addPattern(new Pattern_KeyWord(this, TokenType.NL, "\n"));
		addPattern(new Pattern_KeyWord(this, TokenType.NL, "\r"));
		addPattern(new Pattern_KeyWord(this, TokenType.COMMENT_OPEN, "/**"));
		addPattern(new Pattern_KeyWord(this, TokenType.COMMENT_OPEN, "/*"));
		addPattern(new Pattern_KeyWord(this, TokenType.COMMENT_OPEN, "*/"));
		addPattern(new Pattern_KeyWord(this, TokenType.COMMENT_LINE, "///"));
		addPattern(new Pattern_Word(this));
		addPattern(new Pattern_UnknownChar(this));
	}

	//------------------------------------------------------------------------------
	// パッケージ
	//------------------------------------------------------------------------------

	/** パッケージ名 */
	private String sourcePackage = "";
	/** パッケージ名の参照 */
	public String getSourcePackage() { return(this.sourcePackage); }
	/** パッケージ名の設定 */
	protected void   setSourcePackage(String p) { this.sourcePackage = p; }

	//------------------------------------------------------------------------------
	// パターン
	//------------------------------------------------------------------------------

	private ArrayList<Pattern> patList = new ArrayList<Pattern>();
	private Pattern            eofPat  = new Pattern_Eof(this);
	public void addPattern(Pattern pat) { this.patList.add(pat); }

	//------------------------------------------------------------------------------
	// 行番号
	//------------------------------------------------------------------------------

	int line = 1;
	private void resetLine() { this.line = 1; }
	private void incrementLine() { this.line++; }
	private int getLine() { return(this.line); }

	//------------------------------------------------------------------------------
	// 字句解析
	//------------------------------------------------------------------------------

	public void analyze(File src) {
		setSourcePackage("");
		resetLine();

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
	}

	//------------------------------------------------------------------------------
	// 構文解析
	//------------------------------------------------------------------------------

	private enum Mode {
		INIT,
		IN_COMMENT,
		IN_COMMENT_LINE,
		WAIT_PACKAGE_NAME,
		ERROR,
		END,
	}
	private Mode mode = Mode.INIT;

	public void tokenDetected(Token t) {
		if(t.type == TokenType.EOF)
			return;

		if(t.type == TokenType.NL)
			incrementLine();

		switch(this.mode) {
		case INIT	:
			if(t.type == TokenType.UK) {
				this.mode = Mode.ERROR;
			} else if(t.type == TokenType.STRING) {
				this.mode = Mode.ERROR;
			} else if(t.type == TokenType.COMMENT_OPEN) {
				this.mode = Mode.IN_COMMENT;
			} else if(t.type == TokenType.COMMENT_LINE) {
				this.mode = Mode.IN_COMMENT_LINE;
			} else if(t.type == TokenType.WORD && t.data.equals("package")) {
				this.mode = Mode.WAIT_PACKAGE_NAME;
			} else if(t.type == TokenType.WORD) {
				this.mode = Mode.END;
			}
			break;
		case IN_COMMENT	:
			if(t.type == TokenType.COMMENT_CLOSE) {
				this.mode = Mode.INIT;
			}
			break;
		case IN_COMMENT_LINE	:
			if(t.type == TokenType.NL) {
				this.mode = Mode.INIT;
			}
			break;
		case WAIT_PACKAGE_NAME	:
			if(t.type == TokenType.WORD) {
				setSourcePackage(t.data);
				this.mode = Mode.END;

			} else if(t.type == TokenType.UK) {
				this.mode = Mode.ERROR;
			}
			break;
		case ERROR	:
			break;
		case END	:
			break;
		}
	}
}
