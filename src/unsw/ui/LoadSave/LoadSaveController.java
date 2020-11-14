package unsw.ui.LoadSave;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.saves.GameData;
import unsw.ui.UIPath;
import unsw.ui.Observer.MenuInfo;
import unsw.ui.Observer.Observer;

public class LoadSaveController extends Controller implements Observer<MenuInfo>{
    public static String SAVE_PATH = "saves/";

    @FXML
    private TextField filename;
    @FXML
    private ListView<String> list = new ListView<String>();
    private GameData gameData = new GameData();
    private ObservableList<String> name;
    private String selectFile;
    @FXML
	public void initialize() {
        name = FXCollections.observableArrayList(gameData.getFileNames());
        list.setItems(name);
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> ov, 
            String old_val, String new_val){
                selectFile = new_val;
                System.out.println(new_val);
            }
        });
            
    }
    @FXML
    public void load(){
        //process load
        //newgame
    }
    @FXML
    public void save(){
        if (!(gameData.checkFileName(filename.getText()))){
            gameData.saveGame(filename.getText());
        } else {
            filename.setText("name already exist, try another one");
        }
    }
    @FXML
    public void delete(){
        if (selectFile != null) {
            gameData.deleteFile(selectFile);
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
    @Override
    public void update(MenuInfo message) {};


}
