package unsw.ui.MainMenu;

import java.io.FileInputStream;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import unsw.engine.GameController;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import static unsw.gloriaromanus.GloriaRomanusApplication.app;
import unsw.gloriaromanus.GloriaRomanusController;
import unsw.ui.UIPath;
import unsw.ui.Observer.Observer;
import unsw.ui.Observer.TurnFeatureInfo;

public class MainMenuController extends Controller{
    private GameController game;
    @FXML private ImageView sidepicture;
    @FXML private ImageView mainpicture;
    @FXML private VBox menu;
    @FXML private Button resume;
    @FXML
	public void initialize() throws Exception {
        Image mappic = new Image(new FileInputStream("src/unsw/ui/MainMenu/map.png"));
        //sidepicture.setImage(mappic);
        //mainpicture.setImage(mappic);

        
    }

    @FXML
    public void newGame(){
        
        Controller controller = GloriaRomanusApplication.loadController(UIPath.NEWGAME.getPath());
        GloriaRomanusApplication.app.setScene(controller);
    
    }
    @FXML
    public void loadsave(){
        Controller controller = GloriaRomanusApplication.loadController(UIPath.LOADSAVE.getPath());
        GloriaRomanusApplication.app.setScene(controller);
    }
    @FXML
    public void setting(){
        System.out.println("where is setting?");
    }
    @FXML
    public void quit(){
        GloriaRomanusApplication.app.stop();
    }
    public void setGame(GameController game){
        this.game = game;
        resume.onActionProperty().set(c-> resumeGame());
        resume.setDisable(false);
    }
    private void resumeGame(){
        GameController game = GameController.loadFromSave(UIPath.TMP.getPath());
    
    	GloriaRomanusController controller = new GloriaRomanusController(game);
        app.setScene(controller);
    }
    // @Override
    // public void update(TurnFeatureInfo message) {
    //     // TODO Auto-generated method stub
        
    // }
}
