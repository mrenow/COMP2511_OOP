package unsw.engine;

import static unsw.engine.BattleSide.DEFEND;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.geojson.Point;

import unsw.engine.VicCondition.VicComponent;
import unsw.engine.VicCondition.VicComposite;
import unsw.engine.VicCondition.VictoryCondition;
import unsw.ui.Observer.Observable;
import unsw.ui.Observer.Observer;
import unsw.ui.Observer.*;

import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol.Style;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import util.Concatenator;
import util.MappingIterable;
import util.MathUtil;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import javafx.scene.paint.Color;

/**
 * Game controller class
 * Exposes an API which has getter functions and action methods.
 * The return value of a getter function is called "valid" as long as 
 * no action methods have been called after the value has been retrieved.
 * @author ezra
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class GameController {
	public static double STARTING_DENSITY = 0.5;
	public static int AVERAGE_BARBARIANS = 4;
	// A reference to NO_ONE so the json parser knows about it.
	private Faction NO_ONE = Faction.NO_ONE;
	
	private Collection<Province> allProvinces;
	private List<Faction> factionOrder;
	// ARGB
	private Map<FactionType, Integer> factionColourMap = new EnumMap<>(FactionType.class);

	//private List<ItemType> currentMercenaries = new ArrayList<ItemType>();
	private int round = 0;
	private int currentTurn = 0; // Must always be less than factionTurnOrder.size()
	
	
	// Observable events
	
	@JsonIgnore private Observable<TurnFeatureInfo> triggerTurnChanged = new Observable<>();
	@JsonIgnore private Observable<Province> triggerProvinceChanged = new Observable<>();
	@JsonIgnore private Observable<Province> triggerTrainingChanged = new Observable<>();
	@JsonIgnore private Observable<Province> triggerBuildingChanged = new Observable<>();
	@JsonIgnore private Observable<Province> triggerUnitsChanged = new Observable<>();
	@JsonIgnore private Observable<BattleInfo> triggerBattleEnd = new Observable<>();
	private boolean hasWon;
	
	/**
	 * called on endTurn()
	 */
	public void attatchTurnChangedObserver(Observer<TurnFeatureInfo> o) {
		triggerTurnChanged.attach(o);
	}
	/**
	 * Observer called whenever a game function caused a province to change.
	 */
	public void attatchProvinceChangedObserver(Observer<Province> o) {
		triggerProvinceChanged.attach(o);
	}
	/**
	 * Observer called whenever the training list in a particular province is changed
	 */
	public void attatchTrainingChangedObserver(Observer<Province> o) {
		triggerTrainingChanged.attach(o);
	}
	/**
	 * Observer called when the buidlingSlot list in a particular province is changed
	 */
	public void attatchBuildingChangedObserver(Observer<Province> o) {
		triggerBuildingChanged.attach(o);
	}
	/**
	 * Observer called whenever the unit list in a particular province is changed
	 */
	public void attatchUnitsChangedObserver(Observer<Province> o) {
		triggerUnitsChanged.attach(o);
	}
	
	/**
	 * Observer called whenever the unit list in a particular province is changed
	 */
	public void attatchBattleEndObserver(Observer<BattleInfo> o) {
		triggerBattleEnd.attach(o);
	}
	
	
	

	@JsonCreator
	private GameController() {}
	
	/**
	 * Does what it says on the can.
	 * We try to fill in as many values as we can as defaults so that we can construct sparse, clear testing files.
	 * @param saveFilename
	 * @return Game from save file
	 */
	public static GameController loadFromSave(String saveFilename) throws DataInitializationError {
		try{
			return Parsing.readValue(new File(saveFilename), GameController.class);
		}catch(Exception e) {
			e.printStackTrace(); 
			throw new DataInitializationError("Error while loading game", e);
		}
	}
	/**
	 * Saves game in human readable json
	 */
	public void saveGame(String saveFilename) throws IOException {
		Parsing.writeValue(new File(saveFilename), this);
	}
		
	/**
	 * Uses file contents to decide ownership. Turn order and factions decided by
	 * the order in which factions appear in the file at <code>ownershipFilePath</code>.
	 * primarily used for testing backend.
	 */
	
	public GameController(String adjacencyFile,
			String landlockedFile, String factionFile, VicComponent conditions) throws DataInitializationError{
		
		Map<String, Province> provinceMap;
		provinceMap = Parsing.readAdjacency(adjacencyFile);
		if(landlockedFile != null) {
			Parsing.readLandlocked(landlockedFile, provinceMap);
		}
		this.factionOrder = Parsing.readFactions(factionFile, provinceMap);
		this.allProvinces = provinceMap.values();
		setGoal(conditions);
		setupEntities();
	}


	/**
	 * Uses default province allocation algorithm to decide ownership
	 * Uses factionTypes list order to determine turn order.
	 */
	public GameController(String adjacencyFile, String landlockedFile,
			List<FactionType> factionTypes, VicComponent conditions) throws DataInitializationError{
		Map<String, Province> provinceMap;
		provinceMap = Parsing.readAdjacency(adjacencyFile);
		if(landlockedFile != null) {
			Parsing.readLandlocked(landlockedFile, provinceMap);
		}
		this.factionOrder = Parsing.allocateProvinces(factionTypes, provinceMap, STARTING_DENSITY);
		this.allProvinces = provinceMap.values();
		setGoal(conditions);
		setupEntities();
	}
	
	private void setupEntities() {
		Random r = new Random();
		for (Faction f : factionOrder) {
			factionColourMap.put(f.getType(), 0x60000000 | (0x00FFFFFF & ColorUtil.colorToArgb(Color.hsb(r.nextDouble()*360, 1 - r.nextDouble()*0.3, r.nextDouble()))));
		}
		factionColourMap.put(FactionType.NO_ONE, 0);
		spawnBarbarianUnits(AVERAGE_BARBARIANS);
	}
	
	
	private void setGoal(VicComponent vic){
		for (Faction faction : factionOrder) {
			faction.setVicComponent(vic);
		}
	}

	/**
	 * Helper method for init
	 * Current algorithm just uses a gaussian distribution around N units.
	 * @pre allProvinces list is properly populated
	 * @pre provinces have been assigned to factions
	 * @post unowned provinced have units.
	 */
	private void spawnBarbarianUnits(int mean) {
		for (Province p : allProvinces) {
			if(p.getOwner() != Faction.NO_ONE) {
				//continue;
			}
			int numUnits = (int)Math.round(Math.max(1, mean + GlobalRandom.nextGaussian() * mean));
			while(numUnits-- > 0) {
				p.addUnit(ItemType.BARBARIAN);
			}
		}
	}
	
