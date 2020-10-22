package unsw.gloriaromanus;

public enum FactionType {
	PLAYER		("Player"),
	ENEMY		("Enemy");
	
	private String name;
	
	private FactionType(String name) {
		this.name = name;
		
	}
	public String getName() {
		return name;
	}
}
