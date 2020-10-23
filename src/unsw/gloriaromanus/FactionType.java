package unsw.gloriaromanus;

public enum FactionType {
	// Placeholders
	ROME		("Rome", "Roman", "Romans"),
	GAUL		("Gaul", "Gallic", "Gauls");
	
	private String name;
	private String adjective;
	private String plural;

	private FactionType(String name, String adjective, String plural) {
		this.name = name;
		this.adjective = adjective;
		this.plural = plural;
	}

	public String getName() {
		return name;
	}

	public String getAdjective() {
		return adjective;
	}

	public String getPlural() {
		return plural;
	}
}
