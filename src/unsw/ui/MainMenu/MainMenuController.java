package unsw.ui.MainMenu;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.ui.Observer.Observer;
import unsw.ui.Observer.TurnFeatureInfo;

public class MainMenuController extends Controller implements Observer<TurnFeatureInfo>{
    @FXML private Button newGame;
    
    private void initialize() throws Exception {

    }

    @FXML
    public void newGame()throws Exception{
        Controller mainMenu = GloriaRomanusApplication.loadController("src/unsw/ui/MainMenu/MainMenu.fxml");
        System.out.println("here");
        GloriaRomanusApplication.app.setScene(mainMenu);;
        
    
    }
    @FXML
    public void loadsave(){

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
