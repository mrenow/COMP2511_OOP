package unsw.gloriaromanus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parsing {
	// Constructs provinces
	public static Map<String, Province> readAdjacency(String provinceFile) throws JsonProcessingException, IOException {
		Map<String, Province> allProvinces = new HashMap<String, Province>();
		ObjectMapper om = new ObjectMapper();
		JsonNode root = om.readTree(new File(provinceFile));
		// First construct all provinces 
		root.fieldNames()
		.forEachRemaining((name)->
			allProvinces.put(name, new Province(name))
			);
		
		// Then construct a connection for each adjacent province
		for(Entry<String, Province> provinceEntry : allProvinces.entrySet()) {
			// Iterate through Json
			for (Iterator<String> adjacencyIter = root.get(provinceEntry.getKey()).fieldNames(); adjacencyIter.hasNext();) {
				String adjacentName = adjacencyIter.next(); 
				if(root.get(adjacentName).asBoolean()) {
					provinceEntry.getValue().addConnection(allProvinces.get(adjacentName));			
				}	
			}
			
		}
		return allProvinces;
	}
	public static void readLandlocked(String landlockedFile, Map<String, Province> allProvinces) throws JsonProcessingException, IOException {
		ObjectMapper om = new ObjectMapper();
		JsonNode root = om.readTree(new File(landlockedFile));
		for (JsonNode name : root) {
			allProvinces.get(name.asText()).setLandlocked(true);
		}
	}
	
	public static List<Faction> readFactions(String factionFile, Map<String, Province> allProvinces) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		JsonNode root = om.readTree(new File(factionFile));
		// Construct factions without provinces
		List<Faction> factionOrder = om.readValue(new File(factionFile), new TypeReference<List<Faction>>() {});
		// Province stuff
		int factionIndex = 0;
		for (JsonNode node : root) {
			List<String> provinceNames = om.readValue(node.get("provinceNames").toString(), new TypeReference<List<String>>(){});
			for (String name: provinceNames) {
				factionOrder.get(factionIndex).takeProvince(allProvinces.get(name));
			}
			factionIndex ++;
		}
		
		return factionOrder;
	}
	/**
	 * Automatically allocates provinces to factions using some algorithm. TODO
	 * @param types
	 * @param provinceMap
	 * @return
	 */
	public static List<Faction> allocateProvinces(List<FactionType> types, Map<String, Province> provinceMap){
		// TODO
		return null;
	}
}
