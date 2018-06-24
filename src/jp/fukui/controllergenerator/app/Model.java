package jp.fukui.controllergenerator.app;

import java.io.*;
import java.util.*;
import jp.fukui.controllergenerator.fxml.*;
import jp.fukui.controllergenerator.java.*;

public class Model {
	//------------------------------------------------------------------------------
	// オブザーバ
	//------------------------------------------------------------------------------

	public interface Observer {
		/** FXMLファイルの検出 */
		public void fxmlDetected(File fxml, ControllerHint h, boolean isNew);
		/** 完了通知 */
		public void finished();
		/** エラーにより中断 */
		public void abort();
		/** ディレクトリ指定が適当ではない */
		public void directoryIsInappropriate(File dir, File file, String pkg);
		/** ソースのパッケジ指定が誤り */
		public void illegaiPackage(File src, String pkg);
	}

	/** オブザーバインスタンス */
	private Observer o = null;
	/** オブザーバインスタンス設定 */
	public  void     setObserver(Observer o) { this.o = o; }
	/** オブザーバインスタンス参照 */
	private Observer getObserver()           { return(this.o); }

	//------------------------------------------------------------------------------
	// コントローラ生成ヒント
	//------------------------------------------------------------------------------

	/** コントローラ生成ヒントのリスト */
	private ArrayList<ControllerHint> hint = new ArrayList<ControllerHint>();
	/** コントローラ生成ヒントのリスト参照 */
	public ArrayList<ControllerHint> getHint() { return(this.hint); }

	//------------------------------------------------------------------------------
	// 推定パッケージパス
	//------------------------------------------------------------------------------

	/** ディレクトリ名から推定されるパッケージ */
	private StringBuffer estimatedPackage = new StringBuffer();
	/** ディレクトリ名から推定されるパッケージをリセット */
	private void resetEstimatedPackage() { this.estimatedPackage.setLength(0); }
	/** ディレクトリ名から推定されるパッケージを参照 */
	private StringBuffer getEstimatedPackage() { return(this.estimatedPackage); }

	//------------------------------------------------------------------------------
	// スキャンの中断
	//------------------------------------------------------------------------------

	/** 中断フラグ */
	private boolean interrupt = false;
	/** 中断フラグのリセット */
	private void resetInterrupt() { this.interrupt = false; }
	/** 中断フラグのセット */
	private void setInterrupt()   { this.interrupt = true; }
	/** 中断フラグの参照 */
	private boolean isInterrupt() { return(this.interrupt); }

	//------------------------------------------------------------------------------
	// スキャン
	//------------------------------------------------------------------------------

