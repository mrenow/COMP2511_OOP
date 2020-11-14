package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle.Control;

import com.esri.arcgisruntime.internal.io.handler.request.ServerContextConcurrentHashMap.HashMapChangedEvent.Action;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import unsw.engine.*;
import unsw.ui.Observer.*;

import static unsw.gloriaromanus.GloriaRomanusApplication.app;;

/**
 * 
 * @author Derek
 */
public class ProvinceSideBarController extends Controller implements Observer<ProvinceFeatureInfo> {

    private GameController game;
    private Province province;
    private Province initialProvince;
    private Province targetProvince;
    
    @FXML private ChoiceBox<String> trainChoiceBox;
    @FXML private ChoiceBox<String> taxChoiceBox;
    @FXML private Button trainBtn;
    @FXML private Button moveBtn;
    @FXML private Button taxLevelBtn;
    @FXML private Button selectActionProvince;
    @FXML private Button selectTargetProvince;
    @FXML private Button cancelTrainingBtn;
    @FXML private TextField wealthField;
    @FXML private TextField taxField;
    @FXML private TextField taxLevelField;
    @FXML private TextField trainTextField;
    @FXML private TextField selected_province;
    @FXML private TextField action_province;
    @FXML private TextField target_province;
    @FXML private TextArea selectedProvinceUnitsList;
    @FXML private ListView<TrainingSlotEntry> unitsTrainingListView;
    @FXML private ListView<Unit> unitsProvinceListView;

    public ProvinceSideBarController() {}

    public ProvinceSideBarController(GameController game) {
        this.game = game;
        GloriaRomanusApplication.loadExistingController(this, "src/unsw/gloriaromanus/ProvinceSideBar.fxml");
        game.attatchTrainingChangedObserver(this::updateTrainingList);
        game.attatchUnitsChangedObserver(this::updateUnitList);
        game.attatchProvinceChangedObserver(this::updateProvinceInfo);
        game.attatchTurnChangedObserver(this::refresh);
    }

    // Handles button to train units in that province
    @FXML
    public void handleTrainBtn(ActionEvent e) {
        trainTextField.setText(trainChoiceBox.getValue());
        // Call train method
        for (ItemType u : initialProvince.getTrainable()) {
            if (trainChoiceBox.getValue().toString() == u.getName(1)) {
                game.trainUnit(initialProvince, u);
            }
        }
    }

    // Handles cancel training
    @FXML
    public void handleCancelTraining(ActionEvent e) {
        ArrayList<TrainingSlotEntry> copy = new ArrayList<TrainingSlotEntry>(unitsTrainingListView.getSelectionModel().getSelectedItems());
        app.displayText("Cancelling training for: " + copy.toString());
        for (TrainingSlotEntry t : copy) {
            game.cancelTraining(t);
        }
    }
    
    // Handles button to move to allied province or attack enemy province
    @FXML
    public void handleMove(ActionEvent e) {
        ArrayList<Unit> copy = new ArrayList<Unit>(unitsProvinceListView.getSelectionModel().getSelectedItems());
        if (game.getDestinations(copy).contains(targetProvince)) {
            // Call move method
            System.out.println("IM HERE");
            game.move(copy, targetProvince);
        }
        else if (game.getAttackable(copy).contains(targetProvince)) {
            // Pass info needed for invation to BattlePaneController
            BattlePaneController b = new BattlePaneController(game, copy, targetProvince);
            addToApp(b);
        }
    }

    // Handles changing of tax level
    @FXML
    public void handleTaxLevel() {
        // Get new tax level choice from choice box
        String newTaxLevel = taxChoiceBox.getValue().toString();
        // Switch case to set new tax level
        switch(newTaxLevel) {
            case "Low Tax":
                game.setTax(initialProvince, TaxLevel.LOW_TAX);
                break;
            case "Normal Tax":
                game.setTax(initialProvince, TaxLevel.NORMAL_TAX);
                break;
            case "High Tax":
                game.setTax(initialProvince, TaxLevel.HIGH_TAX);
                break;
            case "Very High Tax":
                game.setTax(initialProvince, TaxLevel.VERY_HIGH_TAX);
                break;
        }
    }

