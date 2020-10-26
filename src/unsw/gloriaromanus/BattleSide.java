package unsw.gloriaromanus;

public enum BattleSide {
	ATTACKER,DEFENDER;
	public BattleSide other() {
		switch(this) {
		case ATTACKER : return DEFENDER;
		case DEFENDER : return ATTACKER;
		default : return null;
		}
		
	}
}
