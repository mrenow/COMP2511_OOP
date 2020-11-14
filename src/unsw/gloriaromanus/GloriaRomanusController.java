package unsw.gloriaromanus;

import java.io.FileInputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import unsw.engine.*;
import unsw.engine.VicCondition.*;
import unsw.ui.topbar.TopBarController;
import unsw.ui.VicUI.VicUIController;
import unsw.ui.UIPath;
import unsw.ui.Observer.MsgObserverable;


public class GloriaRomanusController extends Controller{


	private GameController game;
	private MapController mapController;
	private VicUIController vicUIController;
	private ProvinceSideBarController sideController;

	private TopBarController topBar;
	
	public GloriaRomanusController(GameController game) {
		this.game = game;
    	GloriaRomanusApplication.loadExistingController(this, UIPath.GAME.getPath());
	}
	
	@FXML
	private void initialize() throws Exception {

		
		VBox bottomPane = new VBox();
		HBox lowerBox = new HBox();
		
		mapController = new MapController(game);
		sideController = new ProvinceSideBarController(game);
		mapController.attachProvinceSelectedObserver(sideController);
		mapController.getUnitSelectionProperty().bind(sideController.getUnitSelectionProperty());
		
		
		
		topBar = new TopBarController(game);
		game.attatchTurnChangedObserver(topBar);
		//these two should be inside main
		
		
		HBox.setHgrow(mapController.getRoot(), Priority.ALWAYS);
		lowerBox.getChildren().add(mapController.getRoot());
		HBox.setHgrow(sideController.getRoot(), Priority.NEVER);
		lowerBox.getChildren().add(sideController.getRoot());
		bottomPane.getChildren().add(topBar.getRoot());
		VBox.setVgrow(lowerBox, Priority.ALWAYS);
		bottomPane.getChildren().add(lowerBox);
		((StackPane) root).getChildren().add(bottomPane);
	}


	// public void victory() throws Exception{
	// 	Image im = new Image(new FileInputStream("src/unsw/ui/Victory.JPG"));
	// 	ImageView image = new ImageView(im);
	// 	StackPane pane = new StackPane(image);
	// 	((StackPane)getRoot()).getChildren().add(0, pane);
	// }
	
	@Override
	void terminate() {
		mapController.terminate();
	}
	
}