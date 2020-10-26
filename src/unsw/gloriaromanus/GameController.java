package unsw.gloriaromanus;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.ANY)
public class GameController {

	private Collection<Province> allProvinces;
	private List<Faction> factionOrder;
	
	private List<ItemType> currentMercenaries = new ArrayList<ItemType>();
	private int round = 0;
	private int currentTurn = 0; // Must always be less than factionTurnOrder.size()
	
	@JsonCreator
	private GameController() {}
	
	/**
	 * Does what it says on the can.
	 * We try to fill in as many values as we can as defaults so that we can construct sparse, clear testing files.
	 * @param saveFilename
	 * @return Game from save file
	 */
	public static GameController loadFromSave(String saveFilename) throws DataInitializationException {
		try{
			return new ObjectMapper().readValue(new File(saveFilename), GameController.class);
		}catch(Exception e) {
			e.printStackTrace(); 
			throw new DataInitializationException("Error while loading game", e);
		}
		
	}
	/**
	 * Saves game in human readable json
	 */
	public void saveGame(String saveFilename) throws IOException {
		ObjectMapper om = new ObjectMapper();
		om.writerWithDefaultPrettyPrinter().writeValue(new File(saveFilename), this);
	}
		
	/**
	 * Uses file contents to decide ownership. Turn order and factions decided by
	 * the order in which factions appear in the file at <code>ownershipFilePath</code>.
	 * primarily used for testing backend.
	 */
	public GameController(String adjacencyFile,
			String landlockedFile, String factionFile) throws DataInitializationException{
		Map<String, Province> provinceMap;
		try {
			provinceMap = Parsing.readAdjacency(adjacencyFile);
			Parsing.readLandlocked(landlockedFile, provinceMap);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataInitializationException("Error while constructing provinces", e);
		}
		
		try {
			this.factionOrder = Parsing.readFactions(factionFile, provinceMap);
			this.allProvinces = provinceMap.values();
		}catch(Exception e){
			e.printStackTrace();
			throw new DataInitializationException("Error while constructing game from files", e);
		}
	}
	
	/**
	 * Uses default province allocation algorithm to decide ownership
	 * Uses factionTypes list order to determine turn order.
	 */
	public GameController(String adjacencyFile, String landlockedFile,
			List<FactionType> factionTypes) throws DataInitializationException{
		Map<String, Province> provinceMap;
		try {
			provinceMap = Parsing.readAdjacency(adjacencyFile);
			Parsing.readLandlocked(landlockedFile, provinceMap);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataInitializationException("Error while constructing provinces", e);
		}
		try {	
			this.factionOrder = Parsing.allocateProvinces(factionTypes, provinceMap);
			this.allProvinces = provinceMap.values();
		}catch(Exception e){
			e.printStackTrace();
			throw new DataInitializationException("Error while constructing game from automatic allocation", e);
		}
	}

	
	public Faction getCurrentTurn() {
		return this.factionOrder.get(this.currentTurn);
	}
	
//	Constraints:
//	UnitType must be in getMercenaries()
	public void hireUnit(Province province, ItemType unitType) {
		Faction currFaction = this.factionOrder.get(this.currentTurn);
		int level = 0;
		//TODO need create a mercenaries
		Unit unit = new Unit();

		int cost = unitType.getCost(level);
		int gold = currFaction.getGold();
		if (gold<cost) {
			//TODO cant hire: not enought gold
		} else {
			// TODO create a hireFunc in province to complete here
			// province.hireUnit(unit);
		}
	}
	
//	Constraints:
//	ItemType must be in getTrainable()
//	Free slots must be available
	public void trainUnit (Province province, ItemType unitType) {
		//TODO create a unit with unitType
		List<ItemType> trainable = province.getTrainable();
		if (trainable.contains(unitType)) {
			// TODO train Unit
		} else {
			// TODO fail to train unit
		}
	}
	
//	Constraints:
//	ItemType must be in getBuildable()
//	Free slots must be available
	public void buildInfrastructure(Province province, ItemType infraType) {
		if (province.getBuildable().contains(infraType)) {
			//can be build
			// TODO :add build to building queue #func in province
			
			// province.build();
		} else {
			//cannot build
			// TODO :print to terminal cannot bulid
		}
		// should return the entry?
	} // also upgrade
	
//	Constraints:
//	InfrastructureEntity must be in getCurrentInfrastructure 
	public void cancelInfrastructure(BuildingSlotEntry entry) {
		Province province = entry.getProvince();
		province.getCurrentConstruction().remove(entry);
	}
	
//	Constraints:
//	TrainingEntity must be in getCurrentTraining
	public void cancelTraining(TrainingSlotEntry entry) {
		Province province = entry.getProvince();
		province.getCurrentTraining().remove(entry);
	}
	
//	Constraints: None
	public void setTax (Province province, TaxLevel taxLevel) {
		province.setTaxLevel(taxLevel);
	}
	
	/**
	 * select units in a province
	 * @param province
	 * @param units
	 * @return true if province owner make select, false for else
	 */
	public boolean selectUnits(Province province, List<Unit> units){
		//find out who is selecting
		Faction currFaction = this.factionOrder.get(this.currentTurn);

		if(!currFaction.getProvinces().contains(province)){
			return false;
		} else {
			//TODO :how to store select units
			Faction faction;
			return false;//TODO return just keeping the compiler happy
		}
	}
//	Constraints:
//	attacker.faction != defender.faction
//	Province.adjacent(attacker, defender)
	public AttackInfo attack (Province attacker, Province defender) {return null;}
	

	public AttackInfo invade(List<Unit>attackers,List<Unit> defenders){
		
		return null;
	}
//	Constraints:
//	destination from getDestinations()
//	units.province is invariant
	public void move (List<Unit> units, Province destination) {}
	
//	Increases the turn counter by 1 
//	returns non-null VictoryInfo if the player ending their turn has won.
	public VictoryInfo endTurn() {return null;}
	
//	returns non-null VictoryInfo if the player ending their turn has won.
	public VictoryInfo checkVictory() {
		return null;
	}
	
/* Getters */
	
//	Called when a group of units is selected to determine which provinces to highlight
	public List<Province> getDestinations(List<Unit> unitGroup){return null;}
	
//	Called when highlighting provinces to attack
	public List<Province> getAttackable(Province province){return null;}
	
//	Called when the mercenary hire menu is opened
	public List<ItemType> getMercenaries(){return null;}
	
	public List<Faction> getFactions(){return new ArrayList<Faction>(factionOrder);}
	
//	returns provinces owned by a particular faction. Will return all provinces when Faction == null
	public Collection<Province> getProvinces(Faction faction){
		if(faction != null) {
			// TODO
			return null;
		}else {
			return new ArrayList<Province>(allProvinces);
		}
	}
	
//	returns the province under the specified location
	public Province getProvince(Point location) {return null;}
	
//	returns the province with this name (debug / testing only).
	public Province getProvince(String name){return null;}
	
//	Displayed on UI
	public int getYear() {return 0;}
		
}
