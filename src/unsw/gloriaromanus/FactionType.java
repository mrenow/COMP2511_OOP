package unsw.gloriaromanus;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public enum FactionType {
	// Placeholders
	ROME		("Roman", "Romans"),
	GAUL		("Gallic", "Gauls");
	
	private String title;
	private String adjective;
	private String plural;

	public static FactionType fromName(String name) {
		name = name.toLowerCase();
		for (FactionType t : FactionType.values()) {
			if(Objects.equals(t.name().toLowerCase(), name)) {
				return t;
			}
		}
		return null;
	}
	
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
