package unsw.ui.LoadSave;

import static unsw.gloriaromanus.GloriaRomanusApplication.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import unsw.engine.GameController;
import unsw.engine.VicCondition.VicComponent;
import unsw.engine.VicCondition.VictoryCondition;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.gloriaromanus.GloriaRomanusController;
import unsw.saves.GameData;
import unsw.ui.UIPath;
import unsw.ui.Observer.MenuInfo;
import unsw.ui.Observer.Observer;

public class LoadSaveController extends Controller implements Observer<MenuInfo>{

    @FXML
    private TextField filename;
    @FXML
    private ListView<String> list = new ListView<String>();
    private GameData gameData = new GameData();
    private ObservableList<String> name;
    @FXML
	public void initialize() {
        name = FXCollections.observableArrayList(getgamelist());
        list.setItems(name);
    }
    
    @FXML
    public void load(){
    	GameController game = GameController.loadFromSave(UIPath.SAVES.getPath() + getSelectedFile());
    
    	GloriaRomanusController controller = new GloriaRomanusController(game);
        app.setScene(controller);
    }
    @FXML
    public void delete(){
        if (getSelectedFile() != null) {
            gameData.deleteFile(getSelectedFile());
        }
    }
    @FXML
    public void quit(){
        Controller controller = GloriaRomanusApplication.loadController(UIPath.MENU.getPath());
        GloriaRomanusApplication.app.setScene(controller);
    }
    private String getSelectedFile() {
    	return list.getSelectionModel().getSelectedItem();
    }
    private List<String> getgamelist(){
        List<String> namelist=gameData.getFileNames();
        namelist.remove("GameData.java");
        return namelist;
    }
    @Override
    public void update(MenuInfo message) {
        name = FXCollections.observableArrayList(getgamelist());
        list.setItems(name);
    };


}
