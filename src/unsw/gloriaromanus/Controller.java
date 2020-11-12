package unsw.gloriaromanus;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Parent;

public class Controller {
	@FXML
	protected Parent root;
	
	void terminate() {}
	
	public Parent getRoot() {
		return root;
	}
	void setRoot(Parent root) {
		this.root = root;
	}
}
