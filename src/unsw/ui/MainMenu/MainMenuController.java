package unsw.ui.MainMenu;

import java.io.FileInputStream;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.ui.UIPath;
import unsw.ui.Observer.Observer;
import unsw.ui.Observer.TurnFeatureInfo;

public class MainMenuController extends Controller implements Observer<TurnFeatureInfo>{
    
    @FXML private ImageView sidepicture;
    @FXML
	public void initialize() throws Exception {
        Image mappic = new Image(new FileInputStream("src/unsw/ui/MainMenu/map.png"));
        sidepicture.setImage(mappic);
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

    }
    @FXML
    public void quit(){

    }
    @Override
    public void update(TurnFeatureInfo message) {
        // TODO Auto-generated method stub
        
    }
}
