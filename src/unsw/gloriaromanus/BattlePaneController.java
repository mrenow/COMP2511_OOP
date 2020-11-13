package unsw.gloriaromanus;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import unsw.engine.Province;
import unsw.engine.Unit;

public class BattlePaneController extends Controller{
	
	@FXML
	private ScrollPane attackerPane;
	@FXML
	private ScrollPane defenderPane;
	@FXML
	private ScrollPane skirmishPane;
	@FXML
	private Button returnButton;
	@FXML
	private Button invadeButton;

	private Map<BattleSide, Province> provinces = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, List<Unit>> armies = new EnumMap<>(BattleSide.class);
	
	public BattlePaneController(List<Unit> attackerArmy, Province defender) {
		
		
		
	}
	@FXML
	public void initialize() {
		
	}
	
	public void onReturnButtonPressed() {
		
		
		
	}
	public void onInvadeButtonPressed() {
		
		
		
	}
	
	
	
}
