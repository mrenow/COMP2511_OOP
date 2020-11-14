package unsw.gloriaromanus;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.esri.arcgisruntime.mapping.view.Grid;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import unsw.engine.BattleInfo;
import unsw.engine.BattleSide;
import unsw.engine.GameController;
import unsw.engine.Province;
import unsw.engine.SkirmishInfo;
import unsw.engine.Unit;
import static unsw.engine.BattleSide.*;

public class BattlePaneController extends Controller{
	@FXML private TitledPane titlePane;
	@FXML private ScrollPane attackerPane;
	@FXML private ScrollPane defenderPane;
	@FXML private ScrollPane skirmishPane;
	@FXML private GridPane attackerGridPane;
	@FXML private GridPane defenderGridPane;
	@FXML private GridPane skirmishGridPane;
	@FXML private Button returnButton;
	@FXML private Button invadeButton;
	
	private GameController game;
	private Map<BattleSide, Province> provinces = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, List<Unit>> armies = new EnumMap<>(BattleSide.class);
	
	private Map<BattleSide, List<List<Label>>> labelGrids = new EnumMap<>(BattleSide.class);
	
	private int GRID_HEIGHT = 25;
	private int NUM_COLUMNS = 9;
	
	private Image[] displayOrder = {
			null, 				// 0, uses the u
			null,				// 1
			Images.HEALTH_ICON, // 2
			Images.ATTACK_ICON,	// 3
			Images.ARMOUR_ICON,	// 4
			Images.SHIELD_ICON,	// 5
			Images.SKILL_ICON,	// 6
			Images.MORALE_ICON,	// 7
			Images.SPEED_ICON	// 8
			};

	
//	private Image HEALTH_ICON = new Image(new InputStream(new File("images/UISprites/health_icon.png")));
//	private Image HEALTH_ICON = new Image(new InputStream(new File("images/UISprites/health_icon.png")));
	
	
	/**
	 * @pre attackerArmy cannot be empty
	 * @param attackerArmy
	 * @param defender
	 */
	public BattlePaneController(GameController game, List<Unit> attackerArmy, Province defender) {
		this.game = game;
		attackerArmy = new ArrayList<>(attackerArmy);
		provinces.put(ATTACK, attackerArmy.get(0).getProvince());
		provinces.put(DEFEND, defender);
		armies.put(ATTACK, attackerArmy);
		armies.put(DEFEND, defender.getUnits());
		labelGrids.put(ATTACK, new ArrayList<>());
		labelGrids.put(DEFEND, new ArrayList<>());
		// Corresponds to display order
		Collections.sort(armies.get(ATTACK));
		Collections.sort(armies.get(DEFEND));

		GloriaRomanusApplication.loadExistingController(this, "src/unsw/gloriaromanus/battle.fxml");
	}
	
	@FXML
	public void initialize() {
		titlePane.setText("Battle of " + provinces.get(DEFEND).getName());
		
		attackerPane.setFitToWidth(true);
		defenderPane.setFitToWidth(true);
		skirmishPane.setFitToWidth(true);
	
		returnButton.setOnAction(e -> destroy());
		invadeButton.setOnAction(this::onInvadeButtonPressed);
		
		initArmyPane(attackerGridPane, ATTACK);
		initArmyPane(defenderGridPane, DEFEND);
	}
	
