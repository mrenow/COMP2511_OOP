package unsw.gloriaromanus;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class Controller {
	@FXML
	protected Parent root;
	boolean isDestroyed = false;
	
	void terminate() {}
	
	public Parent getRoot() {
		return root;
	}
	void setRoot(Parent root) {
		this.root = root;
	}
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	// Only if parent allows
	void destroy() {
		// Cast error on this line means that root does not support modifying children
		((Pane)root.getParent()).getChildren().remove(this.root);
		terminate();
		isDestroyed = true;
	}
}
