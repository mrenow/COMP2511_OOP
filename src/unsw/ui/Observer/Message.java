package unsw.ui.Observer;

import unsw.engine.Faction;
import unsw.engine.GameController;
import unsw.engine.Province;

public class Message {
    Faction f;
    String action;
    Province province;
    GameController game;

    public GameController getGame() {
        return game;
    }

    public void setGame(GameController game) {
        this.game = game;
    }

    
}
