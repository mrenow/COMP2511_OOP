package unsw.gloriaromanus;

public class Artillery extends Unit{
	private double seige = 0;
	
	public Artillery(ItemType newType, int newLevel) {
		super(newType, newLevel);
		this.seige = ((Integer) getType().getAttribute("seige", getLevel())).doubleValue();
	}

	
	public double getSeigeAttack() {
		return seige;
	}
	
}
