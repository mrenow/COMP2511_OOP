package unsw.ui.MainMenu;

import java.io.IOException;

import javafx.fxml.FXML;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.ui.UIPath;
import unsw.ui.Observer.Observer;
import unsw.ui.Observer.TurnFeatureInfo;

public class MainMenuController extends Controller implements Observer<TurnFeatureInfo>{
    
    
    private void initialize() throws Exception {

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
