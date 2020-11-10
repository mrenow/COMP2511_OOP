package unsw.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public enum FactionType {
	// Placeholders
	NO_ONE		("Barbarian", "Barbarians"),
	ROME		("Roman", "Romans"),
	GAUL		("Gallic", "Gauls"),
	EGYPT		("Egyptian", "Egyptians"),
	CARTHAGE	("Carthaginian", "Carthaginians"),
	GREECE		("Hellenic", "Hellenes"),
	SELEUCID_EMPIRE	("Seleucid", "Seleucids"),
	MACEDONIA	("Macedonian", "Macedonians"),
	NUMIDIA		("Numidian", "Numidians"),
	BRITAIN		("Briton", "Britons"),
	PARTHIA		("Parthian", "Parthians"),
	THRACE		("Thracian", "Thracians"),
	SPAIN		("Spanish", "Spaniards"),
	DACIA		("Dacian", "Dacians"),
	PONTUS		("Pontian", "Pontians"),
	ARMENIA		("Armenian", "Armenians"),
	GERMANY		("Germanic", "Germanics");
	
	
	
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