/*
 * 
 * API
 * 
 */

	
//	Constraints:
//	UnitType must be in getMercenaries()
//	NOW NOT NEEDED FOR MILESTONE 2
/*	public void hireUnit(Province province, ItemType unitType) {
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
	}*/
	
/**
 * @pre province in getProvinces(getCurrentTurn())
 * @pre	unitType contained in a valid return value of getTrainable()
 * @pre province.getTrainingSlots() > 0
 * @post province.getTrainingSlots() is reduced by 1
 * @post province.getCurrentTraining() contains an new entry such that entry.getType() == unitType
 * @param province
 * @param unitType
 */
	
//	Free slots must be available
	public void trainUnit (Province province, ItemType unitType) {
		// Create a unit with unitType
		province.trainUnit(unitType);
		
		triggerProvinceChanged.notifyUpdate(province);
		triggerTrainingChanged.notifyUpdate(province);
	}

	// Constraints:
	/**
	 * @pre province in getProvinces(getCurrentTurn())
	 * @pre infraType in province.getBuildable()
	 * @pre province.getBuildingSlots() > 0
	 * @post province.getBuildingSlots() is reduced by 1
	 * @post entry such that entry.getType() == infraType contained within new call to province.getCurrentConstruction()
	 * @param province
	 * @param infraType
	 */
	public void buildInfrastructure(Province province, ItemType infraType) {
		// add build to building queue #func in province
		province.build(infraType);
		
		triggerProvinceChanged.notifyUpdate(province);
		triggerBuildingChanged.notifyUpdate(province);
	}
	
	/**
	 * @pre For some province in getProvinces(getCurrentTurn()), entry in province.getCurrentConstruction()
	 * @post entry not in new call to province.getCurrentBuilding()
	 * @param entry
	 */
	public void cancelInfrastructure(BuildingSlotEntry entry) {
		Province province = entry.getProvince();
		province.getCurrentConstruction().remove(entry);
		
		triggerProvinceChanged.notifyUpdate(province);
		triggerBuildingChanged.notifyUpdate(province);
	}
	/**
	 * @pre For some province in getProvinces(getCurrentTurn()), entry in province.getCurrentTraining()
	 * @param entry
	 */
	public void cancelTraining(TrainingSlotEntry entry) {
		Province province = entry.getProvince();
		province.trainAdjustUnit(entry);
		
		triggerProvinceChanged.notifyUpdate(province);
		triggerTrainingChanged.notifyUpdate(province);
	}
	
	/**
	 * @pre province is in getProvinces(getCurrentTurn())
	 * @post province.getTaxLevel() == taxLevel
	 * @param province
	 * @param taxLevel
	 */
	public void setTax (Province province, TaxLevel taxLevel) {
		province.setTaxLevel(taxLevel);
		triggerProvinceChanged.notifyUpdate(province);
		triggerProvinceChanged.notifyUpdate(province);
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

	/**
	 * @pre attacker is in getProvinces(getCurrentTurn())
	 * @param attacker
	 * @param defender
	 * @return
	 */
	public BattleInfo invade (Province attacker, Province defender) {
		return invade(attacker.getUnits(), defender);
	}
	/**
	 * 
	 * @pre For some province in getProvinces(getCurrentTurn()), attackers is a subset of province.getUnits()
	 * @pre defender is in getAttackable(attackers)
	 * @pre for each unit in attackers, unit.canAttack() == true
	 * @param attackers
	 * @param defender
	 * @return
	 */
	public BattleInfo invade(List<Unit>attackers,Province defender){
		Province attacker = attackers.get(0).getProvince();
		Faction attackOwner = attackers.get(0).getOwner();
		Faction defendOwner = defender.getOwner();
		Battle battle = new Battle(attackers, defender.getUnits());
		battle.setNumEagles(BattleSide.ATTACK, attackOwner.getNumLostEagles());
		battle.setNumEagles(BattleSide.DEFEND, defendOwner.getNumLostEagles());
		int defNumEagles = 0;
		for (Unit u  : defender.getUnits()) {
			if(u.getType() == ItemType.ROMAN_LEGIONARY) {
				defNumEagles++;
			}
		}
		
		BattleInfo info = battle.getResult();
		
		BattleResult attackInfo = info.getResult();
		
		// done after the battle so battle can function without province information.
		for (Unit u : new Concatenator<>(attackers,defender.getUnits())) {
			if(!u.isAlive()) {
				u.kill();		
			}
		}
		
		// After attackinfo is assigned
		// if the current province is taken, lost eagles are assigned to this province
		if(attackInfo == BattleResult.WIN) {
			defender.putLostEagles(defNumEagles);
			// Finally, change owner and move armies
			attackOwner.takeProvince(defender);
			Unit.transferArmy(attackers, defender);
		}
		Unit.expendInvade(attackers);
		Unit.expendMovement(attackers); // could get the province's updated list, but like ceebs, it shouldnt break anything
		triggerUnitsChanged.notifyUpdate(attacker);
		triggerUnitsChanged.notifyUpdate(defender);
		triggerProvinceChanged.notifyUpdate(attacker);
		triggerProvinceChanged.notifyUpdate(defender);
		triggerBattleEnd.notifyUpdate(info);
		return info;
	}
	
	/**
	 * @pre For some province in getProvinces(getCurrentTurn()), unitGroup is a subset of province.getUnits()
	 * @pre destination in getDestinations(unitGroup)
	 * @post unitGroup not a subset of a new call to province.getUnits()
	 * @post unitGroup is now a subset of a new call to destination.getUnits()
	 * @invariant province.getUnits() union destination.getUnits()
	 * @param unitGroup
	 * @param destination
	 */
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
		triggerUnitsChanged.notifyUpdate(start);
		triggerUnitsChanged.notifyUpdate(destination);
		triggerProvinceChanged.notifyUpdate(start);
		triggerProvinceChanged.notifyUpdate(destination);
	}
	
	/**
	 * @return null if no one has won, otherwise returns a victory info object detailing who has won and by which victory.
	 */
	public VicComponent endTurn() {
		Faction curr = getCurrentTurn();
		curr.update();//update faction's gold and province wealth
		updateVictoryInfo();
		VicComponent vic = checkVictory();
		this.currentTurn++;
		if (this.factionOrder.size() == this.currentTurn) {
			this.round++;
			this.currentTurn = 0;	
		}
		if(vic != null) hasWon = true;	
		TurnFeatureInfo m = new TurnFeatureInfo(this);
		triggerTurnChanged.notifyUpdate(m);
		return vic;
	}
	private void updateVictoryInfo(){
		Faction faction = getCurrentTurn();
		VicComponent vic = faction.getVicComponent();
		double ownpro = faction.getProvinces().size();
		double allpro = this.getNumProvinces();
		Integer gold = faction.getGold();
		Integer wealth = faction.getTotalWealth();
		//System.out.println(gold.toString()+"  " +wealth.toString());//test
		vic.update(VictoryCondition.CONQUEST, ownpro/allpro);
		vic.update(VictoryCondition.TREASURY, 
		//0.5//test
		gold.doubleValue()/1000.0
		);
		vic.update(VictoryCondition.WEALTH, 
		wealth.doubleValue()/400.0
		//0.5//test
		);
		faction.setVicComponent(vic);
	}
