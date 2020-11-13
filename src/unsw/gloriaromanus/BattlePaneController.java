package unsw.gloriaromanus;

import java.io.IOException;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import unsw.engine.BattleInfo;
import unsw.engine.BattleSide;
import unsw.engine.GameController;
import unsw.engine.Province;
import unsw.engine.SkirmishInfo;
import unsw.engine.Unit;
import static unsw.engine.BattleSide.*;

public class BattlePaneController extends Controller{

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
	
	private int GRID_HEIGHT = 30;
	private int NUM_COLUMNS = 9;
	
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
		// Corresponds to display order
		Collections.sort(armies.get(ATTACK));
		Collections.sort(armies.get(DEFEND));

		try {
			GloriaRomanusApplication.loadExistingController(this, "src/unsw/gloriaromanus/battle.fxml");
		} catch (IOException e) {
			System.err.println("Failed to load!!");
			e.printStackTrace();
		}
	}
	
	@FXML
	public void initialize() {
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
		armyPane.getColumnConstraints().get(0).setMinWidth(GRID_HEIGHT);
		armyPane.getColumnConstraints().get(0).setPrefWidth(GRID_HEIGHT);
//		armyPane.setGridLinesVisible(true);
		while(armyPane.getColumnCount() < NUM_COLUMNS) {
			armyPane.getColumnConstraints().add(new ColumnConstraints(GRID_HEIGHT));
		}
		// Set up labels and rows
		int row = 0;
		for(Unit u : armies.get(side)) {
			armyPane.getRowConstraints().add(new RowConstraints(GRID_HEIGHT));
			Label deathMarker = new Label(valueMap(u, 0));
			deathMarker.setFont(new Font(23));
			armyPane.add(deathMarker, 0, row);
			for (int col = 1; col < NUM_COLUMNS; col++) {
				armyPane.add(new Label(valueMap(u, col)), col, row);
			}
			row++;
		}
		// centre components
		armyPane.getColumnConstraints().forEach((c)->c.setHalignment(HPos.CENTER));
		armyPane.getRowConstraints().forEach((r)->r.setValignment(VPos.CENTER));
	}
	private void updateArmyPane(GridPane armyPane, BattleSide side) {
		for(Node node : armyPane.getChildren()) {
			Label label = (Label) node;
			
			int index = GridPane.getRowIndex(label);
			Unit u = armies.get(side).get(index);
			label.setText(valueMap(u, GridPane.getColumnIndex(label)));
		}
	}
	
	private String valueMap(Unit u, int column) {
		switch(column) {
		case 0: return u.isAlive() ? "" : "☠";
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
		BattleInfo result = game.invade(armies.get(ATTACK), provinces.get(DEFEND));
		updateArmyPane(attackerGridPane, ATTACK);
		updateArmyPane(defenderGridPane, DEFEND);
		
		for (SkirmishInfo s : result.getSkirmishes()) {
			// Rudimentary, will do for now
			int index = skirmishGridPane.getRowCount();
			skirmishGridPane.getRowConstraints().add(new RowConstraints(GRID_HEIGHT));
			String attackMessage = s.getUnit(ATTACK).getName() + " " + s.getResult(ATTACK);
			String defendMessage = s.getUnit(DEFEND).getName() + " " + s.getResult(DEFEND);   
			skirmishGridPane.add(new Label(attackMessage), 0 , index);
			skirmishGridPane.add(new Label(defendMessage), 1 , index);
			
		}
	}
}
