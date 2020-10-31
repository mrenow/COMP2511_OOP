package unsw.gloriaromanus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
/**
 * Does all jobs for parsing game state
 * 
 */
public class Parsing {
	public static final ObjectMapper mapper = configuredObjectMapper();
	
	
	
	private static ObjectMapper configuredObjectMapper() {
		ObjectMapper newMapper = JsonMapper.builder()
				.enable(
					JsonReadFeature.ALLOW_JAVA_COMMENTS,
					JsonReadFeature.ALLOW_TRAILING_COMMA,
					JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES
					)
				.enable(SerializationFeature.INDENT_OUTPUT)
				.disable(
					MapperFeature.AUTO_DETECT_GETTERS,
					MapperFeature.AUTO_DETECT_SETTERS,
					MapperFeature.AUTO_DETECT_IS_GETTERS
					)
				.build();
				
		return newMapper;
	}
	
	public static <T extends Enum<T>> T getEnum(String name, Class<T> typeRef) {
		try {
			// Im lazy
			return Parsing.mapper.readValue("\"" + name.toUpperCase() + "\"", typeRef);
		}catch(Exception e){
			return null;
		}
	}
	
	
	// Comma seprated enum fields.
	public static <T extends Enum<T>> List<T> getEnums(String enumString, Class<T> typeRef) throws DataInitializationException{
		List<T> out = new ArrayList<>();
		Scanner sc = new Scanner(enumString);
		try {
			while(sc.hasNext()) {
				T val = Parsing.mapper.readValue("\"" + sc.next().toUpperCase() + "\"", typeRef);
				out.add(val);
			}		
		}catch(Exception e){
			throw new DataInitializationException("Error while parsing enum list",e);
		}
		return out;
	}
		
	
	
	
	// Constructs provinces
	public static Map<String, Province> readAdjacency(String provinceFile) throws Exception {
		Map<String, Province> provinceMap= new HashMap<>();
		// MUST COPY BEFORE EDITING MAPPER CONFIG
		ObjectMapper om = mapper.copy();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<Province> allProvinces = om.readValue(new File(provinceFile), new TypeReference<List<Province>>() {});
		for (Province p : allProvinces) {
			for ( Unit u : p.getUnits()) {
				u.loadProvince(p);
			}
			provinceMap.put(p.getName(), p);
		}
		return provinceMap;
	}
	public static void readLandlocked(String landlockedFile, Map<String, Province> allProvinces) throws Exception {
		JsonNode root = mapper.readTree(new File(landlockedFile));
		for (JsonNode name : root) {
			allProvinces.get(name.asText()).setLandlocked(true);
		}
	}
	
	public static List<Faction> readFactions(String factionFile, Map<String, Province> allProvinces) throws Exception {
		// MUST COPY BEFORE EDITING MAPPER CONFIG
		ObjectMapper om = mapper.copy();
		JsonNode root = om.readTree(new File(factionFile));
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// Construct factions without provinces
		List<Faction> factionOrder = om.readValue(new File(factionFile), new TypeReference<List<Faction>>() {});
		// Province stuff
		int factionIndex = 0;
		for (JsonNode node : root) {
			List<String> provinceNames = om.readValue(node.get("provinceNames").toString(), new TypeReference<List<String>>(){});
			for (String name: provinceNames) {
				factionOrder.get(factionIndex).loadProvince(allProvinces.get(name));
			}
			factionIndex ++;
		}
		
		return factionOrder;
	}
	/**
	 * Automatically allocates provinces to factions using some algorithm. 
	 * Simply picks a random province and tries to expand in all directions.
	 * makes sure that half of the existing provinces are not occupied.
	 * @param types
	 * @param provinceMap
	 * @return
	 */
	public static List<Faction> allocateProvinces(List<FactionType> types, Map<String, Province> provinceMap, double density){
		List<Faction> allFactions = new ArrayList<>(types.size());
		while(!tryAllocateProvinces(allFactions, types, provinceMap.values(), density));
		return allFactions;
	}
	/**
	 * Tries to allocate provinces and construct factions. Could fail if we get bad RNG.
	 * 
	 */
	private static boolean tryAllocateProvinces(List<Faction> target, List<FactionType> types, Collection<Province> allProvinces, double density) {
		
		List<Province> freeProvinces = new ArrayList<>(allProvinces);
		// Determine the number of provinces each faction should have/
		int numStartProvinces = (int)(density*allProvinces.size()/types.size());
		Queue<Province> candidates = new LinkedList<>();
		Map<FactionType, Collection<Province>> selected = new HashMap<>();
		for (FactionType type : types) {
			// Find suitable seed province and reset selection
			Province seed = GlobalRandom.getRandom(freeProvinces);
			Set<Province> selection = new HashSet<>();
			candidates.add(seed);
			while (selection.size() < numStartProvinces) {

				// Whoops, looks like rng does not look favourably on us today. Time to start over I guess
				if(candidates.isEmpty()) {
					// Time to give up, you cant win them all
					target.clear();
					return false;
				}
				// Get adjacent provinces to current and add them to candidates.
				Province curr = candidates.poll();
				// transfer from freeProvinces to selection.
				selection.add(curr);
				freeProvinces.remove(curr);
				
				for(Province adj : curr.getAdjacent()) {
					// Ensure that we arent double counting and that this province is free.
					if(selection.contains(adj) || !adj.getOwner().equals(Faction.NO_ONE)) {
						continue;
					}
					// all clear. Add adj
					candidates.add(adj);
				}
			}
			// confirm selection
			selected.put(type, selection);
			candidates.clear();
		}
		// all clear, we have a valid arrangement
		for (FactionType type : types) {
			target.add(new Faction(type, selected.get(type), 50));

		}
		return true;
	}

	
	
	
}
