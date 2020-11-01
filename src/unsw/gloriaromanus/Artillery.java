package unsw.gloriaromanus;

public class Artillery extends Unit{
	public static UnitClass UNIT_CLASS = UnitClass.ARTILLERY;
	private double seige = 0;
	
	public Artillery(ItemType newType, int newLevel) {
		super(newType, UNIT_CLASS, newLevel);
		this.seige = ((Integer) getType().getAttribute("seige", getLevel())).doubleValue();
	}

	
	public double getSeigeAttack() {
		return seige;
	}
	
}
