package unsw.ui.GameSetting;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import unsw.engine.FactionType;
import unsw.engine.GameController;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.gloriaromanus.GloriaRomanusController;
import unsw.ui.Observer.Observer;
import unsw.ui.UIPath;
import unsw.ui.Observer.MenuInfo;

public class GameSettingController extends Controller implements Observer<MenuInfo>{
    @FXML private Button play;
    @FXML private Button quit;
    @FXML private ListView<FactionType> factionList;
    // implement later
    @FXML private ListView<FactionType> VictoryConditionList;
    private List<String> faction = new ArrayList<>();
    private List<String> condition = new ArrayList<>();
    
    
    
    
    @FXML
    public void initialize() {
    	factionList.getItems().addAll(FactionType.values());
    	factionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	
    }
    @FXML
    public void play(){
    	// Init game
		GameController game = new GameController("src/unsw/gloriaromanus/province_id_adjacent.json",
				"src/unsw/gloriaromanus/landlocked_provinces.json",
				new ArrayList<>(factionList.getSelectionModel().getSelectedItems()));
        try {
        	GloriaRomanusController controller = new GloriaRomanusController(game);
            GloriaRomanusApplication.app.setScene(controller);
        } catch (Exception e) {
            System.out.println("fild DNE");
        }
        
    }
    @FXML
    public void quit(){
        try {
            Controller controller = GloriaRomanusApplication.loadController(UIPath.MENU.getPath());
            GloriaRomanusApplication.app.setScene(controller);
        } catch (Exception e) {
            System.out.println("setting fild DNE");
        }
    }
    @FXML
    public void GenerateVictoryCondition(){
        // randomize victory conditions
    }
    @FXML
    public void selectfaction(){
        
    }
    
    @Override
    public void update(MenuInfo message) {
        // TODO Auto-generated method stub
        
    }
}
