package unsw.gloriaromanus;

import static unsw.engine.BattleSide.ATTACK;
import static unsw.engine.BattleSide.DEFEND;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import unsw.engine.GameController;
import unsw.engine.Province;
import unsw.engine.Unit;

public class SavePaneController extends Controller{
	
	private GameController game;
	@FXML private TextField fileTextField;
	public SavePaneController(GameController game) {
		this.game = game;
		GloriaRomanusApplication.loadExistingController(this, "src/unsw/gloriaromanus/battle.fxml");
	
	
	@FXML
	private void save() {
		try {
			game.saveGame(fileTextField.getText());
		} catch (IOException e) {
			System.err.println("Couldnt save");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	} 
	
}
