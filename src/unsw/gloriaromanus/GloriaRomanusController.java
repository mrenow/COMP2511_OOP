package unsw.gloriaromanus;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import unsw.engine.*;
import unsw.engine.VicCondition.*;
import unsw.ui.TopBar;

import unsw.ui.Observer.MsgObserverable;
import unsw.ui.Observer.Observable;
import unsw.ui.Observer.Subject;


public class GloriaRomanusController extends Controller{


	private GameController game;
	private MapController mapController;
	
	private MsgObserverable turnChangedObservable = new MsgObserverable();

	@FXML
	private HBox topbox;
	private TopBar topBar;
	
	@FXML
	private void initialize() throws Exception {
		// TODO = you should rely on an object oriented design to determine ownership
		this.game = new GameController("src/unsw/gloriaromanus/province_id_adjacent.json",
				"src/unsw/gloriaromanus/landlocked_provinces.json",
				List.of(FactionType.ROME,
						FactionType.GAUL,
						FactionType.CARTHAGE,
						FactionType.PARTHIA,
						FactionType.BRITAIN));
		
		this.mapController = new MapController(game);
		
		GloriaRomanusApplication.loadExistingController(mapController, "src/unsw/gloriaromanus/map.fxml");
		// adds to the first index of the child list
		((Pane)getRoot()).getChildren().add(0, mapController.getRoot());
		

		//topbar observer and observerable implement
		
		VicComposite vic = generateVic();
		game.setVic(vic);
		displayInfo();
		
	}

	private VicComposite generateVic(){
		VicLeaf l1 = new VicLeaf(VictoryCondition.CONQUEST);
		VicLeaf l2 = new VicLeaf(VictoryCondition.WEALTH);
		VicLeaf l3 = new VicLeaf(VictoryCondition.TREASURY);
		VicComposite vic1 = new VicComposite(VictoryCondition.AND);
		VicComposite vic2 = new VicComposite(VictoryCondition.OR);

		vic2.addSubVic(l2);
		vic2.addSubVic(l3);
		vic1.addSubVic(l1);
		vic1.addSubVic(vic2);
		return vic1;
	}

	private void displayInfo(){
		this.topBar = new TopBar(topbox, game);
		
		game.attatchTurnChangedObserver(topBar);
	}
	
	@Override
	void terminate() {
		mapController.terminate();
	}
	
}