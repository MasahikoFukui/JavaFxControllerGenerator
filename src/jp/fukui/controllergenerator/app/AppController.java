package jp.fukui.controllergenerator.app;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;

public class AppController implements Initializable {
	@Override public void initialize(URL location, ResourceBundle resources) { }

	@FXML private TextArea console;
	@FXML private CheckBox useEventObserver;
	@FXML private Button scan;
	@FXML private CheckMenuItem individually;
	@FXML private TextField directory;

	public TextArea getConsole() { return(this.console); }
	public CheckBox getUseEventObserver() { return(this.useEventObserver); }
	public Button getScan() { return(this.scan); }
	public CheckMenuItem getIndividually() { return(this.individually); }
	public TextField getDirectory() { return(this.directory); }

	@FXML public void useEventObserverChecked(ActionEvent e) { this._o.useEventObserverChecked(e);  }
	@FXML public void folderDropped(DragEvent e) { this._o.folderDropped(e);  }
	@FXML public void folderDragOver(DragEvent e) { this._o.folderDragOver(e);  }
	@FXML public void directoryKeyPressed(KeyEvent e) { this._o.directoryKeyPressed(e);  }
	@FXML public void directoryMouseClicked(MouseEvent e) { this._o.directoryMouseClicked(e);  }
	@FXML public void refClicked(ActionEvent e) { this._o.refClicked(e);  }
	@FXML public void scanClicked(ActionEvent e) { this._o.scanClicked(e);  }
	@FXML public void closeMenu(ActionEvent e) { this._o.closeMenu(e);  }
	@FXML public void openIndividuallyMenu(ActionEvent e) { this._o.openIndividuallyMenu(e);  }
	@FXML public void aboutMenu(ActionEvent e) { this._o.aboutMenu(e);  }

	//------------------------------------------------------------------------------
	// Event notification
	//------------------------------------------------------------------------------

	public interface Observer {
		public void useEventObserverChecked(ActionEvent e);
		public void folderDropped(DragEvent e);
		public void folderDragOver(DragEvent e);
		public void directoryKeyPressed(KeyEvent e);
		public void directoryMouseClicked(MouseEvent e);
		public void refClicked(ActionEvent e);
		public void scanClicked(ActionEvent e);
		public void closeMenu(ActionEvent e);
		public void openIndividuallyMenu(ActionEvent e);
		public void aboutMenu(ActionEvent e);
	}

	private Observer _o = null;
	public void setObserver(Observer o) { this._o = o; }
}
