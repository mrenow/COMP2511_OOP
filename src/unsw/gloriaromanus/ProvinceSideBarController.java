package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.StringConverter;
import unsw.engine.*;
import unsw.ui.Observer.*;
import util.ArrayUtil;
import util.Concatenator;

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
    @FXML private Button cancelTrainingBtn;
    @FXML private TextField wealthRateField;
    @FXML private TextField wealthField;
    @FXML private TextField taxField;
    @FXML private TextField action_province;
    @FXML private TextField target_province;
    @FXML private Label numSlotsLabel;
    @FXML private ListView<TrainingSlotEntry> unitsTrainingListView;
    @FXML private ListView<Unit> unitsProvinceListView;
    
    private Tooltip trainingTooltip = new Tooltip();

    public ProvinceSideBarController() {}

    public ProvinceSideBarController(GameController game) {
        this.game = game;
        GloriaRomanusApplication.loadExistingController(this, "src/unsw/gloriaromanus/ProvinceSideBar.fxml");
        game.attatchTrainingChangedObserver(this::updateTrainingList);
        game.attatchTrainingChangedObserver(p -> updateTrainButton());
        
        game.attatchUnitsChangedObserver(this::updateUnitList);
        game.attatchProvinceChangedObserver(this::updateTaxDisplay);
        game.attatchTurnChangedObserver(e -> refresh());
    }

    @FXML
    public void initialize() {
    	
        taxChoiceBox.getItems().addAll(TaxLevel.values());
        taxChoiceBox.setOnAction((e)->handleTaxLevel());
		
        // Set selection mode for listview in action province list to multiple
        unitsProvinceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        unitsProvinceListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Unit>)(c->updateTargetButton()));
        unitsProvinceListView.setCellFactory((self) -> new ListCell<Unit>() {
            @Override 
            protected void updateItem(Unit item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                	setGraphic(null);
                	setDisable(true);
                }else {
                	setGraphic(createUnitEntry(item));
                	setDisable(!item.canAttack());
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
	        		setGraphic(null);
        			return;
        		}else {
	        		setGraphic(new ImageView(Images.ITEM_ICONS.get(item.getType())));
	        		setContentDisplay(ContentDisplay.LEFT);
	        		setText(item.toString());
	    			setDisable(false);
        		}
        	}
        });
        unitsTrainingListView
        	.getSelectionModel()
        	.getSelectedItems()
        	.addListener((ListChangeListener<TrainingSlotEntry>)
        			(c->cancelTrainingBtn.setDisable(c.getList().isEmpty())));
        
        unitsTrainingListView
        	.getItems()
        	.addListener((ListChangeListener<TrainingSlotEntry>)(c->updateNumTrainingSlotsLabel()));
        
        
        			
    	moveBtn.setText("Select Target");
    	moveBtn.setDisable(true);
    	trainChoiceBox.setOnAction((e)->{
    		updateTrainButton();
    		trainingTooltip.setText(getTrainingDescription(trainChoiceBox.getSelectionModel().getSelectedItem()));
    	});
    	trainChoiceBox.setConverter(new StringConverter<ItemType>() {

			@Override
			public String toString(ItemType item) {
				return String.format("%d Gold,\t %s", item.getCost(1), item.toString());
			}

			@Override
			public ItemType fromString(String string) {
				return null;
			}
    	});
    	trainChoiceBox.setTooltip(trainingTooltip);
    	trainingTooltip.setShowDelay(Duration.millis(0));
    	trainingTooltip.setWrapText(true);
    	trainingTooltip.setMaxWidth(300);
    	
    	actionProvince.addListener((ChangeListener<ProvinceMouseEvent>)((o,prev,next)->{
    		if(!Objects.equals(prev,next)) updateActionProvince();
    	}));
    	targetProvince.addListener((ChangeListener<ProvinceMouseEvent>)((o,prev,next)->{
    		if(!Objects.equals(prev,next)) updateTargetProvince();
    	}));
		
    	refresh();
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
        ArrayList<TrainingSlotEntry> copy = new ArrayList<>(unitsTrainingListView.getSelectionModel().getSelectedItems());
        app.displayText("Cancelling training for: " + copy.toString());
        for (TrainingSlotEntry t : copy) {
            game.cancelTraining(t);
        }
    }
    
    // Handles button to move to allied province or attack enemy province
    @FXML
    public void handleMove(ActionEvent e) {
        ArrayList<Unit> copy = new ArrayList<>(unitsProvinceListView.getSelectionModel().getSelectedItems());
        Province province = targetProvince.getValue().getProvince();
        if (targetProvince.getValue().canMove()) {
            // Call move method
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
    	if(taxChoiceBox.getValue() == null) return;
        game.setTax(actionProvince.getValue().getProvince(), taxChoiceBox.getValue()); 
    }


    public void update(ProvinceMouseEvent p) {
        // Primary mouse button to determine action province
        if (p.getSource().getButton() == MouseButton.PRIMARY && p.getOwner().equals(game.getCurrentTurn())) {
            app.displayText("Selected province belongs to you.");

            actionProvince.setValue(p);
            app.displayText("Action province selected.");
        }
        // Secondary mouse button to determine target province
        else if (p.getSource().getButton() == MouseButton.SECONDARY) {
            app.displayText("Selected target province is: " + p.getName());
            targetProvince.setValue(p);
        }
    }
    private void updateActionProvince() {
        // Clears the province unit choice box every time handle event is called
    	targetProvince.setValue(null);
        unitsProvinceListView.getItems().clear();
        unitsTrainingListView.getItems().clear();
        trainChoiceBox.getItems().clear();
        trainingTooltip.setText("Select Unit");

    	if(actionProvince.getValue() == null) {

            trainChoiceBox.setDisable(true);
            cancelTrainingBtn.setDisable(true);
            trainBtn.setDisable(true);
            taxChoiceBox.setDisable(true);
            taxChoiceBox.setValue(null);
            
            numSlotsLabel.setText("");
            trainingTooltip.setText("");
            action_province.clear();
            wealthRateField.clear();
            wealthField.clear();
            taxField.clear();
            
    	}else {
    		Province p = actionProvince.getValue().getProvince();

            trainChoiceBox.setDisable(false);
            taxChoiceBox.setDisable(false);
	        action_province.setText(String.format("%s (%s)", p.getName(), p.getOwner()));
	        
	        // Update wealth and tax info
	        taxChoiceBox.setValue(p.getTaxLevel()); // performs redundant tax change
	        updateTaxDisplay(p);
	        // Update province choicebox accordingly with units
	        if (!p.getUnits().isEmpty() || p.getUnits() != null) {
	            for (Unit u : p.getUnits()) {
	                unitsProvinceListView.getItems().add(u);
	            }
	        } else {
	            app.displayText("There are no units currently in selected province.");
	        }
	        // Update units currently in training for that province in listview
	        for (TrainingSlotEntry u : p.getCurrentTraining()) {
	            unitsTrainingListView.getItems().add(u);
	        }
	        updateNumTrainingSlotsLabel();
	        
	        // Update Trainable Units
	        for (ItemType u : p.getTrainable()) {
	            trainChoiceBox.getItems().add(u);
	        }
	        updateTrainButton();
    	}
        
    }
    private void updateTargetProvince() {
    	if(targetProvince.getValue() == null) {
            targetProvince.setValue(null);
            target_province.clear();
    	} else {
    		Province p = targetProvince.getValue().getProvince();
    		target_province.setText(String.format("%s (%s)", p.getName(), p.getOwner()));
    	}
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
    private void updateTaxDisplay(Province p) {
        wealthField.setText(Integer.toString(p.getWealth()));
        taxField.setText(p.getTaxLevel().getTaxPercentage() + "%");
        wealthRateField.setText(Integer.toString(p.getTaxLevel().getWealthGen()));
    }


    // Clear all fields when turn ends
    private void refresh() {
    	actionProvince.setValue(null);
    	targetProvince.setValue(null);
        updateActionProvince();
        updateTargetProvince();
    }
    
    /**
     * Updates the state of the action button to match the current target province
     * 
     */
    private void updateTargetButton() {
        // Check if target province belongs to player faction
        if(targetProvince.getValue() == null) {
        	moveBtn.setText("Select Target");
        	moveBtn.setDisable(true);
        }else if (targetProvince.getValue().canMove()){
            // Set button text to "Move"
            moveBtn.setText("Move to Target");
        	moveBtn.setDisable(false);
        }
        else if (targetProvince.getValue().canAttack()){
            // Set button text to "Invade"
            moveBtn.setText("Invade Target");
        	moveBtn.setDisable(false);
        }
    }
    private void updateNumTrainingSlotsLabel() {
    	if(actionProvince.getValue() == null) return;
		Province p = actionProvince.getValue().getProvince();
		numSlotsLabel.setText(String.format("%d/%d Free Slots", p.getTrainingSlots(), p.getMaxTrainingSlots()));
    }
    // observer
    private void updateTrainButton() {
    	trainBtn.setDisable(
    			(actionProvince.getValue() == null) ||
    			(actionProvince.getValue().getProvince().getTrainingSlots() <= 0) ||
    			(trainChoiceBox.getValue() == null) ||
    			(trainChoiceBox.getValue().getCost(1) > game.getCurrentTurn().getGold()));
    }
    ListProperty<Unit> getUnitSelectionProperty() {
    	return new SimpleListProperty<>(unitsProvinceListView.getSelectionModel().getSelectedItems());
    }
    // Pass the mouse event back so that destinations etc do not need to be recomputed.
    void addTargetChangedListener(ChangeListener<? super ProvinceMouseEvent> l) {
    	targetProvince.addListener(l);
    }
    void addActionChangedListener(ChangeListener<? super ProvinceMouseEvent> l) {
    	actionProvince.addListener(l);
    }
    
    private String getTrainingDescription(ItemType t) {
    	if(t == null) return "Select Unit";
    	StringBuilder s = new StringBuilder();
    	s.append(t.getDescription(1));
    	s.append("\n\n");
    	for (String attr : new String[]{"health", "attack", "armour", "shieldDefense", "defenseSkill", "morale", "speed"}){
    		s.append(String.format("%s: %d\n", attr, t.getAttributeOrNull(attr, 1)));
    	}
    	s.append("abilities:\n\n");
    	for (ModifierMethod<?> m : new Concatenator<>(
    	Parsing.getEnums(t.getAttribute("combatModifiers", 1), CombatModifierMethod.class),
    	Parsing.getEnums(t.getAttribute("moraleModifiers", 1), MoraleModifierMethod.class))) {
    		s.append(String.format("\t%s: %s\n", m, m.getDescription()));
    	}
    	return s.toString();
    }
    private Node createUnitEntry(Unit u) {
    	Insets margin = new Insets(3,0,3,0);
    	HBox out = new HBox();
    	Label typeLabel = new Label(u.getName());
    	typeLabel.setContentDisplay(ContentDisplay.LEFT);
    	typeLabel.setGraphic(new ImageView(Images.ITEM_ICONS.get(u.getType())));
    	typeLabel.setPrefWidth(120);
    	HBox.setMargin(typeLabel, margin);
    	out.getChildren().add(typeLabel);
    	
    	Label healthLabel = new Label(Integer.toString(u.getHealth()));
    	healthLabel.setContentDisplay(ContentDisplay.CENTER);
    	healthLabel.setGraphic(new ImageView(Images.HEALTH_ICON));

    	HBox.setMargin(healthLabel,margin);
    	out.getChildren().add(healthLabel);
    	
    	Label moveLabel = new Label(String.format("%s/%s", u.getMovPoints(), u.getMaxMovPoints()));
    	moveLabel.setContentDisplay(ContentDisplay.CENTER);
    	moveLabel.setGraphic(new ImageView(Images.MOV_POINT_ICON));
    	HBox.setMargin(moveLabel, margin);
    	out.getChildren().add(moveLabel);
    	
    	return out;
    	
    }
    
}