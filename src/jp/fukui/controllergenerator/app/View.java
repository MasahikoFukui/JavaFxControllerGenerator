package jp.fukui.controllergenerator.app;

import java.io.*;
import java.util.*;
import javafx.stage.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.image.*;

/** ビュー */
public class View {
	//------------------------------------------------------------------------------
	// オブザーバ
	//------------------------------------------------------------------------------

	/** 処理の受付確認 */
	public interface Acceptor  {
		public void confirm(boolean b, Object...param);
	}

	/** オブザーバ */
	public interface Observer {
		public void useEventObserver(boolean b);
		public void folderDragOver(List<File> files, Acceptor a);
		public void folderDropped(List<File> files);
		public void directoryChanged();
		public void directoryReset();
		public void directoryRef();
		public void projectScan();
		public void generate();
		public void openWhole();
		public void openIndividually();
		public void about();
		public void controllerDragDetected(Acceptor a);
		public void fxmlDragOver(List<File> files, Acceptor a);
		public void fxmlDropped(List<File> files);
		public void appClosed();
	}

	/** オブザーバインスタンス */
	private Observer observer = null;
	/** オブザーバインスタンス設定 */
	public void setObserver(Observer o) { this.observer = o; }
	/** オブザーバインスタンス参照 */
	public Observer getObserver() { return(this.observer); }

	//------------------------------------------------------------------------------
	// イベントハンドラ
	//------------------------------------------------------------------------------

	/** 参照ボタンクリック */
	public void refClickedHndl(ActionEvent e) {
		if(getObserver() != null)
			getObserver().directoryRef();
	}

	/** イベントオブザーバ組み込み可否のチェック操作 */
	public void useEventObserverCheckedHndl(ActionEvent e) {
		if(getObserver() != null)
			getObserver().useEventObserver(isUseEventObserver());
	}

	/** パッケージ最上位ディレクトリパスへのフォルダドラッグオーバ */
	public void folderDragOverHndl(DragEvent e) {
		if(e.getDragboard().hasFiles()) {
			Acceptor a = new Acceptor() {
				public void confirm(boolean b, Object...param) {
					e.acceptTransferModes(TransferMode.COPY);
				}
			};

			if(getObserver() != null)
				getObserver().folderDragOver(e.getDragboard().getFiles(), a);
		}
	}

	/** パッケージ最上位ディレクトリパスへのフォルダドロップ */
	public void folderDroppedHndl(DragEvent e) {
		if(getPrimaryStage() != null)
			getObserver().folderDropped(e.getDragboard().getFiles());
	}

	/** パッケージスキャンボタンクリック */
	public void scanClickedHndl(ActionEvent e) {
		if(getPrimaryStage() != null)
			getObserver().projectScan();
	}

	/** パッケージ最上位ディレクトリパスへのキー入力 */
	public void directoryKeyPressedHndl(KeyEvent e) {
		if(e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
			if(getObserver() != null)
				getObserver().directoryChanged();
		}
		if(e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER) {
			if(getObserver() != null)
				getObserver().directoryReset();
		}
	}

	/** パッケージ最上位ディレクトリパスへのマウスクリック */
	public void directoryMouseClickedHndl(MouseEvent e) {
		if(getObserver() != null)
			getObserver().directoryChanged();
	}

	/** パッケージ最上位ディレクトリパスの変更 */
	public void directoryChangedHndl() {
		if(getPrimaryStage() != null)
			getObserver().directoryChanged();
	}

	/** 「閉じる」メニュー */
	public void closeMenuHndl() {
		if(getPrimaryStage() != null)
			getPrimaryStage().close();
		if(getObserver() != null)
			getObserver().appClosed();
	}

	/** 「パッケージ全体画面オープン」メニュー */
	public void openWholeMenuHndl() {
		if(getObserver() != null)
			getObserver().openWhole();
	}

	/** 「コントローラの個別生成画面オープン」メニュー */
	public void openIndividuallyMenuHndl() {
		if(getObserver() != null)
			getObserver().openIndividually();
	}

	/** 「このアプリについて」メニュー */
	public void aboutMenuHndl() {
		if(getObserver() != null)
			getObserver().about();
	}

	/** コントローラ生成ボタンクリック */
	public void resultGenerateClickedHndl(ActionEvent e) {
		getObserver().generate();
		closeResults();
	}

