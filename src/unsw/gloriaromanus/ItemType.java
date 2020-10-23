package unsw.gloriaromanus;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

//Represents a queueable task for a province that takes duration turns to complete
enum ItemType{
	// enum potential values
	// infrastructure
	BASIC_INFRASTRUCTURE	(),
	FARM					(),
	MINE					(),
	PORT					(),
	MARKET					(),
	WALLS					(),
	SMITH					(),
	ROADS					(),
	ROAD					(),
	STABLES					(),
	BARRACKS				(),
	ARCHERY_RANGE			(),
	
	// units
	BASIC_TROOP				(),
	HEAVY_INFANTRY			(),
	HEAVY_CAVALRY			();
	
	private static final String SOURCE_DIR = "data";
	
	// Attributes
	private List<String> names;
	private List<String> descriptions;
	private List<Integer> costs;
	private List<Integer> durations; // duration to train/build
	private int maxLevel;
	 
	// Mapping between string (name of attribute) and List of integers (attribute values for each level)
	private Map<String, List<Object>> attributes; 
	
	private ItemType() throws ExceptionInInitializerError{
		// Filename determined automatically
		constructFromFile(
				String.format("%s/%s.json",
						SOURCE_DIR,
						this.name().toLowerCase())
				);
	}
	private ItemType(String filename) throws ExceptionInInitializerError{
		constructFromFile(filename);
	}
	/**
	 * Fills in the enum attributes and attributes list.
	 * @param filename
	 * @throws ExceptionInInitializerError if name, desc, cost,
	 * duration or maxlvl is missing from the file
	 */
	private void constructFromFile(String filename) throws ExceptionInInitializerError{
		// TODO
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

	
}