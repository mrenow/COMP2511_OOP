package unsw.ui.VicUI;

import java.io.FileInputStream;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import unsw.engine.GameController;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.gloriaromanus.GloriaRomanusController;
import unsw.ui.UIPath;

public class VicUIController extends Controller{

    @FXML private AnchorPane pane;
    @FXML private Label victorLabel;
	private GameController game;
	private String winner;
    
    public VicUIController(GameController game, String winner) {
    	this.game = game;
    	this.winner = winner;
    	GloriaRomanusApplication.loadExistingController(this, UIPath.VIC.getPath());
    }
    @FXML
    private void initialize() {

    	victorLabel.setText("VICTORY FOR\n" +  winner.toUpperCase());
    }
    @FXML
    private void quit(){
        Controller controller = GloriaRomanusApplication.loadController(UIPath.MENU.getPath());
        GloriaRomanusApplication.app.setScene(controller);
    }
    @FXML
    private void resume() {
    	GloriaRomanusApplication.app.setScene(new GloriaRomanusController(game));		
    }

}
