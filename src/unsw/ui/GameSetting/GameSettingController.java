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
import unsw.engine.VicCondition.VicLeaf;
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
    //@FXML private ListView<FactionType> VictoryConditionList;
    //@FXML private ListView<FactionType> selectedfaction;
    //private List<FactionType> selectedfaction = new ArrayList<>();
    
    
    @FXML
    public void initialize() {
    	factionList.getItems().addAll(FactionType.values());
    	factionList.getItems().remove(FactionType.NO_ONE);	
    	factionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	factionList.getSelectionModel().getSelectedItems().addListener(
    			(ListChangeListener<FactionType>)(c -> play.setDisable(c.getList().size() < 2)));
        //play.setDisable(true);
        play.setDisable(false);
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
        conditions.add(VictoryCondition.CONQUEST);
        conditions.add(VictoryCondition.TREASURY);
        conditions.add(VictoryCondition.WEALTH);
        List<VictoryCondition> logic = new ArrayList<>();
        logic.add(VictoryCondition.AND);
        logic.add(VictoryCondition.OR);
        Random r = new Random();
        VicComposite logic1 = new VicComposite(logic.get(r.nextInt()%2));
        VicComposite logic2 = new VicComposite(logic.get(r.nextInt()%2));
        int index = r.nextInt()%3;
        VicLeaf leaf1 = new VicLeaf(conditions.get(index));
        conditions.remove(index);
        index = r.nextInt()%2;
        VicLeaf leaf2 = new VicLeaf(conditions.get(index));
        conditions.remove(index);
        VicLeaf leaf3 = new VicLeaf(conditions.get(0));
        conditions.remove(index);
        logic2.addSubVic(leaf2);
        logic2.addSubVic(leaf3);
        logic1.addSubVic(leaf1);
        logic1.addSubVic(logic2);
        //final vic = logic1;
    }
    // @FXML
    // public void selectfaction(){
        
    // }
    
    @Override
    public void update(MenuInfo message) {
        // TODO Auto-generated method stub
        
    }
}
