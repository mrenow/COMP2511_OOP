package unsw.ui.GameSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import unsw.engine.FactionType;
import unsw.engine.GameController;
import unsw.engine.VicCondition.VicComponent;
import unsw.engine.VicCondition.VicComposite;
import unsw.engine.VicCondition.VictoryCondition;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.gloriaromanus.GloriaRomanusController;
import unsw.ui.Observer.Observer;
import unsw.ui.UIPath;
import unsw.ui.Observer.MenuInfo;
import static unsw.gloriaromanus.GloriaRomanusApplication.app;
public class GameSettingController extends Controller implements Observer<MenuInfo>{
    @FXML private Button play;
    @FXML private Button quit;
    @FXML private ListView<FactionType> factionList;
    // implement later
    @FXML private ListView<FactionType> VictoryConditionList;
    
    
    
    
    @FXML
    public void initialize() {
    	factionList.getItems().addAll(FactionType.values());
    	factionList.getItems().remove(FactionType.NO_ONE);	
    	factionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	factionList.getSelectionModel().getSelectedItems().addListener(
    			(ListChangeListener<FactionType>)(c -> play.setDisable(c.getList().size() < 2)));
    	play.setDisable(true);
    }
   
    @FXML
    public void play(){
    	// Init game
    	List<VictoryCondition> valuesList = new ArrayList<>(Arrays.asList(VictoryCondition.values()));
    	GameController game = new GameController(
    			"src/unsw/gloriaromanus/province_id_adjacent.json",
				"src/unsw/gloriaromanus/landlocked_provinces.json",
				new ArrayList<>(factionList.getSelectionModel().getSelectedItems()),
				VicComponent.randVicComponent(valuesList, new Random()));
        try {
        	GloriaRomanusController controller = new GloriaRomanusController(game);
            app.setScene(controller);
        } catch (Exception e) {
            System.out.println("fild DNE");
        }
        
    }
    @FXML
    public void quit(){
        try {
            Controller controller = GloriaRomanusApplication.loadController(UIPath.MENU.getPath());
            app.setScene(controller);
        } catch (Exception e) {
            System.out.println("setting fild DNE");
        }
    }
    @FXML
    public void GenerateVictoryCondition(){
        // randomize victory conditions
        
        List<VictoryCondition> conditions = new ArrayList<>();
        List<VictoryCondition> generated = new ArrayList<>();
        conditions.add(VictoryCondition.CONQUEST);
        conditions.add(VictoryCondition.TREASURY);
        conditions.add(VictoryCondition.WEALTH);
        List<VictoryCondition> logic = new ArrayList<>();
        conditions.add(VictoryCondition.AND);
        conditions.add(VictoryCondition.OR);
        Random r = new Random();

    }
    @FXML
    public void selectfaction(){
        
    }
    
    @Override
    public void update(MenuInfo message) {
        // TODO Auto-generated method stub
        
    }
}
