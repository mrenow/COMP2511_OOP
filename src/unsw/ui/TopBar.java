package unsw.ui;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import unsw.engine.GameController;
import unsw.engine.VicCondition.VicComposite;
import unsw.engine.VicCondition.VictoryCondition;
import unsw.ui.Observer.Message;
import unsw.ui.Observer.MsgObserver;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TopBar implements MsgObserver{
    private HBox topbar;
    private GameController game;
    private VicComposite vicinfo;

    private Integer year = 300;
    private Text y = new Text(year.toString());
    private Text facnameIndicator = new Text("faction name");
    private MenuBar infoMenu = new MenuBar();
    private Menu gold = new Menu();
    private Integer g = 0;
    private Button endTurn = new Button("EndTurn");


    private Menu vic = new Menu("VicInfo");
    
    private DoubleProperty p = new SimpleDoubleProperty();
    private DoubleProperty p1 = new SimpleDoubleProperty();
    private DoubleProperty p2 = new SimpleDoubleProperty();
    private DoubleProperty p3 = new SimpleDoubleProperty();
    
    /**
     * top bar constructor
     * @param topbar topbar constructed
     * @param game gameconstructed pointer
     */
    public TopBar(HBox topbar,GameController game) {
        Text yearIndicator = new Text("Year:");
        this.topbar = topbar;
        this.game = game;
        gold.setText("Gold:"+g.toString());;
        endTurn.setOnAction((e)->endTurnPressed());
        this.topbar.getChildren().addAll(yearIndicator,y,facnameIndicator,infoMenu,endTurn);
        
        infoMenuSetup();
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
        gn2.progressProperty().bind(p3);
        MenuItem item3 = new MenuItem("Wealth",gn3);
        
        //set main entry
        ProgressBar gn = new ProgressBar();
        gn.progressProperty().bind(p);
        vic.getItems().addAll(item1,item2,item3);
        vic.graphicProperty().set(gn);
        infoMenu.getMenus().addAll(vic,gold);
    }
    
    private void endTurnPressed(){
        if (this.game.endTurn()==null){
            //game continue
            
        }else{
            //game end
            //TODO new sceen
        }
    }

    private void progressVicInfo(VicComposite vic){
        Double conquest = vic.getProgress(VictoryCondition.CONQUEST);
        Double treasury = vic.getProgress(VictoryCondition.TREASURY);
        Double wealth = vic.getProgress(VictoryCondition.WEALTH);
        p1.set(conquest);
        p2.set(treasury);
        p3.set(wealth);
        p.set(vic.getMainProgress());
    }
    
    @Override
    public void update(Message m) {
        this.game = m.getGame();
        System.out.println("update top bar");
        year = game.getYear();
        y.setText(year.toString());
        facnameIndicator.setText(game.getCurrentTurn().getType().toString());
        vicinfo = game.getCurrentTurn().getVicComposite();
        g = game.getCurrentTurn().getGold();
        gold.setText("Gold:"+g.toString());
        progressVicInfo(vicinfo);
    }
}
