package unsw.gloriaromanus;

import java.io.FileInputStream;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import unsw.engine.*;
import unsw.engine.VicCondition.*;
import unsw.ui.topbar.TopBarController;
import unsw.ui.VicUIController;
import unsw.ui.Observer.MsgObserverable;


public class GloriaRomanusController extends Controller{


	private GameController game;
	private MapController mapController;
	private VicUIController vicUIController;
	private MsgObserverable turnChangedObservable = new MsgObserverable();
	private ProvinceSideBarController sideController;
	
	private MsgObserverable turnchange = new MsgObserverable();

	private TopBarController topbar;
	
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
		StackPane.setAlignment(sideController.getRoot(), Pos.CENTER_RIGHT);
		
		// attach observer
		mapController.attachProvinceSelectedObserver(sideController);
		
		topbar = new TopBarController(game);
		//topbar observer and observerable implement
		GloriaRomanusApplication.loadExistingController(topbar, "src/unsw/ui/topbar/TopBar.fxml");
		((Pane)getRoot()).getChildren().add(2, topbar.getRoot());
		StackPane.setAlignment(topbar.getRoot(), Pos.TOP_CENTER);
		
		//these two should be inside main
		VicComposite vic = generateVic();
		game.setVic(vic);
		
	}

	/**
	 * this should belong to main menu feature
	 * and we dont have main menu yet so just put here for later implement
	 * @return
	 */
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