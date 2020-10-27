package unsw.gloriaromanus;

public class Artillery extends Unit{
	public Artillery(ItemType newType, int newLevel) {
		super(newType, newLevel);
		// TODO Auto-generated constructor stub
	}

	private double towerAttack = 0;
	
	public double getTowerAttack() {
		return towerAttack;
	}
	
}
