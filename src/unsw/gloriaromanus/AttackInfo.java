package unsw.gloriaromanus;

enum AttackInfo {
    WIN, LOSE, DRAW;
    public AttackInfo defenderView(){
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
