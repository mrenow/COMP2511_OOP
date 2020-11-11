package unsw.ui;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import unsw.engine.GameController;
import unsw.engine.VicCondition.VicComposite;
import unsw.engine.VicCondition.VictoryCondition;
import unsw.gloriaromanus.GloriaRomanusController;
import unsw.ui.Observer.Global;
import unsw.ui.Observer.Message;
import unsw.ui.Observer.MsgObserver;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class TopBar implements MsgObserver{
    private HBox topbar;
    private GameController game;
    private VicComposite vicinfo;

    private Text facname = new Text("faction name");
    private MenuBar infoMenu = new MenuBar();
    private List<VictoryCondition> vConditions = new ArrayList<>();
    private Button endTurn = new Button("EndTurn");

    private Menu goalname1 = new Menu("VicInfo");
    
    private DoubleProperty p = new SimpleDoubleProperty();
    private DoubleProperty p1 = new SimpleDoubleProperty();
    private DoubleProperty p2 = new SimpleDoubleProperty();
    private DoubleProperty p3 = new SimpleDoubleProperty();
    public TopBar(HBox topbar,GameController game) {
        this.topbar = topbar;
        this.game = game;

        endTurn.setOnAction((e)->endTurnPressed());
        this.topbar.getChildren().addAll(facname,infoMenu,endTurn);
        
        infoMenuSetup();
    }
    
    private void endTurnPressed(){
        if (this.game.endTurn()==null){
            //game continue
            
        }else{
            //game end
        }
        vicinfo = game.getCurrentTurn().getVicComposite();
        facname.setText(game.getCurrentTurn().getType().toString());
        progressVicInfo(vicinfo);
    }

    private void progressVicInfo(VicComposite vic){
        p1.set(vic.getProgress(VictoryCondition.CONQUEST));
        p2.set(vic.getProgress(VictoryCondition.TREASURY));
        p3.set(vic.getProgress(VictoryCondition.WEALTH));
        
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
        goalname1.getItems().addAll(item1,item2,item3);
        goalname1.graphicProperty().set(gn);
        infoMenu.getMenus().add(goalname1);
    }
    @Override
    public void update(Message m) {
        // TODO Auto-generated method stub
        
    }
}
