package unsw.gloriaromanus;

public class Artillery extends Unit{
	private double seigeAttack = 0;
	
	public Artillery(ItemType newType, int newLevel) {
		super(newType, newLevel);
		// TODO Auto-generated constructor stub
	}

	
	public double getSeigeAttack() {
		return seigeAttack;
	}
	
}
