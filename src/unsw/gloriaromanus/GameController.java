package unsw.gloriaromanus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import util.MappingIterable;
import util.MathUtil;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class GameController {
	public static double STARTING_DENSITY = 0.5;
	private Collection<Province> allProvinces;
	private List<Faction> factionOrder;

	//private List<ItemType> currentMercenaries = new ArrayList<ItemType>();
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
			return Parsing.mapper.readValue(new File(saveFilename), GameController.class);
		}catch(Exception e) {
			e.printStackTrace(); 
			throw new DataInitializationException("Error while loading game", e);
		}
		
	}
	/**
	 * Saves game in human readable json
	 */
	public void saveGame(String saveFilename) throws IOException {
		Parsing.mapper.writerWithDefaultPrettyPrinter().writeValue(new File(saveFilename), this);
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
			if(landlockedFile != null) {
				Parsing.readLandlocked(landlockedFile, provinceMap);
			}
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
			if(landlockedFile != null) {
				Parsing.readLandlocked(landlockedFile, provinceMap);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DataInitializationException("Error while constructing provinces", e);
		}
		try {	
			this.factionOrder = Parsing.allocateProvinces(factionTypes, provinceMap, STARTING_DENSITY);
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
//	NOW NOT NEEDED FOR MILESTONE 2
	public void hireUnit(Province province, ItemType unitType) {
		Faction currFaction = getCurrentTurn();
		int level = 0;
		//TODO need create a mercenaries
		//Unit unit = new Unit();

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
		// Create a unit with unitType
		//List<ItemType> trainable = province.getTrainable();
		if (province.getTrainingSlots() > 0) {
			province.trainUnit(unitType);
		} else {
			System.out.println("Failed to train unit");
		}
	}

	// Constraints:
//	ItemType must be in getBuildable()
//	Free slots must be available
	public void buildInfrastructure(Province province, ItemType infraType) {
		if (province.getBuildable().contains(infraType) && province.getInfrastructureSlots() > 0) {
			//can be build
			// add build to building queue #func in province
			province.build(infraType);
		} else {
			//cannot build
			//print to terminal cannot bulid
			System.out.println("Unable to build infrastructure.");
		}
		// should return the entry??
	}
	
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
		province.adjustTraining();
	}
	
//	Constraints: None
	public void setTax (Province province, TaxLevel taxLevel) {
		province.setTaxLevel(taxLevel);
	}
	
	// /**
	//  * select units in a province
	//  * @param province
	//  * @param units
	//  * @return true if province owner make select, false for else
	//  */
	// public boolean selectUnits(Province province, List<Unit> units){
	// 	//find out who is selecting
	// 	Faction currFaction = this.factionOrder.get(this.currentTurn);

	// 	if(!currFaction.getProvinces().contains(province)){
	// 		return false;
	// 	} else {
	// 		//TODO :how to store select units
	// 		Faction faction;
	// 		return false;//TODO return just keeping the compiler happy
	// 	}
	// }
	/**	Constraints:
	 * defender in getAttackable(attacker);
	 */
	public AttackInfo invade (Province attacker, Province defender) {
		//check if adjency
		return invade(attacker.getUnits(), defender);
	}
	/**
	 * 
	 * Constraints:
	 * There exists a province P such that:
	 * defender in getAttackable(p)
	 * for all u in attackers, u.getProvince() == p
	 * @param attackers
	 * @param defender
	 * @return
	 */
	public AttackInfo invade(List<Unit>attackers,Province defender){

		Faction attackOwner = attackers.get(0).getProvince().getOwner();
		Battle battle = new Battle(attackers,defender);
		AttackInfo attackInfo = battle.getResult();
		//change owner
		if (attackInfo==AttackInfo.WIN) {
			attackOwner.takeProvince(defender);
			Unit.transferArmy(attackers, defender);
			Unit.expendMovement(attackers);
		}

		return attackInfo;
	}
	
//	Constraints:
//	destination from getDestinations()
//	all units have the same home province
	public void move (List<Unit> unitGroup, Province destination) {
		// units moving to a province conquered on this turn lose all movement points
		Province start = unitGroup.get(0).getProvince();
		if (destination.isConquered()) {
			Unit.transferArmy(unitGroup, destination);
			Unit.expendMovement(unitGroup);
			return;
		}
		// Init seen list.
		Map<Province, Integer> seen = new HashMap<>(allProvinces.size());
		for(Province p : allProvinces) {
			seen.put(p, Integer.MAX_VALUE);
		}
		
		int movCost = -1;
		Queue<Province> queued = new LinkedList<>();
		
		Faction faction = start.getOwner();
		
		queued.add(start);
		seen.put(start, 0);
		while(!queued.isEmpty()) {
			Province p = queued.poll();
			
			// If province has been conquered on this turn, then do not add any neighbours.
			if(p.isConquered()) {
				continue;
			}
			
			// Mov point cost of all neighboring provinces.
			int dist = seen.get(p) + p.getMovCost();
			// For each adjacent province 
			for (Province q : p.getAdjacent()) {
				if(q.equals(destination)) {
					movCost = dist;
					queued.clear();
					break;
				}
				if(seen.get(q) <= dist || !faction.equals(q.getOwner())) {
					continue;
				}
				seen.put(q, dist);
				queued.add(q);
			}
		}
		assert movCost != -1 : "either contract was breached or algorithm is buggy";
		
		// Move units between provinces and subtract mov points accordingly.
		Unit.transferArmy(unitGroup, destination);
		Unit.expendMovement(unitGroup, movCost);
		
	}
	
	
//	Increases the turn counter by 1 
//	returns non-null VictoryInfo if the player ending their turn has won.
	public VictoryInfo endTurn() {
		this.currentTurn++;
		if (this.factionOrder.size()<=this.currentTurn) {
			this.round++;
			this.currentTurn = this.currentTurn%this.factionOrder.size();	
		}
		getCurrentTurn().updateWealth();
		return checkVictory();
	}
	
//	returns non-null VictoryInfo if the player ending their turn has won.
	public VictoryInfo checkVictory() {
		return getCurrentTurn().getVictoryInfo();
		/*if (vInfo.isVictory()) {
			return vInfo;
		} else {
			return null;
		}*/
	}
	
/* Testing only (could become a game mechanic but doubt) */
	
	public void disownProvince(Province province) {
		assert(getCurrentTurn() == province.getOwner());
		List<Unit> disownedUnits = new ArrayList<>(province.getUnits());
		Faction.NO_ONE.takeProvince(province);
		province.addUnits(disownedUnits);
	}
	
/* Getters */
	
//	Called when a group of units is selected to determine which provinces to highlight
//  Does not return the province the units are in.
// 	Modify later to return a path?s
	public Collection<Province> getDestinations(List<Unit> unitGroup){
		
		int distMax = MathUtil.min(new MappingIterable<>(unitGroup, Unit::getMovPoints));
		// If there are no units in the group or the group cannot move, return nothing,
		if(distMax == 0 || unitGroup.size() == 0) {
			return Collections.emptySet();
		}
		Set<Province> out = new HashSet<Province>();
		
		Map<Province, Integer> seen = new HashMap<>(allProvinces.size());
		for(Province p : allProvinces) {
			seen.put(p, Integer.MAX_VALUE);
		}
		
		Queue<Province> queued = new LinkedList<>();
		
		Province start = unitGroup.get(0).getProvince();
		Faction faction = start.getOwner();
		// dont add start to out
		queued.add(start);
		seen.put(start, 0);
		while(!queued.isEmpty()) {
			Province curr = queued.poll();
			// Distance to neighbours
			int distNext = seen.get(curr) + curr.getMovCost();
			// Conquered provinces are impassable.
			if(distNext > distMax || curr.isConquered()) {
				continue;
			}
			// Distance ok.
			// for each neighbour:
			for (Province next : curr.getAdjacent()) {
				if(seen.get(next) <= distNext || !faction.equals(next.getOwner())) {
					continue;
				}
				out.add(next);
				seen.put(next, distNext);
				queued.add(next);
			}
		}
		return out;
	}
	
//	Called when highlighting provinces to attack
	public Collection<Province> getAttackable(Province attacker){
		// cannot attack out of a conquered province.
		if (attacker.isConquered()) {
			return Collections.emptyList();
		}
		Collection<Province> out = new ArrayList<>();
		for (Province p : attacker.getAdjacent()) {
			if(p.getOwner() != attacker.getOwner()) {
				out.add(p);
			}
		}
		return out;
	}
	
//	Called when the mercenary hire menu is opened
	public List<ItemType> getMercenaries(){return null;}
	
	public List<Faction> getFactions(){
		return new ArrayList<Faction>(factionOrder);
		}
	
//	returns provinces owned by a particular faction. Will return all provinces when Faction == null
	public Collection<Province> getProvinces(Faction faction){
		if(faction != null) {
			return faction.getProvinces();
		}else {
			return new ArrayList<Province>(allProvinces);
		}
	}
	
//	returns the province under the specified location
/* 	To implement in M3
	public Province getProvince(Point location) {
		return null;
	}
	*/
//	returns the province with this name (debug / testing only).
	public Province getProvince(String name){
		for(Province p : allProvinces) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
		
	}
	public int getNumProvinces() {
		return allProvinces.size();
	}
//	Displayed on UI
	public int getYear() {
		return 200+round;
	}
	
}