//	returns non-null VictoryInfo if the player ending their turn has won.
	public VicComponent checkVictory() {
		//return getCurrentTurn().getVictoryInfo();
		VicComponent vicComposite = getCurrentTurn().getVicComponent();
		if (vicComposite.checkVic()) {
			return vicComposite;
		} else {
			return null;
		}
	}
	
/* Testing only (could become a game mechanic but doubt) */
	/**
	 * @pre province in getProvinces(getCurrentTurn())
	 * @post province not contained in new call to getProvinces(getCurrentTurn())
	 * @param province
	 */
	public void disownProvince(Province province) {
		assert(getCurrentTurn() == province.getOwner());
		List<Unit> disownedUnits = new ArrayList<>(province.getUnits());
		Faction.NO_ONE.takeProvince(province);
		province.addUnits(disownedUnits);
	}
	
/*
 * 
 * GETTERS
 * 
 */
	
	/**
	 * Called when a group of units is selected to determine which provinces to highlight
	 * Does not return the province the units are in.
	 * Modify later to return a paths?	
	 * @pre For some province in getProvinces(getCurrentTurn()) unitGroup subset of province.getUnits()
	 * @param unitGroup
	 * @return Collection of provinces which the specified unitGroup can move to on this turn.
	 */
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
	
	/**
	 * Called when highlighting provinces to attack, and picking provinces to attack
	 * @pre For some province in getProvinces(getCurrentTurn()), attackers must be a subset of of province.getUnits(). 
	 * @pre for each unit in attackers, unit.canAttack() == true
	 * @param attackers
	 * @return
	 */
	public Collection<Province> getAttackable(List<Unit> attackers){
		// cannot attack out of a conquered province.
		if(attackers.size() == 0) {
			return Collections.emptyList();
		}
		Province sourceProvince = attackers.get(0).getProvince();
		if (sourceProvince.isConquered()) {
			return Collections.emptyList();
		}
		Collection<Province> out = new ArrayList<>();
		for (Province p : sourceProvince.getAdjacent()) {
			if(p.getOwner() != sourceProvince.getOwner()) {
				out.add(p);
			}
		}
		return out;
	}
	