	/** キャンセルボタンクリック */
	public void resultCancelClickedHndl(ActionEvent e) {
		closeResults();
	}

	/** 全選択ボタンクリック */
	public void resultSelectAllClickedHndl(ActionEvent e) {
		ListView<CheckBox> resultsList = getResultsController().getControllerList();
		for(CheckBox c : resultsList.getItems())
			c.setSelected(true);
	}

	/** 全選択解除ボタンクリック */
	public void resultSelectAllClearClickedHndl(ActionEvent e) {
		ListView<CheckBox> resultsList = getResultsController().getControllerList();
		for(CheckBox c : resultsList.getItems())
			c.setSelected(false);
	}

	/** ステージクローズ */
	private void stageClosedHndl() {
		if(getObserver() != null)
			getObserver().appClosed();
	}

	/** コントローラアイコンのドラッグ検出 */
	private void individuallyControllerDragDetectedHndl(MouseEvent e) {
		Acceptor a = new Acceptor() {
			public void confirm(boolean b, Object...param) {
				List<File> files = (List<File>) param[0];
				if(files.size() > 0) {
					Node source = (Node) e.getSource();
					Dragboard db = source.startDragAndDrop(TransferMode.COPY);

					ClipboardContent content = new ClipboardContent();
					content.putFiles(files);
					db.setContent(content);
			        e.consume();
				}
			}
		};

		if(getObserver() != null)
			getObserver().controllerDragDetected(a);
	}

	/** FXMLアイコンのドラッグオーバー */
	private void individuallyFxmlDragOverHndl(DragEvent e) {
		if(e.getDragboard().hasFiles()) {
			Acceptor a = new Acceptor() {
				public void confirm(boolean b, Object...param) {
					if(b)
						e.acceptTransferModes(TransferMode.COPY);
				}
			};

			if(getObserver() != null)
				getObserver().fxmlDragOver(e.getDragboard().getFiles(), a);
		}
	}

	/** FXMLファイルのドロップ */
	private void individuallyFxmlDroppedHndl(DragEvent e) {
		getObserver().fxmlDropped(e.getDragboard().getFiles());
	}

	//------------------------------------------------------------------------------
	// ダイアログ
	//------------------------------------------------------------------------------

