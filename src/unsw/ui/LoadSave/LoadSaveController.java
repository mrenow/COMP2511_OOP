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
import unsw.ui.Observer.MenuInfo;
import unsw.ui.Observer.Observer;

public class LoadSaveController extends Controller implements Observer<MenuInfo>{
    @FXML
    private ListView<Text> list = new ListView<Text>();

    private List<Text> buttons = new ArrayList<>();

    @FXML
	public void initialize() {
        for (int i = 0; i < 20; i++) {
            buttons.add(new Text(Integer.toString(i)));
        }
        ObservableList<Text> items = FXCollections.observableList(buttons);
        list.setItems(items);
    }
    @Override
    public void update(MenuInfo message) {};


}