	private void initArmyPane(GridPane armyPane, BattleSide side) {
		// set up columns
		List<List<Label>> labelGrid = labelGrids.get(side);
		armyPane.getColumnConstraints().get(0).setMinWidth(GRID_HEIGHT);
		armyPane.getColumnConstraints().get(0).setPrefWidth(GRID_HEIGHT);
		armyPane.setGridLinesVisible(true);
		while(armyPane.getColumnCount() < NUM_COLUMNS) {
			armyPane.getColumnConstraints().add(new ColumnConstraints(GRID_HEIGHT));
		}
		// Set up labels and rows
		int row = 0;
		for(Unit u : armies.get(side)) {
			labelGrid.add(new ArrayList<>());
			armyPane.getRowConstraints().add(new RowConstraints(GRID_HEIGHT));
			
			Label unitIcon = new Label(stringMap(u, 0));
			unitIcon.setTextAlignment(TextAlignment.CENTER);
			unitIcon.setFont(new Font(25));
			unitIcon.setTextFill(Color.RED);
			
			unitIcon.setGraphic(new ImageView(Images.ITEM_ICONS.get(u.getType())));
			unitIcon.setContentDisplay(ContentDisplay.CENTER);

			armyPane.add(unitIcon, 0, row);
			labelGrid.get(row).add(unitIcon);
			for (int col = 1; col < NUM_COLUMNS; col++) {
				if(displayOrder[col] != null) {
					ImageView bg = new ImageView(displayOrder[col]);
					Label l = new Label(stringMap(u, col), bg);
					l.setContentDisplay(ContentDisplay.CENTER);
					l.setFont(new Font(15));
					
					armyPane.add(l, col, row);
					labelGrid.get(row).add(l);
				}else {
					Label l = new Label(stringMap(u, col));
					armyPane.add(l, col, row);
					labelGrid.get(row).add(l);
				}
			}
			row++;
		}
		// centre components
		armyPane.getColumnConstraints().forEach((c)->c.setHalignment(HPos.CENTER));
		armyPane.getRowConstraints().forEach((r)->r.setValignment(VPos.CENTER));
	}
	
	private void updateArmyPane(GridPane armyPane, BattleSide side) {
		List<List<Label>> labelGrid = labelGrids.get(side);
		for (int row = 0; row < labelGrid.size(); row ++) {
			List<Label> gridRow = labelGrid.get(row);
			Unit u = armies.get(side).get(row);
			if(!u.isAlive()) {
				gridRow.get(0).setGraphic(null);
				gridRow.get(0).setText("☠");
				
			}
			for (int col = 1; col < gridRow.size(); col ++) {
				Label label = gridRow.get(col);
				label.setText(stringMap(u, GridPane.getColumnIndex(label)));
			}
		}
	}
	
	private String stringMap(Unit u, int column) {
		switch(column) {
		case 0: return "";
		case 1: return u.getName();
		case 2: return Integer.toString(u.getHealth());
		case 3: return Integer.toString((int)u.getAttack());
		case 4: return Integer.toString((int)u.getArmour());
		case 5: return Integer.toString((int)u.getShieldDefense());
		case 6: return Integer.toString((int)u.getDefenseSkill());
		case 7: return Integer.toString((int)u.getMorale());
		case 8: return Integer.toString((int)u.getSpeed());
		default: return "";
		}
	}
	
	
	public void onInvadeButtonPressed(ActionEvent e) {
		invadeButton.setDisable(true);
		BattleInfo result = game.invade(armies.get(ATTACK), provinces.get(DEFEND));
		
		updateArmyPane(attackerGridPane, ATTACK);
		updateArmyPane(defenderGridPane, DEFEND);
		
		for (SkirmishInfo s : result.getSkirmishes()) {
			// Rudimentary, will do for now
			int index = skirmishGridPane.getRowCount();
			skirmishGridPane.getRowConstraints().add(new RowConstraints(GRID_HEIGHT));
			String attackMessage = String.format("%s(%d->%d) %s", s.getUnit(ATTACK).getName(), s.getPrevHealth(ATTACK), s.getNextHealth(ATTACK), s.getResult(ATTACK));
			String defendMessage = String.format("%s(%d->%d) %s", s.getUnit(DEFEND).getName(), s.getPrevHealth(DEFEND), s.getNextHealth(DEFEND), s.getResult(DEFEND));

			skirmishGridPane.add(new Label(attackMessage), 0 , index);
			skirmishGridPane.add(new Label(defendMessage), 1 , index);
			
		}
	}
}
