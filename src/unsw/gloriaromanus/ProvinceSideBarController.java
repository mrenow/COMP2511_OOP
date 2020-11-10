package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

/**
 * 
 * @author Derek
 */
public class ProvinceSideBarController {

    @FXML private ChoiceBox<String> provinceUnitCB;
    @FXML private ChoiceBox<String> choiceBox;
    @FXML private Label choiceBoxLabel;
    @FXML private Label provinceUnitLabel;
    @FXML private Button trainBtn;
    @FXML private Button moveBtn;
    @FXML private Button selectTarget;
    @FXML private TextField textField;
    @FXML private ToggleButton toggleSelect;

    private String selectedTargetthing;

    //@FXML
    public void handleTrainBtn() {
        choiceBoxLabel.setText(choiceBox.getValue().toString());
        textField.setText(choiceBox.getValue());
        // TODO Call train method
        
    }
    
    // Button to move to allied province or attack enemy province (two in one kinda)
    // Select province -> select unit -> select another province (enemy or ally) to move
    public void handleMove() {
        String selectedUnit = provinceUnitCB.getValue().toString();
        // Handle move or attack
        //if (ALLIED_PROVINCE_SELECTED) {
        if (selectedTargetthing = ally) {
            moveBtn.setText("Move");
            // TODO Call move method

        }
        //else if (ENEMY_PROVINCE_SELECTED) {
        else if (selectedTargetthing = enemy) {
            moveBtn.setText("Invade");
            // TODO Call Attack/Invade method

        }

    }

    // This might be better to handle selecting a target province vs basic button
    public void handleToggleSelect() {
        boolean isSelected = toggleSelect.isSelected();
        // TODO SELECT A SECOND TARGET PROVINCE AND STORE
        if (isSelected) {
            // TODO do select target province
        }

    }

    // When this button is selected, choose a 2nd province and store in list(?)
    public void handleSelectTarget() {
        // TODO SELECT A SECOND TARGET PROVINCE AND STORE
        // observer(?) State(?)
        selectedTargetthing = SELECTED_PROVINCE;

    }

    //@Override
    @FXML
    public void initialize() {

        // This items are for configuring the ChoiceBox (To Train)
        choiceBoxLabel.setText("");
        //choiceBox.setValue("SAMPLE");
        choiceBox.getItems().add("Heavy Calvary");
        choiceBox.getItems().add("Archer");
        choiceBox.getItems().add("Elephants");

        // These items are for configuring the choicebox for units in province
        provinceUnitLabel.setText("");
        provinceUnitCB.getItems().add("Unit1");
        provinceUnitCB.getItems().add("Unit2");
        provinceUnitCB.getItems().add("Unit3");
    }
}