	/** 生成するオントローラ名の重複を警告 */
	public void openControllerDuplicate(File file) {
		String msg = "コントローラ \"" + file + "\" の名称が重複しました。";
		Alert eAlert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK);
		eAlert.showAndWait().orElse( ButtonType.CANCEL);
	}

	/** ディレクトリ生成の意思確認 */
	public boolean openConfirm_directoryCreate(File dir) {
		String msg = "コントローラの保存先 \"" + dir + "\" が存在しません。作成きしますか？";
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES , ButtonType.NO);
		ButtonType button  = alert.showAndWait().orElse(ButtonType.CANCEL);
		return(button == ButtonType.YES);
	}

	/** ディレクトリ生成の失敗を警告 */
	public void openAlert_DirectoryCreationFailed(File dir) {
		String eMsg = "\"" + dir + "\" の作成に失敗しました。";
		Alert eAlert = new Alert(Alert.AlertType.CONFIRMATION, eMsg, ButtonType.OK);
		eAlert.showAndWait().orElse( ButtonType.CANCEL);
	}

	/** ディレクトリチューザ */
	public File openDirectoryChooser(File dir) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("パッケージ最上位ディレクトリ");
		chooser.setInitialDirectory(dir);
		dir = chooser.showDialog(getPrimaryScene().getWindow());
		return(dir);
	}

	//------------------------------------------------------------------------------
	// パッケージ全体画面
	//------------------------------------------------------------------------------

	/** パッケージ全体画面のオープン */
	public void openWhole() {
		try {
			setPrimaryStage(new Stage());

			// タイトルセット
			getPrimaryStage().setTitle("コントローラ生成ツール");

			// ステージクローズ時の処理ハンドラ定義
			getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent e) {
					closeWhole();
				}
			});

			// シーンの生成
			FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
			Scene scene = new Scene(loader.load());
			setPrimaryScene(scene);

			// JavaFXコントローラの取得
			setWholeController(loader.getController());

			// コントローラ由来のイベントハンドラの設定
			getWholeController().setObserver(new AppController.Observer() {
				@Override public void useEventObserverChecked(ActionEvent e) { useEventObserverCheckedHndl(e); }
				@Override public void folderDropped(DragEvent e)             { folderDroppedHndl(e); }
				@Override public void folderDragOver(DragEvent e)            { folderDragOverHndl(e); }
				@Override public void directoryKeyPressed(KeyEvent e)        { directoryKeyPressedHndl(e); }
				@Override public void refClicked(ActionEvent e)              { refClickedHndl(e); }
				@Override public void scanClicked(ActionEvent e)             { scanClickedHndl(e); }
				@Override public void closeMenu(ActionEvent e)               { closeMenuHndl(); }
				@Override public void openIndividuallyMenu(ActionEvent e)    { openIndividuallyMenuHndl(); }
				@Override public void aboutMenu(ActionEvent e)               { aboutMenuHndl(); }
				@Override public void directoryMouseClicked(MouseEvent e)    { directoryMouseClickedHndl(e); }
			});

			// 検索先パス変更時の処理ハンドラ定義
			getWholeController().getDirectory().textProperty().addListener(new ChangeListener<String>() {
				@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					directoryChangedHndl(); }
			});

			// ステージクローズ時の処理ハンドラ定義
			getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent e) { stageClosedHndl(); }
			});

			// ステージの表示
			getPrimaryStage().setScene(scene);
			getPrimaryStage().show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/** パッケージ全体画面を閉じる */
	public void closeWhole() {
		if(getPrimaryStage() != null)
			getPrimaryStage().close();
		setPrimaryStage(null);
		setWholeController(null);
	}

	//------------------------------------------------------------------------------
	// プロジェクトのスキャン結果
	//------------------------------------------------------------------------------

	/** 結果の表示画面を開く */
	public void openResults() {
		if(getResultsStage() != null)
			return;

		try {
			setResultsStage(new Stage());

			// タイトルセット
			getResultsStage().setTitle("プロジェクトの検索結果");

			// ステージクローズ時の処理ハンドラ定義
			getResultsStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent e) {
					closeResults();
				}
			});

			// シーンの生成
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Results.fxml"));
			Scene scene = new Scene(loader.load());

			// JavaFXコントローラの取得
			setResultsController(loader.getController());

			// コントローラ由来のイベントハンドラの設定
			getResultsController().setObserver(new ResultsController.Observer() {
				@Override public void generateClicked(ActionEvent e)       { resultGenerateClickedHndl(e); }
				@Override public void cancelClicked(ActionEvent e)         { resultCancelClickedHndl(e); }
				@Override public void selectAllClicked(ActionEvent e)      { resultSelectAllClickedHndl(e); }
				@Override public void selectAllClearClicked(ActionEvent e) { resultSelectAllClearClickedHndl(e); }
			});

			// ステージの表示
			getResultsStage().setScene(scene);
			getResultsStage().initModality(Modality.WINDOW_MODAL);
			getResultsStage().initOwner(getPrimaryScene().getWindow());
			getResultsStage().show();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/** 結果の表示画面を閉じる */
	public void closeResults() {
		if(getResultsStage() != null)
			getResultsStage().close();
		setResultsStage(null);
		setResultsController(null);
	}

	//------------------------------------------------------------------------------
	// コントローラの個別生成
	//------------------------------------------------------------------------------

	/** 結果の表示画面を開く */
	public void openIndividually() {
		try {
			setPrimaryStage(new Stage());

			// タイトルセット
			getPrimaryStage().setTitle("コントローラの個別生成");

			// ステージクローズ時の処理ハンドラ定義
			getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent e) { stageClosedHndl(); }
			});

			// シーンの生成
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Individually.fxml"));
			Scene scene = new Scene(loader.load());
			setPrimaryScene(scene);

			// JavaFXコントローラの取得
			setIndividuallyController(loader.getController());

			// コントローラ由来のイベントハンドラの設定
			getIndividuallyController().setObserver(new IndividuallyController.Observer() {
				@Override public void useEventObserverChecked(ActionEvent e) { useEventObserverCheckedHndl(e); }
				@Override public void controllerDragDetected(MouseEvent e)   { individuallyControllerDragDetectedHndl(e); }
				@Override public void fxmlDropped(DragEvent e)               { individuallyFxmlDroppedHndl(e); }
				@Override public void fxmlDragOver(DragEvent e)              { individuallyFxmlDragOverHndl(e); }
				@Override public void closeMenu(ActionEvent e)               { closeMenuHndl(); }
				@Override public void openWholeMenu(ActionEvent e)           { openWholeMenuHndl(); }
				@Override public void aboutMenu(ActionEvent e)               { aboutMenuHndl(); }
			});

			// ステージの表示
			getPrimaryStage().setScene(scene);
			getPrimaryStage().show();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/** 結果の表示画面を閉じる */
	public void closeIndividually() {
		if(getPrimaryStage() != null)
			getPrimaryStage().close();
		setPrimaryStage(null);
		setIndividuallyController(null);
	}

	public void aboutCloseHndl() {
		closeAbout();
	}

	//------------------------------------------------------------------------------
	// アバウトボックス
	//------------------------------------------------------------------------------

	/** アバウト画面を開く */
	public void openAbout() {
		if(getAboutStage() != null)
			return;

		try {
			setAboutStage(new Stage());

			// タイトルセット
			getAboutStage().setTitle("このアプリケーションについて");

			// シーンの生成
			FXMLLoader loader = new FXMLLoader(getClass().getResource("About.fxml"));
			Scene scene = new Scene(loader.load());

			// JavaFXコントローラの取得
			setAboutController(loader.getController());

			// コントローラ由来のイベントハンドラの設定
			getAboutController().setObserver(new AboutController.Observer() {
				@Override public void closeClicked(ActionEvent e) { aboutCloseHndl(); }
			});

			// ステージの表示
			getAboutStage().setScene(scene);
			getAboutStage().setResizable(false);
			getAboutStage().initModality(Modality.WINDOW_MODAL);
			getAboutStage().initOwner(getPrimaryScene().getWindow());
			getAboutStage().show();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/** 結果の表示画面を閉じる */
	public void closeAbout() {
		if(getAboutStage() != null)
			getAboutStage().close();
		setAboutStage(null);
		setAboutController(null);
	}

	//------------------------------------------------------------------------------
	// コンソール出力
	//------------------------------------------------------------------------------

	/** コンソールへの出力 */
	private void writeConsole(String s) {
		getWholeController().getConsole().appendText(s);
		getWholeController().getConsole().appendText("\n");
	}

	/** コントロール生成時のメッセージ出力 */
	public void putMessage_ControllerGenerated(File file) {
		writeConsole("コントローラ \"" + file.getPath() + "\" を生成しました。");
	}

	/** コントロールの保存先ディレクトリ生成失敗時のメッセージ出力 */
	public void putMessage_DirectoryCreationFailed(File dir) {
		writeConsole("\"" + dir.getPath() + "\" の作成に失敗しました。");
	}

	/** パッケージスキャン完了時のメッセージ出力 */
	public void putMessage_Finished() {
		writeConsole("--- パッケージスキャン完了 ---");
	}

	/** パッケージスキャン中断時のメッセージ出力 */
	public void putMessage_Aborted() {
		writeConsole("--- パッケージスキャン中断 ---");
	}

	/** パッケージ最上位ディレクトリの誤り検出時のメッセージ出力 */
	public void putMessage_DirectoryIsInappropriate(File dir, File file, String pkg) {
		writeConsole("ファイル \"" + file + "\" のパッケージ \"" + pkg + "\" は指定されたディレクトリと一致しません。");
		writeConsole("入力で指定されたディレクトリ \"" + dir + "\" が正しいか確認してください。");
	}

	/** Javaソースのパッケージ指定誤り検出時のメッセージ出力 */
	public void putMessage_IllegaiPackage(File file, String pkg) {
		writeConsole("ファイル \"" + file.getPath() + "\" のパスとパッケージ指定 \"" + pkg + "\" が一致しません。");
	}

	//------------------------------------------------------------------------------
	// プロジェクトのスキャン結果
	//------------------------------------------------------------------------------

	/** スキャン結果からコントローラ生成可否リストを取得 */
	public List<Boolean> getControllerSelectList() {
		ObservableList<CheckBox> items = getResultsController().getControllerList().getItems();
		ArrayList<Boolean> selectList = new ArrayList<Boolean>();

		if(getResultsController() != null) {
			for(CheckBox c : items)
				selectList.add(c.isSelected());
		}

		return(selectList);
	}

	/** スキャン結果を追加 */
	public void addResult(File dst, boolean selected) {
		if(getResultsController() == null)
			return;
		CheckBox item = new CheckBox();
		item.setSelected(selected);
		item.setText(dst.getPath());
		getResultsController().getControllerList().getItems().add(item);
	}

	//------------------------------------------------------------------------------
	// その他表示関連操作
	//------------------------------------------------------------------------------

	/** コントロール個別生成のソースアイコン設定 */
	public void setSrcImage(int nofFile) {
		Image image = null;
		switch(nofFile) {
		case 0	:	image = new Image("image/src0.png"); break;
		case 1	:	image = new Image("image/src1.png"); break;
		default	:	image = new Image("image/src2.png"); break;
		}
		getIndividuallyController().getSrcImage().setImage(image);
	}

	/** コントロール個別生成の出力アイコン設定 */
	public void setDstImage(int nofFile) {
		Image image = null;
		switch(nofFile) {
		case 0	:	image = null;
		case 1	:	image = new Image("image/dst1.png"); break;
		default	:	image = new Image("image/dst2.png"); break;
		}
		getIndividuallyController().getDstImage().setImage(image);
	}

	/** スキャン結果を追加 */
	public void setDirectoryStat(boolean b) {
		if(b)
			getWholeController().getDirectory().setStyle("-fx-background-color: white;");
		else
			getWholeController().getDirectory().setStyle("-fx-background-color: red;");
	}

	public void setScanStat(Boolean b) {
		getWholeController().getScan().setDisable(!b);
	}

	public File getDirectory() {
		return(new File(getWholeController().getDirectory().getText()));
	}

	public void setDirectory(File f) {
		getWholeController().getDirectory().setText(f.getPath());
	}

	public int getDirectoryAnchor() {
		return(getWholeController().getDirectory().getCaretPosition());
	}

	public int getDirectoryCaret() {
		return(getWholeController().getDirectory().getCaretPosition());
	}

	public void setDirectoryCaret(int a, int c) {
		getWholeController().getDirectory().selectRange(a, c);
	}

	public boolean isUseEventObserver() {
		if(getWholeController() != null) {
			return(getWholeController().getUseEventObserver().isSelected());
		} else if(getIndividuallyController() != null) {
			return(getIndividuallyController().getUseEventObserver().isSelected());
		} else {
			throw new RuntimeException("ValidControllerIsNotFound");
		}
	}

	public void setUseEventObserver(boolean b) {
		if(getWholeController() != null) {
			getWholeController().getUseEventObserver().setSelected(b);
		} else if(getIndividuallyController() != null) {
			getIndividuallyController().getUseEventObserver().setSelected(b);
		} else {
			throw new RuntimeException("ValidControllerIsNotFound");
		}
	}

	public void clearConsole() {
		int len = getWholeController().getConsole().getLength();
		getWholeController().getConsole().deleteText(0, len);
	}

	//------------------------------------------------------------------------------
	// 表示画面
	//------------------------------------------------------------------------------

	private Stage primaryStage = null;
	private Stage getPrimaryStage() { return(this.primaryStage); }
	private void setPrimaryStage(Stage stage) { this.primaryStage = stage; }

	private Stage resultsStage = null;
	private Stage getResultsStage() { return(this.resultsStage); }
	private void setResultsStage(Stage s) { this.resultsStage = s; }

	private Stage aboutStage = null;
	private Stage getAboutStage() { return(this.aboutStage); }
	private void setAboutStage(Stage s) { this.aboutStage = s; }

	private Scene primaryScene = null;
	private Scene getPrimaryScene() { return(this.primaryScene); }
	private void setPrimaryScene(Scene scene) { this.primaryScene = scene; }

	//------------------------------------------------------------------------------
	// コントローラ
	//------------------------------------------------------------------------------

	private AppController wholeController;
	private void          setWholeController(AppController c) { this.wholeController = c; }
	private AppController getWholeController()                { return(this.wholeController); }

	private ResultsController resultsController = null;
	private ResultsController getResultsController() { return(this.resultsController); }
	private void setResultsController(ResultsController c) { this.resultsController = c; }

	private IndividuallyController individuallyController = null;
	private IndividuallyController getIndividuallyController() { return(this.individuallyController); }
	private void setIndividuallyController(IndividuallyController c) { this.individuallyController = c; }

	private AboutController aboutController = null;
	private AboutController getAboutController() { return(this.aboutController); }
	private void setAboutController(AboutController c) { this.aboutController = c; }
}
