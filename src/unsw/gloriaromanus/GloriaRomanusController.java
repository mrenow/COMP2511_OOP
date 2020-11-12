package unsw.gloriaromanus;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import unsw.engine.*;
import unsw.ui.TopBar;



public class GloriaRomanusController extends Controller{


	private GameController game;
	private MapController mapController;
	

	@FXML
	private void initialize() throws Exception {
		// TODO = you should rely on an object oriented design to determine ownership
		game = new GameController("src/unsw/gloriaromanus/province_id_adjacent.json",
				"src/unsw/gloriaromanus/landlocked_provinces.json",
				List.of(FactionType.ROME,
						FactionType.GAUL,
						FactionType.CARTHAGE,
						FactionType.PARTHIA,
						FactionType.BRITAIN));
		
		mapController = new MapController(game);
		
		GloriaRomanusApplication.loadExistingController(mapController, "src/unsw/gloriaromanus/map.fxml");
		// adds to the first index of the child list
		((Pane)getRoot()).getChildren().add(0, mapController.getRoot());
		
		displayInfo();
	}


	@FXML
	private HBox topbox;
	private TopBar topBar;
	private void displayInfo(){
		this.topBar=new TopBar(topbox, game);
	}
	
	@Override
	void terminate() {
		mapController.terminate();
	}
	
}