	/** パッケージ内のスキャン */
	public void scanPackage() {
		try {
			resetInterrupt();
			getHint().clear();

			resetEstimatedPackage();
			if(scanJava(getDirectory()) == false) {
				if(getObserver() != null)
					getObserver().abort();
					return;
			}

			scanFxml(getDirectory());
			if(getObserver() != null)
				getObserver().finished();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/** Javaファイルのスキャン */
	private boolean scanJava(File file) {
		if(isInterrupt())
			return(false);

		// ファイル
		if(file.isFile()) {
			if(isSource(file)) {
				JavaAnalyzer analyzer = new JavaAnalyzer();
				analyzer.analyze(file);

				String sourcePackage = analyzer.getSourcePackage();

				if(getEstimatedPackage().toString().equals(sourcePackage) == false) {
					if(sourcePackage.endsWith(getEstimatedPackage().toString()) == false || getEstimatedPackage().toString().endsWith(sourcePackage) == false) {
						// 検索開始位置はパッケージの最上位ディレクトリではない
						if(getObserver() != null)
							getObserver().directoryIsInappropriate(getDirectory(), file, sourcePackage);
						setInterrupt();
						return(false);
					} else {
						// ファイルのパッケージ指定に誤りがある
						if(getObserver() != null)
							getObserver().illegaiPackage(file, sourcePackage);
						return(false);
					}
				}
			}

		// ディレクトリ
		} else {
			// ディレクトリメンバをサーチ
			for(File f : file.listFiles()) {
				if(isInterrupt())
					return(false);

				if(f.isDirectory()) {
					if(getEstimatedPackage().length() > 0)
						getEstimatedPackage().append(".");
					getEstimatedPackage().append(f.getName());
				}

				scanJava(f);

				if(f.isDirectory()) {
					int idx = getEstimatedPackage().lastIndexOf(".");
					if(idx == -1)
						idx = 0;
					getEstimatedPackage().setLength(idx);
				}
			}
		}

		return(true);
	}

	/** FXMLファイルのスキャン */
	private void scanFxml(File file) {
		// 引数がファイル
		if(file.isFile()) {
			// ファイルがFXMLの場合
			if(isFxml(file)) {
				// FXMLを解析
				FxmlAnalyzer analyzer = new FxmlAnalyzer();
				ControllerHint h = analyzer.analyze(file);
				getHint().add(h);
				if(getObserver() != null) {
					String parent = h.getControllerFile().getParent();
					if(parent == null)
						parent = ".";
					File dstFull = new File(getDirectory(), parent);
					getObserver().fxmlDetected(file, h, !dstFull.exists());
				}
			}

		// 引数がディレクトリ
		} else {
			// ディレクトリメンバをサーチ
			for(File f : file.listFiles())
				scanFxml(f);
		}
	}

	//------------------------------------------------------------------------------
	// コントローラの生成
	//------------------------------------------------------------------------------

	/** コントローラの生成 */
	public void generateController(ControllerHint h) {
		ControllerGenerator generator = new ControllerGenerator();
		File dst = new File(getDirectory(), h.getControllerFile().getPath());
		generator.output(dst, h, isUseEventObserver());
	}

	//------------------------------------------------------------------------------
	// イベント通知オブザーバの組み込み
	//------------------------------------------------------------------------------

	/** イベント通知オブザーバの組み込み可否 */
	private boolean useEventObserver = false;
	/** イベント通知オブザーバの組み込み可否を設定 */
	public void setUseEventObserver(boolean b) { this.useEventObserver = b; }
	/** イベント通知オブザーバの組み込み可否を参照 */
	public boolean isUseEventObserver() { return(this.useEventObserver); }

	//------------------------------------------------------------------------------
	// ファイルチェック
	//------------------------------------------------------------------------------

	/** FXMLファイルの判定 */
	public static boolean isFxml(File f) {
		return(f.isFile() && f.getName().endsWith(".fxml"));
	}

	/** Javaソースファイルの判定 */
	public static boolean isSource(File f) {
		return(f.isFile() && f.getName().endsWith(".java"));
	}

	//------------------------------------------------------------------------------
	// 検索開始ディレクトリ
	//------------------------------------------------------------------------------

	/** 検索開始ディレクトリ */
	private File directory = null;
	/** 検索開始ディレクトリの設定 */
	public void setDirectory(File d) { this.directory = d; }
	/** 検索開始ディレクトリの参照 */
	public File getDirectory() { return(this.directory); }

	//------------------------------------------------------------------------------
	// コントローラの個別生成
	//------------------------------------------------------------------------------

	private ArrayList<File> controllers = new ArrayList<File>();
	private File dstDir = new File("controllers/");

	/** コントローラ生成 */
	public void generate(List<File> files) {
		// コントローラの生成先を初期化
		dstDir.mkdirs();
		this.clearControllers();

		// コントローラの生成
		for(File f : files) {
			FxmlAnalyzer analyzer = new FxmlAnalyzer();
			ControllerHint h = analyzer.analyze(f);
			// 保存先は"controllers"の直下
			File dst = new File(dstDir, h.getControllerFile().getPath());
			controllers.add(dst);
			if(dst.exists())
				throw new ControllerDuplicateException(dst);

			dst.getParentFile().mkdirs();
			ControllerGenerator generator = new ControllerGenerator();
			generator.output(dst, h, isUseEventObserver());
		}
	}

	/** 生成したコントローラの取得 */
	public List<File> getControllers() {
		return(this.controllers);
	}

	public void clearControllers() {
		if(dstDir.exists()) {
			for(File f : dstDir.listFiles())
				rmdir(f);
		}

		this.controllers.clear();
	}

	private static void rmdir(File file) {
		if(file.isDirectory()) {
			for(File f : file.listFiles())
				rmdir(f);
		}
		file.delete();
	}

	public static class ControllerDuplicateException extends RuntimeException {
		public File file;

		public ControllerDuplicateException(File file) {
			this.file = file;
		}
	}
}
