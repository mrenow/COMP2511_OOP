package unsw.gloriaromanus;
/**
 * Represents the side that an attacker can be on.
 */
enum BattleSide {
	ATTACK,DEFEND;
	public BattleSide other() {
		switch(this) {
		case ATTACK : return DEFEND;
		case DEFEND : return ATTACK;
		default : return null;
		}
		
	}
}
/**
 * Denotes whether the method is global or local to the given datangagement.
 * null values are accepted: they indicate that this method does not belong to a unit
 * and an error will be thrown when one attempts to add them to a unit.
 */
enum ActiveType {
	SUPPORT, ENGAGEMENT;
}