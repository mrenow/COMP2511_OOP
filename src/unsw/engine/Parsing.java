package unsw.engine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
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
import com.fasterxml.jackson.databind.JavaType;
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
	private static final ObjectMapper mapper;
	
	// Code is run on class initialization
	static {
		mapper = JsonMapper.builder()
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
	}
	
	/*
	 * Wrapper functions to handle errors and handle generic inference
	 */
	
	// Wrapper to handle error internally.
	
	// Oh god why cant we have pythion decorators all I wanted to do was handle errors internally
	public static <T> T readValue(File file, Class<T> typeRef) {
		try{
			T val = mapper.readValue(file, typeRef);
			return val;
		}catch (Exception e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
			return null; // Never runs
		}
	}
	
	// MakE IT STOp
	public static <T> T readValue(String jsonString, Class<T> typeRef) {
		try {
			return mapper.readValue(jsonString, typeRef);
		}catch (Exception e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
			return null; // Never runs
		}
	}

	// I DONt WANT TO REfACTOR ANYMORE
	public static <T> T readValue(String jsonString) {
		try {
			return mapper.readValue(jsonString, new TypeReference<T>() {});
		}catch (Exception e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
			return null; // Never runs
		}
	}	
	// WHERE Are MY C #DEFINEs
	public static <T> T readValue(File file) {
		try {
			return mapper.readValue(file, new TypeReference<T>() {});
		}catch (Exception e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
			return null; // Never runs
		}
	}
	
	public static JsonNode readTree(File file) {
		try {
			return mapper.readTree(file);
		}catch (Exception e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
			return null; // Never runs
		}
	}
	
	public static void writeValue(File file, Object o) {
		try {
			mapper.writeValue(file, o);
		}catch (Exception e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static JsonNode readTree(String jsonString) {
		try {
			return mapper.readTree(jsonString);
		}catch (Exception e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
			return null; // Never runs
		}
	}
		
	
	public static <T extends Enum<T>> T getEnum(String name, Class<T> typeRef) throws NoSuchElementException {

		// Handle exception differently
		try {
			// Im lazy
			return mapper.readValue("\"" + name.toUpperCase() + "\"", typeRef);
		}catch(Exception e) {
			e.printStackTrace();
			throw new NoSuchElementException("unit class " + name.toUpperCase() + " not recognized.");
		}
	}
	
	
	// Comma seprated enum fields.
	public static <T extends Enum<T>> List<T> getEnums(String enumString, Class<T> typeRef) throws NoSuchElementException{
		List<T> out = new ArrayList<>();
		Scanner sc = new Scanner(enumString);
		String name = "";
		// Handle exception differently
		try {
			while(sc.hasNext()) {
				name = sc.next();
				T val = mapper.readValue("\"" + name.toUpperCase() + "\"", typeRef);
				out.add(val);
			}		
		}catch(JsonProcessingException e){
			e.printStackTrace();
			throw new NoSuchElementException("Error while parsing enum list: " + name + " not recognized");
		}
		return out;
	}
		
	
	
	
	// Constructs provinces
	public static Map<String, Province> readAdjacency(String provinceFile) {
		try {
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
		}catch(Exception e) {
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
			return null; // Never runs
		}
	}
	public static void readLandlocked(String landlockedFile, Map<String, Province> allProvinces) {
		JsonNode root = Parsing.readTree(new File(landlockedFile));
		for (JsonNode name : root) {
			allProvinces.get(name.asText()).setLandlocked(true);
		}
	}
	
	public static List<Faction> readFactions(String factionFile, Map<String, Province> allProvinces) {
		try {
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
		}catch(Exception e) {
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
			return null; // Never runs
		}
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
