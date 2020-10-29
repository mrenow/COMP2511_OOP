package unsw.gloriaromanus;
/**
 * Represents the side that an attacker can be on.
 */
public enum BattleSide {
	ATTACK,DEFEND;
	public BattleSide other() {
		switch(this) {
		case ATTACK : return DEFEND;
		case DEFEND : return ATTACK;
		default : return null;
		}
		
	}
}
