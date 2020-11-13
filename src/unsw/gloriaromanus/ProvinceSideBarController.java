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
import javafx.scene.control.TextArea;
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
    @FXML private Button selectActionProvince;
    @FXML private Button selectTargetProvince;
    @FXML private TextField trainTextField;
    @FXML private TextField selected_province;
    @FXML private TextField action_province;
    @FXML private TextField target_province;
    @FXML private TextArea selectedProvinceUnitsList;
    @FXML private TextArea unitsInTraining;

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
        // If "All Units" tag chosen
        if (u == "All Units") {
            // If target province is player's own province
            if (targetProvince.getOwner().equals(initialProvince.getOwner())) {
                // Call move method
                game.move(initialProvince.getUnits(), targetProvince);
            }
            // Else enemy province so invade
            else {
                // Call invade method
                game.invade(initialProvince, targetProvince);
            }
        }
        // Else any other individual unit tag chosen
        else {
            for (Unit unit : initialProvince.getUnits()) {
                if (u == unit.getName()) {
                    unitList = new ArrayList<Unit>();
                    unitList.add(unit);
                    // Handle move or attack
                    if (targetProvince.getOwner().equals(initialProvince.getOwner())) {
                        // Call move method
                        game.move(unitList, targetProvince);
                    }
                    else {
                        // Call invade method
                        game.invade(unitList, targetProvince);
                    }
                }
            }
        }
    }

    // Displays the name of the selected action province along with its information
    @FXML
    public void handleSelectProvince() {
        provinceUnitCB.getItems().clear();
        if (!province.getOwner().equals(game.getCurrentTurn())) {
            System.out.println("Action province must be your own province.");
        }
        else {
            this.initialProvince = province;
            action_province.setText(initialProvince.getName());

            provinceUnitCB.getItems().add("All Units");
            for (Unit u : initialProvince.getUnits()) {
                provinceUnitCB.getItems().add(u.getName());
            }
            System.out.println("Action province selected.");
        }
    }
    @FXML
    public void handleSelectTarget() {
        this.targetProvince = province;
        target_province.setText(targetProvince.getName());

        if (targetProvince.getOwner().equals(game.getCurrentTurn())) {
            moveBtn.setText("Move");
        }
        else {
            moveBtn.setText("Invade");
        }
        System.out.println("Selected province is: " + targetProvince.getName());
    }

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

    }

    @Override
    public void update(ProvinceFeatureInfo p) {
        System.out.println("Selected province is: " + p.getName());
        System.out.println("Selected province owner is: " + p.getOwner().getTitle());
        selected_province.setText(p.getName());
        this.province = p.getProvince();
        this.myProvince = p.getName();
       
    }

}
