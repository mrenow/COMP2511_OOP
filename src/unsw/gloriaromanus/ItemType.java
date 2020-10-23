package unsw.gloriaromanus;

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

//Represents a queueable task for a province that takes duration turns to complete
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
//	ARTILLERY_RANGE			(),
//	
//	// units
//	HEAVY_INFANTRY			(),
//	ARCHER					(),
	TEST_TROOP				("src/test/test_troop.json"),
	TEST_BUILDING			("src/test/test_building.json"),
	HEAVY_CAVALRY			();
	
	private static final String SOURCE_DIR = "src/data";
	
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	private ItemType(String filename) {
		try {
			constructFromFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		
	}
	/**
	 * Fills in the enum attributes and attributes list.
	 * @param filename
	 * @throws IOException 
	 * @throws ExceptionInInitializerError if name, desc, cost,
	 * duration or maxlvl is missing from the file
	 */
	private void constructFromFile(String filename) throws IOException{
		ObjectMapper om = new ObjectMapper();
		JsonNode root = om.readTree(new File(filename));
		
		// Needed to determine list size
		this.maxLevel = om.readValue(root.get("maxLevel").toString(), Integer.class);
		
		this.names = generateLevelList(om, root.get("names"));
		this.descriptions = generateLevelList(om, root.get("descriptions"));
		this.costs = generateLevelList(om, root.get("costs"));
		this.durations = generateLevelList(om, root.get("durations"));
		this.names = generateLevelList(om, root.get("names"));
		
		this.attributes = new HashMap<String, List<Object>>();

		Iterator<String> nodeNames = root.fieldNames();
		while(nodeNames.hasNext() ) {
			String nodeName = nodeNames.next();
			attributes.put(nodeName, generateLevelList(om, root.get(nodeName)));
			if(attributes.get(nodeName).size() != maxLevel) {
				throw new IOException(String.format("%s of %s had wrong number of elements.", nodeName, this.name()));
			}
		}
		
		// Make assertions:
		
		
	}
	private <T> List<T> makeConstantList(T data, int size){
		List<T> out = new ArrayList<T>();
 		for (int i = 0; i < size; i++) {
			out.add(data);
		}
 		return out;
	}
	
	private <T> List<T> generateLevelList(ObjectMapper om, JsonNode node) throws JsonMappingException, JsonProcessingException{
		if(node.isArray()) {
			return om.readValue(node.toString(), new TypeReference<List<T>>(){});			
		}else {
			return makeConstantList(om.readValue(node.toString(), new TypeReference<T>() {}), this.maxLevel);
		}
	}
	
	public String getDescription(int level) {
		return descriptions.get(level);
	}
	
	public String getName(int level) {
		return names.get(level);
	}
	public int getCost(int level) {
		return costs.get(level);
	}
	public int getDuration(int level) {
		return durations.get(level);
	}
	public int getMaxLevel() {
		return maxLevel;
	}

	public Object getAttribute(String name, int level) throws NoSuchElementException{	
		List<Object> list = attributes.get(name);
		if (list == null) {
			throw new NoSuchElementException(this.name() + " has no attribute " + name);
		}
		if(level >= maxLevel) {
			throw new NoSuchElementException(
					String.format("%s has max level %d, tried to access level %d",
							this.name(),
							this.maxLevel,
							level)
					);
		}
		return list.get(level);
	}
	
	public static void main(String[] args) {
		ItemType i = ItemType.TEST_TROOP;
		System.out.println(i.attributes);
		
	}

	
}