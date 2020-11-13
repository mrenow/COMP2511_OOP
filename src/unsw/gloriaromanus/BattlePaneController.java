package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.esri.arcgisruntime.mapping.view.Grid;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import unsw.engine.BattleSide;
import unsw.engine.Province;
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
	
	private Map<BattleSide, Province> provinces = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, List<Unit>> armies = new EnumMap<>(BattleSide.class);
	
	private int GRID_HEIGHT = 30;
	
	/**
	 * @pre attackerArmy cannot be empty
	 * @param attackerArmy
	 * @param defender
	 */
	public BattlePaneController(List<Unit> attackerArmy, Province defender) {
		attackerArmy = new ArrayList<>(attackerArmy);
		
		provinces.put(ATTACK, attackerArmy.get(0).getProvince());
		provinces.put(DEFEND, defender);
		armies.put(ATTACK, attackerArmy);
		armies.put(DEFEND, defender.getUnits());
		// Corresponds to display order
		Collections.sort(armies.get(ATTACK));
		Collections.sort(armies.get(DEFEND));
	}
	
	@FXML
	public void initialize() {
		attackerPane.setFitToWidth(true);
		defenderPane.setFitToWidth(true);
		skirmishPane.setFitToWidth(true);
	
		returnButton.setOnAction(this::onReturnButtonPressed);
		invadeButton.setOnAction(this::onInvadeButtonPressed);
		
		initArmyPane(attackerGridPane, ATTACK);
		initArmyPane(defenderGridPane, DEFEND);

	}
	private void initArmyPane(GridPane armyPane, BattleSide side) {
		armyPane.getColumnConstraints().get(0).setPrefWidth(GRID_HEIGHT);
		
		int index = 0;
		for(Unit u : armies.get(ATTACK)) {
			armyPane.getRowConstraints().add(new RowConstraints(GRID_HEIGHT));
			armyPane.add(new Label(u.getName()), 0, index);
			armyPane.add(new Label(u.statRep()), 1, index);
			index++;
		}
	}
	
	public void onReturnButtonPressed(ActionEvent e) {
		
	}
	public void onInvadeButtonPressed(ActionEvent e) {
		
		
		
	}
	
	
	
}
