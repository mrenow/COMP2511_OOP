package unsw.engine;

// Specifically melee cavalrys
public class MeleeCavalry extends Unit{

	public static UnitClass UNIT_CLASS = UnitClass.MELEE_CAVALRY;
	private double charge = 0;

	public MeleeCavalry(ItemType newType, int newLevel) {
		super(newType, UNIT_CLASS, newLevel);
		this.charge = ((Integer) getType().getAttribute("charge", getLevel())).doubleValue();
	}
	
	public double getChargeAttack() {
		return charge;
	}

}
