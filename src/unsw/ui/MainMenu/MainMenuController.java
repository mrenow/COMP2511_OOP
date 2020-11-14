package unsw.ui.MainMenu;

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
    public void newGame()throws Exception{
        try {
            Controller controller = GloriaRomanusApplication.loadController(UIPath.NEWGAME.getPath());
            GloriaRomanusApplication.app.setScene(controller);
        } catch (Exception e) {
            System.out.println("setting fild DNE");
        }
    
    }
    @FXML
    public void loadsave(){
        try {
            Controller controller = GloriaRomanusApplication.loadController(UIPath.LOADSAVE.getPath());
            GloriaRomanusApplication.app.setScene(controller);
        } catch (Exception e) {
            System.out.println("setting fild DNE");
        }
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
