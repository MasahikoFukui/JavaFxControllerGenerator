package jp.fukui.controllergenerator.app;

import java.util.*;
import java.io.*;
import javafx.stage.*;
import javafx.application.*;
import jp.fukui.controllergenerator.fxml.*;
import jp.fukui.controllergenerator.preference.*;

public class App extends Application {
	@Override public void start(Stage primaryStage) {
		try {
			// ビューのイベントハンドラ設定
			getView().setObserver(new View.Observer() {
				@Override public void useEventObserver(boolean b)                       { useEventObserverHndl(b); }
				@Override public void folderDragOver(List<File> files, View.Acceptor a) { folderDragOverHndl(files, a); }
				@Override public void folderDropped(List<File> files)                   { folderDroppedHndl(files); }
				@Override public void directoryChanged()                                { directoryChangedHndl(); }
				@Override public void directoryReset()                                  { directoryResetHndl(); }
				@Override public void directoryRef()                                    { directoryRefHndl(); }
				@Override public void projectScan()                                     { projectScanHndl(); }
				@Override public void generate()                                        { generateHndl(); }
				@Override public void openWhole()                                       { openWholeHndl(); }
				@Override public void openIndividually()                                { openIndividuallyHndl(); }
				@Override public void about()                                           { aboutHndl(); }
				@Override public void controllerDragDetected(View.Acceptor a)           { controllerDragDetectedHndl(a); }
				@Override public void fxmlDragOver(List<File> files, View.Acceptor a)   { fxmlDragOverHndl(files, a); }
				@Override public void fxmlDropped(List<File> files)                     { fxmlDroppedHndl(files); }
				@Override public void appClosed()                                       { appClosedHndl(); }
			});

			// モデルのイベントハンドラ設定
			getModel().setObserver(new Model.Observer() {
				@Override public void fxmlDetected(File fxml, ControllerHint h, boolean isNew)  { fxmlDetectedHndl(fxml, h, isNew); }
				@Override public void finished()                                                { finishedHndl(); }
				@Override public void abort()                                                   { abortHndl(); }
				@Override public void directoryIsInappropriate(File dir, File file, String pkg) { directoryIsInappropriateHndl(dir, file, pkg); }
				@Override public void illegaiPackage(File file, String pkg)                     { illegaiPackageHndl(file, pkg); }
			});

			primaryStage.close();

			// 入力通知オブザーバ使用の初期設定
			if(getPreference().isOpenWhole())
				openWhole();
			else
				openIndividually();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	//------------------------------------------------------------------------------
	// ビューからの通知
	//------------------------------------------------------------------------------

	/** イベントオブザーバ使用可否 */
	public void useEventObserverHndl(boolean b) {
		getModel().setUseEventObserver(b);
		getPreference().setUseEventObserver(b);
	}

	/** 検索先へのフォルダドラッグオーバー */
	public void folderDragOverHndl(List<File> files, View.Acceptor a) {
		boolean b = false;
		if(files.size() == 1 && files.get(0).isDirectory())
			b = true;
		a.confirm(b);
	}

	/** 検索先へのフォルダドロップ */
	public void folderDroppedHndl(List<File> files) {
		File dir = files.get(0);
		getView().setDirectory(dir);
		getModel().setDirectory(dir);
		getPreference().setDirectory(dir);
	}

	/** 検索先パスの変更 */
	public void directoryChangedHndl() {
		File dir = getView().getDirectory();

		if(dir.exists()) {
			getView().setDirectoryStat(true);
			getView().setScanStat(true);
			getModel().setDirectory(dir);
			getPreference().setDirectory(dir);
			this.directoryAnchor = getView().getDirectoryAnchor();
			this.directoryCaret  = getView().getDirectoryCaret();
		} else {
			getView().setDirectoryStat(false);
			getView().setScanStat(false);
		}
	}

	/** 検索先パスのリセット */
	public void directoryResetHndl() {
		int anchor = this.directoryAnchor;
		int caret  = this.directoryCaret;
		getView().setDirectory(getModel().getDirectory());
		getView().setDirectoryCaret(anchor, caret);
		this.directoryAnchor = anchor;
		this.directoryCaret  = caret;
	}

	/** 検索先を参照 */
	public void directoryRefHndl() {
		File dir = getView().getDirectory();
		if(dir.exists() == false)
			dir = getModel().getDirectory();
		dir = getView().openDirectoryChooser(dir);
		if(dir != null) {
			getView().setDirectory(dir);
			getModel().setDirectory(dir);
			getPreference().setDirectory(dir);
		}
	}

	/** FXMLスキャンボタンクリック */
	public void projectScanHndl() {
		getView().clearConsole();
		getView().openResults();
		getModel().scanPackage();
	}

	/** コントローラ生成 */
	public void generateHndl() {
		List<Boolean> list = getView().getControllerSelectList();

		for(int i = 0; i < list.size(); i++) {
			if(list.get(i)) {
				ControllerHint h = getModel().getHint().get(i);

				File dst        = h.getControllerFile();
				File dstFull    = new File(getModel().getDirectory(), h.getControllerFile().getPath());
				File parent     = dst.getParentFile();
				File parentFull = dstFull.getParentFile();
				if(parentFull.exists() == false) {
					if(getView().openConfirm_directoryCreate(parent)) {
						boolean result = parentFull.mkdirs();
						if(result == false)
							getView().openAlert_DirectoryCreationFailed(parent);
						getView().putMessage_DirectoryCreationFailed(parent);
					}
				}

				getModel().generateController(h);
				getView().putMessage_ControllerGenerated(dst);
			}
		}
	}

	/** 標準画面切り替え */
	private void openWholeHndl() {
		openWhole();
	}

	/** コントローラ個別生成画面切り替え */
	private void openIndividuallyHndl() {
		openIndividually();
	}

	/** アプリケーション終了 */
	private void aboutHndl() {
		getView().openAbout();
	}

	/** コントローラファイルのドラッグ検出 */
	private void controllerDragDetectedHndl(View.Acceptor a) {
		List<File> controllers = getModel().getControllers();
		boolean accept = controllers.size() > 0;
		a.confirm(accept,  controllers);
	}

	/** FXMLファイルのドラッグオーバー */
	private void fxmlDragOverHndl(List<File> files, View.Acceptor a) {
		boolean b = true;
		if(files.size() > 0) {
			for(File f : files) {
				if(Model.isFxml(f) == false) {
					b = false;
					break;
				}
			}
		}

		a.confirm(b);
	}

	/** FXMLファイルのドロップ */
	private void fxmlDroppedHndl(List<File> files) {
		try {
			getView().setSrcImage(files.size());
			getModel().generate(files);
			getView().setDstImage(getModel().getControllers().size());
		}
		catch(Model.ControllerDuplicateException ex) {
			getView().openControllerDuplicate(ex.file);
			getModel().clearControllers();
		}
	}

	/** アプリケーション終了 */
	private void appClosedHndl() {
		getPreference().close();
	}

	//------------------------------------------------------------------------------
	// 検索先パスのアンカーとキャレット
	//------------------------------------------------------------------------------

	private int directoryAnchor = 0;
	private int directoryCaret  = 0;

	//------------------------------------------------------------------------------
	// モデルからの通知
	//------------------------------------------------------------------------------

	/** FXMLファイル検出 */
	private void fxmlDetectedHndl(File fxml, ControllerHint h, boolean isNew) {
		try {
			File dst = h.getControllerFile();
			getView().addResult(dst, isNew);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/** パッケージスキャン完了 */
	private void finishedHndl() {
		getView().putMessage_Finished();
	}

	/** パッケージスキャン中断*/
	private void abortHndl() {
		getView().putMessage_Aborted();
		getView().closeResults();
	}

	/**
	 * パッケージ最上位ディレクトリとソースファイルのパッケージ指定の不一致検出
	 * 　（注：ディレクトリ指定が誤っている可能性あり）
	 */
	private void directoryIsInappropriateHndl(File dir, File file, String pkg) {
		file = new File(file.getPath().substring(dir.getPath().length()));
		getView().putMessage_DirectoryIsInappropriate(dir, file, pkg);
		getView().closeResults();
	}

	/** ソースのパッケージ指定の誤り検出 */
	private void illegaiPackageHndl(File file, String pkg) {
		file = new File(file.getPath().substring(getModel().getDirectory().getPath().length()));
		getView().putMessage_IllegaiPackage(file, pkg);
	}

	//------------------------------------------------------------------------------
	// パッケージ全体画面
	//------------------------------------------------------------------------------

	private void openWhole() {
		getView().closeIndividually();
		getView().openWhole();

		// 検索先の初期設定
		File dir = getPreference().getDirectory();
		if(dir == null)
			dir = new File(".");
		getView().setDirectory(dir);
		getModel().setDirectory(dir);

		// 入力通知オブザーバ使用の初期設定
		getView().setUseEventObserver(getPreference().isUseEventObserver());
		getModel().setUseEventObserver(getPreference().isUseEventObserver());

		getPreference().setOpenWhole(true);
	}

	private void openIndividually() {
		getView().closeWhole();
		getView().openIndividually();

		// 入力通知オブザーバ使用の初期設定
		getView().setUseEventObserver(getPreference().isUseEventObserver());
		getModel().setUseEventObserver(getPreference().isUseEventObserver());

		getPreference().setOpenWhole(false);
	}

	//------------------------------------------------------------------------------
	// 初期設定
	//------------------------------------------------------------------------------

	/** 初期設定インスタンス */
	private Preference preference = new Preference();
	/** 初期設定インスタンス参照 */
	private Preference getPreference() { return(this.preference); }

	//------------------------------------------------------------------------------
	// ビューとモデル
	//------------------------------------------------------------------------------

	/** ビュー */
	private View view = new View();
	/** ビュー参照 */
	private View getView() { return(this.view); }

	/** モデル */
	private Model model = new Model();
	/** モデル参照 */
	private Model getModel() { return(this.model); }

	//------------------------------------------------------------------------------
	// 動作
	//------------------------------------------------------------------------------

	public static void main(String[] args) {
		launch(args);
	}
}
