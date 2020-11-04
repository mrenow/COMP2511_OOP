package unsw.gloriaromanus;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Base interface for Combat and Morale (and God forbid Speed...) modifiers. 
 * Polymorphism is desirable due to the way it allows modifiers to be handled in the UI.
 * NOTE : As description keys do note note the enum type, CombatModifiers and MoraleModifier methods 
 * cannot share names.
 */

interface ModifierMethod<T>{
	public static String DESCRIPTION_FILE_PATH = "src/unsw/gloriaromanus/data/modifier_descriptions.json";
	
	public final static Map<String, String> DESCRIPTIONS = Parsing.readValue(new File(DESCRIPTION_FILE_PATH));
	
	public void modify(T data, BattleSide side);
	
	public String getDescription();
	
	// Enforces a string representation.
	public String toString();
	
	public ActiveType getActiveType();
	
}
