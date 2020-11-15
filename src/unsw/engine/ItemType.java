package unsw.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import util.ArrayUtil;

/**
 * Represents a queueable task for a province that takes <code>duration</code>turns to complete
 * @author ezra
 *
 */
enum UnitClass {
	MELEE_INFANTRY, MELEE_CAVALRY, RANGED, ARTILLERY
//	,TOWER
	;
}

public enum ItemType{
	// enum potential values
	// infrastructure
//	FARM					(),
//	MINE					(),
//	PORT					(),
//	MARKET					(),
//	WALLS					(),
//	SMITH					(),
//	ROADS					(),
//	ROAD					(),
//	STABLES					(),
//	BARRACKS				(),
//	ARCHERY_RANGE			(),

	TRAINING_BUILDING		("src/test/test_building.json"),
	TEST_TROOP				("src/test/test_troop.json"),
	TEST_BUILDING			("src/test/test_building.json"),
	
	//SPECIALS
	JAVELIN_SKIRMISHER		(),
	ELEPHANTS				(),
	ROMAN_LEGIONARY  		(),
	
	//ARTILLERY:
	TREBUCHET				(),
	
	//INFANTRY:
	//HEAVY_INFANTRY			(),
	ARCHER					(),
	SPEARMEN				(),
	PIKEMEN 				(),
	HOPILITE 				(),
	BERSERKER				(),
	
	// CAVALRY:
	HORSE_ARCHER			(),
	HEAVY_CAVALRY			(),
	LIGHT_CAVALRY			(),
	DRUID					(),
	CHARIOTS				(),
	
	// NO_ONE:
	BARBARIAN				();
	// ABSOLUTELY NO ONE:
	
	//
	
	private static final String SOURCE_DIR = "src/unsw/engine/data";
	
	// Attributes
	private int maxLevel;

	private List<String> names;
	private List<String> descriptions;
	private List<Integer> costs;
	private List<Integer> durations; // duration to train/build
	
	// Mapping between string (name of attribute) and List of integers (attribute values for each level)
	private Map<String, List<Object>> attributes; 
	
	private ItemType(){
		// Filename determined automatically
		try {
			constructFromFile(
					String.format("%s/%s.json",
							SOURCE_DIR,
							this.name().toLowerCase())
					);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	private ItemType(String filename) {
		constructFromFile(filename);
	}
	/**
	 * Fills in the enum attributes and attributes list.
	 * @param filename
	 * @throws DataInitializationError 
	 * @throws Exception 
	 * @throws ExceptionInInitializerError if name, desc, cost,
	 * duration or maxlvl is missing frParsing.OM the file
	 */
	private void constructFromFile(String filename) {
		JsonNode root = Parsing.readTree(new File(filename));
		// Needed to determine list size
		this.maxLevel = root.get("maxLevel").asInt();
		
		this.names = generateLevelList(root.get("names"));
		this.descriptions = generateLevelList(root.get("descriptions"));
		this.costs = generateLevelList( root.get("costs"));
		this.durations = generateLevelList(root.get("durations"));
		this.names = generateLevelList(root.get("names"));
		
		this.attributes = new HashMap<String, List<Object>>();

		Iterator<String> nodeNames = root.fieldNames();
		while(nodeNames.hasNext() ) {
			String nodeName = nodeNames.next();
			attributes.put(nodeName, generateLevelList(root.get(nodeName)));
			assert attributes.get(nodeName).size() == maxLevel :
				String.format("%s of %s had wrong number of elements.", nodeName, this.name());
		}
	}
	private <T> List<T> makeConstantList(T data, int size){
		List<T> out = new ArrayList<T>();
 		for (int i = 0; i < size; i++) {
			out.add(data);
		}
 		return out;
	}
	
	private <T> List<T> generateLevelList(JsonNode node) {
		if(node.isArray()) {
			List<T> out = Parsing.readValue(node.toString());
			assert out.size() == maxLevel :
				String.format("Error while parsing %s, too many elements in Json String: %s", this.name(), node.toString());
			return out;
		} else {
			return makeConstantList(Parsing.readValue(node.toString()), this.maxLevel);
		}
	}
	/**
	 * Gets item description for the given level. This will be displayed on the UI.
	 * @param level
	 * @return
	 */
	public String getDescription(int level) {
		return descriptions.get(level - 1);
	}

	/**
	 * Gets item description for the given level. This will be displayed on the UI.
	 * @param level
	 * @return
	 */
	public String getName(int level) {
		return names.get(level - 1);
	}

	/**
	 * Gets item description for the given level. This will be displayed on the UI.
	 * @param level
	 * @return
	 */
	public int getCost(int level) {
		return costs.get(level - 1);
	}

	/**
	 * Gets the number of turns this item will take to complete for the given level.
	 * This will be displayed on the UI, and also passedInto the ItemEntry
	 * @param level
	 * @return
	 */
	public int getDuration(int level) {
		return durations.get(level - 1);
	}

	/**
	 * Gets the maximum level of this item.
	 * @param level
	 * @return
	 */
	public int getMaxLevel() {
		return maxLevel;
	}

	public <T> T getAttribute(String name, int level) throws NoSuchElementException{	
		List<Object> list = attributes.get(name);
		if (list == null) {
			throw new NoSuchElementException(this.name() + " has no attribute " + name);
		}
		if(level > maxLevel) {
			throw new NoSuchElementException(
					String.format("%s has max level %d, tried to access level %d",
							this.name(),
							this.maxLevel,
							level)
					);
		}
		return (T)list.get(level-1);
	}
	public <T> T getAttributeOrNull(String name, int level){	
		List<Object> list = attributes.get(name);
		if (list == null || level > maxLevel) {
			return null;
		}
		return (T)list.get(level-1);
	}
	
	
	public static void main(String[] args) {
		ItemType i = ItemType.TEST_TROOP;
		System.out.println(i.attributes);
		
	}
	public String toString() {
		return ArrayUtil.enumToTitle(this);
	}
}