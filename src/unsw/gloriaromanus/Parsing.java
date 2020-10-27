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
	
	
	// Constructs provinces
	public static Map<String, Province> readAdjacency(String provinceFile) throws JsonProcessingException, IOException {
		Map<String, Province> provinceMap= new HashMap<>();
		// MUST COPY BEFORE EDITING MAPPER CONFIG
		ObjectMapper om = mapper.copy();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<Province> allProvinces = om.readValue(new File(provinceFile), new TypeReference<List<Province>>() {});
		System.out.println(allProvinces);
		for (Province p : allProvinces) {
			provinceMap.put(p.getName(), p);
		}
		return provinceMap;
	}
	public static void readLandlocked(String landlockedFile, Map<String, Province> allProvinces) throws JsonProcessingException, IOException {
		JsonNode root = mapper.readTree(new File(landlockedFile));
		for (JsonNode name : root) {
			allProvinces.get(name.asText()).setLandlocked(true);
		}
	}
	
	public static List<Faction> readFactions(String factionFile, Map<String, Province> allProvinces) throws JsonParseException, JsonMappingException, IOException {
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