/**
 * Called when the mercenary hire menu is opened
 * @return
 */
//	public List<ItemType> getMercenaries(){return null;}
	
	public List<Faction> getFactions(){
		return new ArrayList<Faction>(factionOrder);
	}

	/**
	 * @param faction
	 * @return provinces owned by a particular faction. Will return all provinces when Faction == null.
	 * Will return nothing if faction == NO_ONE.
	 */
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
	

	/**
	 * Returns the current faction's turn
	 * @return
	 */
	public Faction getCurrentTurn() {
		return this.factionOrder.get(this.currentTurn);
	}
	
	/**
	 * @param name
	 * @return the province with this name (debug / testing only).
	 */
	public Province getProvince(String name){
		for(Province p : allProvinces) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	public Integer getFactionColour(Faction f) {
		return getFactionColour(f.getType());
	}
	public Integer getFactionColour(FactionType type) {
		return factionColourMap.get(type);
	}
	public Integer getSolidFactionColour(Faction f) {
		return getSolidFactionColour(f.getType());
		
	}
	public Integer getSolidFactionColour(FactionType type) {
		return  0xFF000000 | factionColourMap.get(type);
	}
	
	public int getNumProvinces() {
		return allProvinces.size();
	}
	
	public int getYear() {
		return 200+round;
	}
	public boolean hasWon() {
		return hasWon;
	}
	


}
