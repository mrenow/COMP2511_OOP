package unsw.gloriaromanus;

/**
 * Storage struct to pair a side and method
 * 
 */
public class CombatModifier{
	private CombatModifierMethod method;
	private BattleSide side;
	
	public CombatModifier(CombatModifierMethod method, BattleSide side) {
		this.side = side;
		this.method = method;
	}
	
	public void alterEngagement(CombatData data) {
		method.alterEngagement(data, side);
	}
}