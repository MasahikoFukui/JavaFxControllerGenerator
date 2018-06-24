package jp.fukui.controllergenerator.app;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;

public class IndividuallyController implements Initializable {
	@Override public void initialize(URL location, ResourceBundle resources) { }

	@FXML private ImageView srcImage;
	@FXML private CheckBox useEventObserver;
	@FXML private Label fxmlName;
	@FXML private Label controllerName;
	@FXML private ImageView dstImage;
	@FXML private CheckMenuItem whole;

	public ImageView getSrcImage() { return(this.srcImage); }
	public CheckBox getUseEventObserver() { return(this.useEventObserver); }
	public Label getFxmlName() { return(this.fxmlName); }
	public Label getControllerName() { return(this.controllerName); }
	public ImageView getDstImage() { return(this.dstImage); }
	public CheckMenuItem getWhole() { return(this.whole); }

	@FXML public void controllerDragDetected(MouseEvent e) { this._o.controllerDragDetected(e);  }
	@FXML public void fxmlDropped(DragEvent e) { this._o.fxmlDropped(e);  }
	@FXML public void fxmlDragOver(DragEvent e) { this._o.fxmlDragOver(e);  }
	@FXML public void useEventObserverChecked(ActionEvent e) { this._o.useEventObserverChecked(e);  }
	@FXML public void closeMenu(ActionEvent e) { this._o.closeMenu(e);  }
	@FXML public void openWholeMenu(ActionEvent e) { this._o.openWholeMenu(e);  }
	@FXML public void aboutMenu(ActionEvent e) { this._o.aboutMenu(e);  }

	//------------------------------------------------------------------------------
	// Event notification
	//------------------------------------------------------------------------------

	public interface Observer {
		public void controllerDragDetected(MouseEvent e);
		public void fxmlDropped(DragEvent e);
		public void fxmlDragOver(DragEvent e);
		public void useEventObserverChecked(ActionEvent e);
		public void closeMenu(ActionEvent e);
		public void openWholeMenu(ActionEvent e);
		public void aboutMenu(ActionEvent e);
	}

	private Observer _o = null;
	public void setObserver(Observer o) { this._o = o; }
}
