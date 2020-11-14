package unsw.ui.topbar;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import unsw.engine.GameController;
import unsw.engine.VicCondition.VicComposite;
import unsw.engine.VicCondition.VictoryCondition;
import unsw.ui.Observer.Message;
import unsw.ui.Observer.Observer;
import unsw.ui.topbar.TurnFeatureInfo;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;

public class TopBarController extends Controller implements Observer<TurnFeatureInfo>{
    private GameController game;
    private TurnFeatureInfo turninfo;

    @FXML
    private HBox topbar;
    private VicComposite vicinfo;
    

    @FXML private Label yearLabel;
    @FXML private Label facnameLabel;
    @FXML private Label goldLabel;
    @FXML private Label goalLabel;
    @FXML private MenuBar infoMenu;
    
    @FXML private Button endTurn;

    @FXML
    private Menu vic = new Menu("VicInfo");
    
    private DoubleProperty p = new SimpleDoubleProperty(0.0);
    private DoubleProperty p1 = new SimpleDoubleProperty(0.0);
    private DoubleProperty p2 = new SimpleDoubleProperty(0.0);
    private DoubleProperty p3 = new SimpleDoubleProperty(0.0);

    public TopBarController(){}
    public TopBarController(GameController game){
        this.game = game;
		GloriaRomanusApplication.loadExistingController(this, "src/unsw/ui/topbar/TopBar.fxml");
    }

    @FXML
    public void initialize(){
        update(new TurnFeatureInfo(game));
        endTurn.setOnAction((e)->endTurnPressed());
        
        infoMenuSetup();
    }

    private String getgold() {
        return turninfo.getGold();
    }

    private String getFaction() {
        return turninfo.getFaction();
    }

    private String getYear() {
        return turninfo.getYear();
    }

    private void infoMenuSetup(){
        //set 1st entry
        ProgressBar gn1 = new ProgressBar();
        gn1.progressProperty().bind(p1);
        MenuItem item1 = new MenuItem("Conquest",gn1);
    
        //set 2nd entry
        ProgressBar gn2 = new ProgressBar();
        gn2.progressProperty().bind(p2);
        MenuItem item2 = new MenuItem("Treasury",gn2);
    
        ProgressBar gn3 = new ProgressBar();
        gn3.progressProperty().bind(p3);
        MenuItem item3 = new MenuItem("Wealth",gn3);
        
        //set main entry
        ProgressBar gn = new ProgressBar();
        gn.progressProperty().bind(p);
        vic.getItems().addAll(item1,item2,item3);
        vic.graphicProperty().set(gn);
        infoMenu.getMenus().addAll(vic);
    }
    
    private void endTurnPressed(){
        if (this.game.endTurn()==null){
            //game continue
            System.out.println("endturn");
        }else{
            //game end
            //TODO new sceen
        }
    }

    private void progressVicInfo(VicComposite vic){
        Double conquest = vic.getProgress(VictoryCondition.CONQUEST);
        Double treasury = vic.getProgress(VictoryCondition.TREASURY);
        Double wealth = vic.getProgress(VictoryCondition.WEALTH);
        //System.out.println(conquest.toString()+" "+ treasury.toString()+" "+wealth.toString());//terminal debug progress
        p1.set(conquest);
        p2.set(treasury);
        p3.set(wealth);
        p.set(vic.getMainProgress());
    }
    
    @Override
    public void update(TurnFeatureInfo m) {
        turninfo = m;
        
        yearLabel.setText(getYear() + " AD");
        facnameLabel.setText(getFaction());

        goldLabel.setText(getgold() + " Gold");

        vicinfo = game.getCurrentTurn().getVicComposite();
        goalLabel.setText("Goal: " + vicinfo.toString());
        
        progressVicInfo(vicinfo);

        //victory\defeat display
        // try {
        //     image();
        // } catch (Exception e) {
        //     //TODO: handle exception
        //     System.out.println("exceptio");
        // }
    }


    // private void image()throws Exception{
    //     // FXMLLoader loader = new FXMLLoader(getClass().getResource("src/unsw/ui/VicUI.fxml"));
    //     // Parent parent = loader.load();
    //     // T c = loader.getController();
    //     Controller controller;
    //     try {
    //         controller = GloriaRomanusApplication.loadController("src/unsw/ui/VicUI.fxml");
    //     } catch (Exception e) {
    //         //TODO: handle exception
    //     }
        
    //     GloriaRomanusApplication.app.setScene(controller);
    //     System.out.println("new image");
    // }
}
