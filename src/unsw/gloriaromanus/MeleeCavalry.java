package unsw.gloriaromanus;

// Specifically melee cavalrys
public class MeleeCavalry extends Unit{

	private double charge = 0;

	public MeleeCavalry(ItemType newType, int newLevel) {
		super(newType, newLevel);
		this.charge = ((Integer) getType().getAttribute("charge", getLevel())).doubleValue();
	}
	public double getChargeAttack() {
		return charge;
	}

}
