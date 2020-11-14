package unsw.ui.LoadSave;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.ui.UIPath;
import unsw.ui.Observer.MenuInfo;
import unsw.ui.Observer.Observer;

public class LoadSaveController extends Controller implements Observer<MenuInfo>{
    @FXML
    private ListView<Text> list = new ListView<Text>();

    private List<Text> buttons = new ArrayList<>();

    @FXML
	public void initialize() {
        
    }
    @FXML
    public void load(){

    }
    @FXML
    public void save(){

    }
    @FXML
    public void delete(){

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
    @Override
    public void update(MenuInfo message) {};


}
