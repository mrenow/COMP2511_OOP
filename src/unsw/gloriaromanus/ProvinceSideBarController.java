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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import unsw.engine.*;
import unsw.ui.Observer.*;
import util.ArrayUtil;

import static unsw.gloriaromanus.GloriaRomanusApplication.app;;

/**
 * 
 * @author Derek
 */
public class ProvinceSideBarController extends Controller{

    private GameController game;
    private Property<ProvinceMouseEvent> targetProvince = new SimpleObjectProperty<ProvinceMouseEvent>();
    private Property<ProvinceMouseEvent> actionProvince = new SimpleObjectProperty<ProvinceMouseEvent>();
    
    @FXML private ChoiceBox<ItemType> trainChoiceBox;
    @FXML private ChoiceBox<TaxLevel> taxChoiceBox;
    @FXML private Button trainBtn;
    @FXML private Button moveBtn;
    @FXML private Button taxLevelBtn;
    @FXML private Button cancelTrainingBtn;
    @FXML private TextField wealthRateField;
    @FXML private TextField wealthField;
    @FXML private TextField taxField;
    @FXML private TextField action_province;
    @FXML private TextField target_province;
    @FXML private ListView<TrainingSlotEntry> unitsTrainingListView;
    @FXML private ListView<Unit> unitsProvinceListView;

    public ProvinceSideBarController() {}

    public ProvinceSideBarController(GameController game) {
        this.game = game;
        GloriaRomanusApplication.loadExistingController(this, "src/unsw/gloriaromanus/ProvinceSideBar.fxml");
        game.attatchTrainingChangedObserver(this::updateTrainingList);
        game.attatchTrainingChangedObserver(p -> updateTrainEnable());
        
        game.attatchUnitsChangedObserver(this::updateUnitList);
        game.attatchProvinceChangedObserver(this::updateProvinceInfo);
        game.attatchTurnChangedObserver(this::refresh);
    }

    @FXML
    public void initialize() {
    	
        taxChoiceBox.getItems().addAll(TaxLevel.values());

        // Set selection mode for listview in action province list to multiple
        unitsProvinceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        unitsProvinceListView.setCellFactory((self) -> new ListCell<Unit>() {
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
        unitsTrainingListView.setCellFactory((self) -> new ListCell<TrainingSlotEntry>() {
        	@Override
        	protected void updateItem(TrainingSlotEntry item, boolean empty) {
        		super.updateItem(item, empty);
        		if(empty) {
        			setText("");
        			setDisable(true);
        			return;
        		}
        		this.setGraphic(new ImageView(Images.ITEM_ICONS.get(item.getType())));
        		this.setContentDisplay(ContentDisplay.LEFT);
        		this.setText(item.toString());
        	}
        });

    	moveBtn.setText("Select Target");
    	moveBtn.setDisable(true);
    	trainChoiceBox.setOnAction((e)->updateTrainEnable());
    	taxChoiceBox.setOnAction((e)->handleTaxLevel());
    }

    // Handles button to train units in that province
    @FXML
    public void handleTrainBtn(ActionEvent e) {
        // Call train method
        game.trainUnit(actionProvince.getValue().getProvince(), trainChoiceBox.getValue());
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
        Province province = targetProvince.getValue().getProvince();
        if (targetProvince.getValue().canMove()) {
            // Call move method
            System.out.println("IM HERE");
            game.move(copy, province);
        }
        else if (targetProvince.getValue().canAttack()) {
            // Pass info needed for invasion to BattlePaneController
            GloriaRomanusApplication.app.addController(new BattlePaneController(game, copy, province));
        }
    }

    // Handles changing of tax level
    @FXML
    public void handleTaxLevel() {
        game.setTax(actionProvince.getValue().getProvince(), taxChoiceBox.getValue()); 
    }


    public void update(ProvinceMouseEvent p) {
        // Primary mouse button to determine action province
        if (p.getSource().getButton() == MouseButton.PRIMARY && p.getOwner().equals(game.getCurrentTurn())) {
            app.displayText("Selected province belongs to you.");

            actionProvince.setValue(p);
        	updateActionProvince(p.getProvince());
            app.displayText("Action province selected.");
        }
        // Secondary mouse button to determine target province
        else if (p.getSource().getButton() == MouseButton.SECONDARY) {
            app.displayText("Selected target province is: " + p.getName());
            targetProvince.setValue(p);
        	updateTargetProvince(p.getProvince());
        }
    }
    private void updateActionProvince(Province p) {
        
        
        // Clears the province unit choice box every time handle event is called
        unitsProvinceListView.getItems().clear();
        trainChoiceBox.getItems().clear();
        action_province.setText(p.getName());
        Province province = actionProvince.getValue().getProvince();
        // Update wealth and tax info

        taxChoiceBox.setValue(p.getTaxLevel());
        wealthField.setText(Integer.toString(province.getWealth()));
        taxField.setText(Double.toString(province.getTaxLevel().getTaxRate()));
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
        updateTrainEnable();
        
    }
    private void updateTargetProvince(Province p) {
        target_province.setText(p.getName());
        updateTargetButton();
    }

    // Update province unit choicebox which deals with move/invade
    private void updateUnitList(Province p) {
    	// throw away if we arent currently focusing on this province
    	if(!p.equals(actionProvince.getValue().getProvince())) return;
    	
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
        taxField.clear();
        taxField.setText(Double.toString(p.getTaxLevel().getTaxRate()));
    }


    // Clear all fields when turn ends
    private void refresh(TurnFeatureInfo o) {
        action_province.clear();
        wealthField.clear();
        taxField.clear();
        target_province.clear();
        unitsProvinceListView.getItems().clear();
        unitsTrainingListView.getItems().clear();
        trainChoiceBox.getItems().clear();
        targetProvince.setValue(null);
        actionProvince.setValue(null);
    }
    
    /**
     * Updates the state of the action button to match the current target province
     * 
     */
    private void updateTargetButton() {
        // Check if target province belongs to player faction
        if (targetProvince.getValue().canMove()){
            // Set button text to "Move"
            moveBtn.setText("Move");
        	moveBtn.setDisable(false);
        }
        else if (targetProvince.getValue().canAttack()){
            // Set button text to "Invade"
            moveBtn.setText("Invade");
        	moveBtn.setDisable(false);
        }else {
        	moveBtn.setText("Select Target");
        	moveBtn.setDisable(true);
        }
    }
    // observer
    private void updateTrainEnable() {
    	trainBtn.setDisable(
    			(actionProvince.getValue().getProvince().getTrainingSlots() <= 0) ||
    			(trainChoiceBox.getValue() == null) ||
    			(trainChoiceBox.getValue().getCost(1) > game.getCurrentTurn().getGold()));
    	
    }
    ListProperty<Unit> getUnitSelectionProperty() {
    	return new SimpleListProperty<>(unitsProvinceListView.getSelectionModel().getSelectedItems());
    }
    void addTargetChangedListener(ChangeListener<? super ProvinceMouseEvent> l) {
    	targetProvince.addListener(l);
    }
    void addActionChangedListener(ChangeListener<? super ProvinceMouseEvent> l) {
    	actionProvince.addListener(l);
    }
}