package unsw.gloriaromanus;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public enum FactionType {
	// Placeholders
	NO_ONE		("Barbarian", "Barbarians"),
	ROME		("Roman", "Romans"),
	GAUL		("Gallic", "Gauls");
	
	public String title;
	private String adjective;
	private String plural;

	// Title is determined by the name() function which gives the enum name as it appears in code.
	private FactionType(String adjective, String plural) {
		// Extract Title name
		this.title = Character.toUpperCase(this.name().charAt(0)) +
				this.name().substring(1).toLowerCase();
		
		this.adjective = adjective;
		this.plural = plural;
	}

	public String getTitle() {
		return title;
	}

	public String getAdjective() {
		return adjective;
	}

	public String getPlural() {
		return plural;
	}
}
