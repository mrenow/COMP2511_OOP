package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import unsw.engine.*;
import unsw.ui.Observer.*;

import static unsw.gloriaromanus.GloriaRomanusApplication.app;;

/**
 * 
 * @author Derek
 */
public class ProvinceSideBarController extends Controller{

    private GameController game;
    private Property<Province> selectedProvince = new SimpleObjectProperty<Province>();
    private Property<Province> targetProvince = new SimpleObjectProperty<Province>();
    private Property<Province> actionProvince = new SimpleObjectProperty<Province>();
    
    @FXML private ChoiceBox<ItemType> trainChoiceBox;
    @FXML private ChoiceBox<TaxLevel> taxChoiceBox;
    @FXML private Button trainBtn;
    @FXML private Button moveBtn;
    @FXML private Button taxLevelBtn;
    @FXML private Button selectActionProvince;
    @FXML private Button selectTargetProvince;
    @FXML private Button cancelTrainingBtn;
    @FXML private TextField wealthRateField;
    @FXML private TextField wealthField;
    @FXML private TextField taxField;
    @FXML private TextField taxLevelField;
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
        // Call train method
        game.trainUnit(actionProvince.getValue(), trainChoiceBox.getValue());
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
        if (game.getDestinations(copy).contains(targetProvince.getValue())) {
            // Call move method
            System.out.println("IM HERE");
            game.move(copy, targetProvince.getValue());
        }
        else if (game.getAttackable(copy).contains(targetProvince.getValue())) {
            // Pass info needed for invation to BattlePaneController
            BattlePaneController b = new BattlePaneController(game, copy, targetProvince.getValue());
            addToApp(b);
        }
    }

    // Handles changing of tax level
    @FXML
    public void handleTaxLevel() {
        game.setTax(actionProvince.getValue(), taxChoiceBox.getValue()); 
    }

    // Handles button that selects target province
    @FXML
    public void handleSelectTarget(ActionEvent e) {
    	Province province = selectedProvince.getValue();
        targetProvince.setValue(province);
        target_province.setText(province.getName());
        // Check if target province belongs to player faction
        if (province.getOwner().equals(game.getCurrentTurn())) {
            // Set button text to "Move"
            moveBtn.setText("Move");
        }
        // Else target province belongs to enemy
        else {
            // Set button text to "Invade"
            moveBtn.setText("Invade");
        }
        app.displayText("Selected province is: " + province.getName());
    }

    @FXML
    public void initialize() {
    	
        taxChoiceBox.getItems().addAll(TaxLevel.values());

        // Set selection mode for listview in action province list to multiple
        unitsProvinceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        unitsProvinceListView.setCellFactory((param) -> new ListCell<Unit>() {
            @Override 
            protected void updateItem(Unit item, boolean empty) {
                super.updateItem(item, empty);
                if(!empty) {
                	setDisable(!item.canAttack());
                    setText(item.toString());
                }else {
                	setText("");
                	setDisable(true);
                }
            }
    	});  
    }

    public void update(ProvinceMouseEvent p) {
        // Primary mouse button to determine action province
        if (p.getSource().getButton() == MouseButton.PRIMARY && p.getOwner().equals(game.getCurrentTurn())) {
            app.displayText("Selected province belongs to you.");
            // Clears the province unit choice box every time handle event is called
            unitsProvinceListView.getItems().clear();
            trainChoiceBox.getItems().clear();
            action_province.setText(p.getName());
            actionProvince.setValue(p.getProvince());
            Province province = actionProvince.getValue();
            // Update wealth and tax info
            wealthField.setText(Integer.toString(province.getWealth()));
            taxField.setText(Double.toString(province.getTaxLevel().getTaxRate()));
            setTaxLevel(province.getTaxLevel());
            wealthRateField.setText(Integer.toString(province.getTaxLevel().getwealthgen()));
            // Update province choicebox accordingly with units
            if (!province.getUnits().isEmpty() || province.getUnits() != null) {
                for (Unit u : province.getUnits()) {
                    unitsProvinceListView.getItems().add(u);
                }
            }
            else {
                app.displayText("There are no units currently in selected province.");
            }
            // Update units currently in training for that province in listview
            List<TrainingSlotEntry> copy = new ArrayList<>(province.getCurrentTraining());
            for (TrainingSlotEntry u : copy) {
                unitsTrainingListView.getItems().add(u);
            }
            // Update Trainable Units
            List<ItemType> trainableUnits = province.getTrainable();
            for (ItemType u : trainableUnits) {
                trainChoiceBox.getItems().add(u);
            }
            app.displayText("Action province selected.");
        }
        // Secondary mouse button to determine target province
        else if (p.getSource().getButton() == MouseButton.SECONDARY) {
            app.displayText("Selected target province is: " + p.getName());
            targetProvince.setValue(p.getProvince());
            target_province.setText(p.getName());
            Province province = targetProvince.getValue();
            // Check if target province belongs to player faction
            if (province.getOwner().equals(game.getCurrentTurn())) {
                // Set button text to "Move"
                moveBtn.setText("Move");
            }
            // Else target province belongs to enemy
            else {
                // Set button text to "Invade"
                moveBtn.setText("Invade");
            }
        }
    }

    // Update province unit choicebox which deals with move/invade
    private void updateUnitList(Province p) {
    	if(!p.equals(actionProvince.getValue())) return;
        unitsProvinceListView.getItems().clear();
        if (p.getUnits() == null || p.getUnits().isEmpty()) {
            app.displayText("No more active units in province");
        } else {
            for (Unit u : p.getUnits()) {
                unitsProvinceListView.getItems().add(u);
            }
        }
    }

    // Updates list of units in training
    private void updateTrainingList(Province p) {
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
        targetProvince.setValue(null);
        selectedProvince.setValue(null);
        actionProvince.setValue(null);
    }
    void updateTrainEnable() {
    	trainBtn.setDisable((actionProvince.getValue().getTrainingSlots() <= 0) ||
    			(trainChoiceBox.getValue().getCost(1) > game.getCurrentTurn().getGold()));
    }
    ListProperty<Unit> getUnitSelectionProperty() {
    	return new SimpleListProperty<>(unitsProvinceListView.getSelectionModel().getSelectedItems());
    }
    void addTargetChangedListener(ChangeListener<? super Province> l) {
    	targetProvince.addListener(l);
    }
    void addSelectedChangedListener(ChangeListener<? super Province> l) {
    	selectedProvince.addListener(l);
    }
    void addActionChangedListener(ChangeListener<? super Province> l) {
    	actionProvince.addListener(l);
    }
}