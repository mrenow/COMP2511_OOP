package unsw.gloriaromanus;

// Specifically melee cavalrys
public class MeleeCavalry extends Unit{

	public static UnitClass UNIT_CLASS = UnitClass.MELEE_CAVALRY;
	private double charge = 0;

	public MeleeCavalry(ItemType newType, int newLevel) {
		super(newType, UNIT_CLASS, newLevel);
		addCombatModifier(CombatModifierMethod._HEROIC_CHARGE_COMBAT);
		addMoraleModifier(MoraleModifierMethod._HEROIC_CHARGE_MORALE);
		this.charge = ((Integer) getType().getAttribute("charge", getLevel())).doubleValue();
	}
	public double getChargeAttack() {
		return charge;
	}

}
