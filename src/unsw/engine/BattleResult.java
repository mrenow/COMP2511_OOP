package unsw.engine;

public enum BattleResult {
    WIN, LOSE, DRAW;
    public BattleResult defenderView(){
        switch (this) {
            case WIN:
                return LOSE;
            case LOSE:
                return WIN;
            case DRAW:
                return DRAW;
            default:
                return null;
        }
    }
}