package unsw.gloriaromanus;

import java.io.FileInputStream;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import unsw.engine.*;
import unsw.engine.VicCondition.*;
import unsw.ui.topbar.TopBar;
import unsw.ui.VicUIController;
import unsw.ui.Observer.MsgObserverable;


public class GloriaRomanusController extends Controller{


	private GameController game;
	private MapController mapController;
	private VicUIController vicUIController;
	private MsgObserverable turnChangedObservable = new MsgObserverable();
	private ProvinceSideBarController sideController;
	
	private MsgObserverable turnchange = new MsgObserverable();

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
		
		sideController = new ProvinceSideBarController(game);

		GloriaRomanusApplication.loadExistingController(sideController, "src/unsw/gloriaromanus/ProvinceSideBar.fxml");
		// adds to the next index of the child list
		((Pane)getRoot()).getChildren().add(1, sideController.getRoot());
		// attach observer
		mapController.attachProvinceSelectedObserver(sideController);

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

	public void victory() throws Exception{
		Image im = new Image(new FileInputStream("src/unsw/ui/Victory.JPG"));
		ImageView image = new ImageView(im);
		StackPane pane = new StackPane(image);
		((StackPane)getRoot()).getChildren().add(0, pane);
	}
	
	@Override
	void terminate() {
		mapController.terminate();
	}
	
}