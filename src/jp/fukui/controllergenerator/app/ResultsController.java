package jp.fukui.controllergenerator.app;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;

public class ResultsController implements Initializable {
	@Override public void initialize(URL location, ResourceBundle resources) { }

	@FXML private Button cancel;
	@FXML private Button selectAll;
	@FXML private ListView controllerList;
	@FXML private Button selectAllClear;
	@FXML private Button generate;

	public Button getCancel() { return(this.cancel); }
	public Button getSelectAll() { return(this.selectAll); }
	public ListView getControllerList() { return(this.controllerList); }
	public Button getSelectAllClear() { return(this.selectAllClear); }
	public Button getGenerate() { return(this.generate); }

	@FXML public void generateClicked(ActionEvent e) { this._o.generateClicked(e);  }
	@FXML public void cancelClicked(ActionEvent e) { this._o.cancelClicked(e);  }
	@FXML public void selectAllClicked(ActionEvent e) { this._o.selectAllClicked(e);  }
	@FXML public void selectAllClearClicked(ActionEvent e) { this._o.selectAllClearClicked(e);  }

	//------------------------------------------------------------------------------
	// Event notification
	//------------------------------------------------------------------------------

	public interface Observer {
		public void generateClicked(ActionEvent e);
		public void cancelClicked(ActionEvent e);
		public void selectAllClicked(ActionEvent e);
		public void selectAllClearClicked(ActionEvent e);
	}

	private Observer _o = null;
	public void setObserver(Observer o) { this._o = o; }
}
