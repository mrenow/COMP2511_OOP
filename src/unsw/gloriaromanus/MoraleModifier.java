package unsw.gloriaromanus;

/**
 * Storage struct to pair a side and method
 * 
 */
public class MoraleModifier{
	
	private MoraleModifierMethod method;
	private BattleSide side;
	
	public MoraleModifier(MoraleModifierMethod method, BattleSide side) {
		this.side = side;
		this.method = method;
	}
	
	public void alterMorale(MoraleData data) {
		method.alterMorale(data, side);
	}
}