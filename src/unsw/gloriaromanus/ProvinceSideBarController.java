package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle.Control;

import com.esri.arcgisruntime.internal.io.handler.request.ServerContextConcurrentHashMap.HashMapChangedEvent.Action;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import unsw.engine.*;
import unsw.ui.Observer.*;

/**
 * 
 * @author Derek
 */
public class ProvinceSideBarController extends Controller implements Observer<ProvinceFeatureInfo> {

    private GameController game;
    private Province province;
    private Province initialProvince;
    private Province targetProvince;
    private String myProvince;
    private String enemyProvince;
    private List<Unit> unitList;
    
    @FXML private ChoiceBox<String> provinceUnitCB;
    @FXML private ChoiceBox<String> trainChoiceBox;
    @FXML private Label choiceBoxLabel;
    @FXML private Label provinceUnitLabel;
    @FXML private Button trainBtn;
    @FXML private Button moveBtn;
    @FXML private Button selectTarget;
    @FXML private TextField trainTextField;
    @FXML private ToggleButton toggleSelect;
    @FXML private TextField invading_province;
    @FXML private TextField opponent_province;

    private String selectedTargetthing;

    public ProvinceSideBarController() {
        
    }

    public ProvinceSideBarController(GameController game) {
        this.game = game;
    }

    @FXML
    public void handleTrainBtn(ActionEvent e) {
        choiceBoxLabel.setText(trainChoiceBox.getValue().toString());
        trainTextField.setText(trainChoiceBox.getValue());
        // TODO Call train method
        
        
    }
    
    // Button to move to allied province or attack enemy province (two in one kinda)
    // Select province -> select unit -> select another province (enemy or ally) to move
    @FXML
    public void handleMove(ActionEvent e) {
        String u = provinceUnitCB.getValue().toString();
        if (u == "All Units") {
            // Handle move or attack
            if (initialProvince.getOwner().equals(province.getOwner())) {
                // Call move method
                game.move(initialProvince.getUnits(), province);
            }
            else {
                // Call invade method
                game.invade(initialProvince, targetProvince);
            }
        }
        else {
            for (Unit unit : initialProvince.getUnits()) {
                if (u == unit.getName()) {
                    unitList = new ArrayList<Unit>();
                    unitList.add(unit);
                    // Handle move or attack
                    if (initialProvince.getOwner().equals(province.getOwner())) {
                        // Call move method
                        game.move(unitList, province);
                    }
                    else {
                        // Call invade method
                        game.invade(unitList, targetProvince);
                    }
                }
            }
        }
        
    }

    // When this button is selected, choose a 2nd province and store in list(?)
    @FXML
    public void handleSelectTarget() {
        //invading_province.setText(myProvince);
        this.initialProvince = province;
    }

    //@Override
    @FXML
    public void initialize() {

        // This items are for configuring the ChoiceBox (To Train)
        choiceBoxLabel.setText("");
        trainChoiceBox.getItems().add("TEST_TROOP");
        trainChoiceBox.getItems().add("Heavy Calvary");
        trainChoiceBox.getItems().add("Archer");
        trainChoiceBox.getItems().add("Elephants");
        //trainChoiceBox.getItems().addAll(ItemType.nameList);

        // These items are for configuring the choicebox for units in province
        provinceUnitLabel.setText("");
        provinceUnitCB.getItems().add("All Units");
        provinceUnitCB.getItems().add("Unit1");
        provinceUnitCB.getItems().add("Unit2");
        provinceUnitCB.getItems().add("Unit3");
    }

    @Override
    public void update(ProvinceFeatureInfo p) {
        //this.province = p.getProvince();
        if (p.getOwner().equals(game.getCurrentTurn())) {
            this.province = p.getProvince();
            this.myProvince = p.getName();
            moveBtn.setText("Move");
        }
        else {
            this.targetProvince = p.getProvince();
            this.enemyProvince = p.getName();
            moveBtn.setText("Invade");
        }
    }

/*
    public void setInvadingProvince(String province) {
        invading_province.setText(province);
    }

    public void setOpponentProvince(String province) {
        opponent_province.setText(province);
    }
*/
}