    // Displays the name of the selected action province along with all other information
    @FXML
    public void handleSelectProvince(ActionEvent e) {
        // Clears the province unit choice box every time handle event is called
        unitsProvinceListView.getItems().clear();
        trainChoiceBox.getItems().clear();
        if (!province.getOwner().equals(game.getCurrentTurn())) {
            app.displayText("Action province must be your own province.");
        }
        else {
            this.initialProvince = province;
            action_province.setText(initialProvince.getName());
            // Update wealth and tax info
            wealthField.setText(Integer.toString(initialProvince.getWealth()));
            taxField.setText(Double.toString(initialProvince.getTaxLevel().getTaxRate()));
            setTaxLevel(initialProvince.getTaxLevel());
            // Update province choicebox accordingly with units
            if (!initialProvince.getUnits().isEmpty() || initialProvince.getUnits() != null) {
                for (Unit u : initialProvince.getUnits()) {
                    unitsProvinceListView.getItems().add(u);
                }
            }
            else {
                app.displayText("There are no units currently in selected province.");
            }
            // Update units currently in training for that province in text area
            List<TrainingSlotEntry> copy = new ArrayList<>(initialProvince.getCurrentTraining());
            for (TrainingSlotEntry u : copy) {
                unitsTrainingListView.getItems().add(u);
            }
            // Update Trainable Units
            List<ItemType> trainableUnits = initialProvince.getTrainable();
            for (ItemType u : trainableUnits) {
                trainChoiceBox.getItems().add(u.getName(1));
            }
            app.displayText("Action province selected.");
        }
    }

    // Handles button that selects target province
    @FXML
    public void handleSelectTarget(ActionEvent e) {
        this.targetProvince = province;
        target_province.setText(targetProvince.getName());
        // Check if target province belongs to player faction
        if (targetProvince.getOwner().equals(game.getCurrentTurn())) {
            // Set button text to "Move"
            moveBtn.setText("Move");
        }
        // Else target province belongs to enemy
        else {
            // Set button text to "Invade"
            moveBtn.setText("Invade");
        }
        app.displayText("Selected province is: " + targetProvince.getName());
    }

    @FXML
    public void initialize() {

        // These items are for configuring the tax level choice box
        taxChoiceBox.getItems().add("Low Tax");
        taxChoiceBox.getItems().add("Normal Tax");
        taxChoiceBox.getItems().add("High Tax");
        taxChoiceBox.getItems().add("Very High Tax");

        // Set selection mode for listview in action province list to multiple
        unitsProvinceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    @Override
    public void update(ProvinceFeatureInfo p) {
        app.displayText("Selected province is: " + p.getName());
        if (p.getOwner().equals(game.getCurrentTurn())) {
            app.displayText("Selected province belongs to you.");
        }
        else {
            app.displayText("Selected province owner is: " + p.getOwner().getTitle());
        }
        selected_province.setText(p.getName());
        this.province = p.getProvince();
        // Show units in selected province
        for (Unit u : p.getProvince().getUnits()) {
            selectedProvinceUnitsList.setText(u.getName() + "\n");
        }
    }

    // Update province unit choicebox which deals with move/invade
    private void updateUnitList(Province p) {
        unitsProvinceListView.getItems().clear();
        if (p.getUnits() == null || p.getUnits().isEmpty()) {
            app.displayText("No more active units in province");
        }
        else {
            for (Unit u : p.getUnits()) {
                unitsProvinceListView.getItems().add(u);
            }
        }
    }

    // Updates list of units in training
    private void updateTrainingList(Province p) {
        //unitsInTraining.clear();
        unitsTrainingListView.getItems().clear();
        for (TrainingSlotEntry u : p.getCurrentTraining()) {
            unitsTrainingListView.getItems().add(u);
        }
    }

    // Updates tax/wealth info
    private void updateProvinceInfo(Province p) {
        wealthField.clear();
        wealthField.setText(Integer.toString(p.getWealth()));
        taxLevelField.clear();
        setTaxLevel(p.getTaxLevel());
        taxField.clear();
        taxField.setText(Double.toString(p.getTaxLevel().getTaxRate()));
    }

    // Calls the BattlePane
    private void addToApp(BattlePaneController b) {
        Pane root = (Pane)GloriaRomanusApplication.app.getSceneRoot();
        root.getChildren().add(b.getRoot());
    }

    private void setTaxLevel(TaxLevel t) {
        switch(t) {
            case LOW_TAX:
                taxLevelField.setText("Low Tax");
                break;
            case NORMAL_TAX:
                taxLevelField.setText("Normal Tax");
                break;
            case HIGH_TAX:
                taxLevelField.setText("High Tax");
                break;
            case VERY_HIGH_TAX:
                taxLevelField.setText("Very High Tax");
                break;
        }
    }

    // Clear all fields when turn ends
    private void refresh(TurnFeatureInfo o) {
        selected_province.clear();
        selectedProvinceUnitsList.clear();
        action_province.clear();
        wealthField.clear();
        taxLevelField.clear();
        taxField.clear();
        target_province.clear();
        unitsProvinceListView.getItems().clear();
        unitsTrainingListView.getItems().clear();
        trainChoiceBox.getItems().clear();
        trainTextField.clear();
    }

    ListProperty<Unit> getUnitSelectionProperty() {
    	return new SimpleListProperty<>(unitsProvinceListView.getSelectionModel().getSelectedItems());
    }
}