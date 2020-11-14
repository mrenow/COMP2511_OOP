package unsw.ui.Observer;

import unsw.engine.Faction;
import unsw.engine.GameController;
import unsw.engine.VicCondition.VicComponent;
import unsw.engine.VicCondition.VicComposite;

public class TurnFeatureInfo{
    private GameController game;
    
    public TurnFeatureInfo(GameController game){
        this.game = game;
    }
    public Faction getTurn (){
        return game.getCurrentTurn();
    }

    public GameController getGame() {
        return game;
    }

    public void setGame(GameController game) {
        this.game = game;
    }
    public String getFaction() {
        return game.getCurrentTurn().getType().toString();
    }
    public String getYear() {
        return Integer.toString(game.getYear());
    }
    public String getGold() {
        return Integer.toString(game.getCurrentTurn().getGold());
    }
    public VicComponent getVicComponent() {
        return game.getCurrentTurn().getVicComponent();
    }
}
