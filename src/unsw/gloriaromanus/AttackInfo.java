package unsw.gloriaromanus;

public enum AttackInfo {
    WIN, LOSE, DRAW;
    public AttackInfo defenderView(AttackInfo info){
        switch (info) {
            case WIN:
                return LOSE;
            case LOSE:
                return WIN;
            default:
                return info;
        }
    }
}
