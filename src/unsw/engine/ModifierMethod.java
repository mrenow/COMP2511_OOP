package unsw.engine;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Denotes whether the method is global or local to the given datangagement.
 * null values are accepted: they indicate that this method does not belong to a unit
 * and an error will be thrown when one attempts to add them to a unit.
 */
enum ActiveType {
	SUPPORT, ENGAGEMENT;
}

/**
 * Base interface for Combat and Morale (and God forbid Speed...) modifiers. 
 * Polymorphism is desirable due to the way it allows modifiers to be handled in the UI.
 * NOTE : As description keys do note note the enum type, CombatModifiers and MoraleModifier methods 
 * cannot share names.
 */

public interface ModifierMethod<T>{
	public static String DESCRIPTION_FILE_PATH = "src/unsw/engine/data/modifier_descriptions.json";
	
	public final static Map<String, String> DESCRIPTIONS = Parsing.readValue(new File(DESCRIPTION_FILE_PATH));
	
	public void modify(T data, BattleSide side);
	
	public String getDescription();
	
	// Enforces a string representation.
	public String toString();
	
	public ActiveType getActiveType();
	
}
