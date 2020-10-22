package unsw.gloriaromanus;

public enum FactionType {
	// Placeholders
	ROMANS		("Romans"),
	GAULS		("Gauls");
	
	private String name;
	
	private FactionType(String name) {
		this.name = name;
		
	}
	public String getName() {
		return name;
	}
}
