package unsw.ui.GameSetting;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.ui.Observer.Observer;
import unsw.ui.Observer.MenuInfo;

public class GameSettingController extends Controller implements Observer<MenuInfo>{
    @FXML private Button play;
    @FXML private Button quit;
    @FXML private ListView<String> factionList;
    @FXML private ListView<String> VictoryConditionList;
    private List<String> faction = new ArrayList<>();
    private List<String> condition = new ArrayList<>();
    
    @FXML
    public void initialize() {
        ObservableList<String> fl = FXCollections.observableArrayList(faction);
        ObservableList<String> cl = FXCollections.observableArrayList(condition);
        factionList.setItems(fl);
        VictoryConditionList.setItems(cl);

    }
    @FXML
    public void play(){
        try {
            Controller controller = GloriaRomanusApplication.loadController("src/unsw/gloriaromanus/main.fxml");
            GloriaRomanusApplication.app.setScene(controller);
        } catch (Exception e) {
            System.out.println("fild DNE");
        }
        
    }
    @FXML
    public void quit(){
        
    }
    @FXML
    public void GenerateVictoryCondition(){
        
    }
    @FXML
    public void selectfaction(){
        
    }
    
    @Override
    public void update(MenuInfo message) {
        // TODO Auto-generated method stub
        
    }
}
