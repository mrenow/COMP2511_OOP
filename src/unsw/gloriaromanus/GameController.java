package unsw.gloriaromanus;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameController {
	private List<Faction> factionTurnOrder;
	private List<Province> allProvinces;
	private Set<Province> landlockedProvinces;
	

	public static GameController loadFromSave(String saveFilename) {
		ObjectMapper om = new ObjectMapper();
		
		
	}
		
	/**
	 * Uses file contents to decide ownership. Turn order and factions decided by
	 * the order in which factions appear in the file at <code>ownershipFilePath</code>.
	 * primarily used for testing backend.
	 * @param adjacencyFilePath
	 * @param ownershipFilepath
	 * @param landlockedFilepath
	 */

	public GameController(String adjacencyFilePath,
			String landlockedFilepath, String ownershipFilepath) {
	}
	
	/**
	 * Uses default province allocation algorithm to decide ownership
	 * Uses faction list order to determine turn order.
	 * @param factions
	 * @param adjacencyFilePath
	 * @param landlockedFilePath
	 */
	public GameController(String adjacencyFilePath, String landlockedFilePath,
			List<FactionType> factions) {
	
	}
	public Faction getCurrentTurn() {return null;}
	
//	Constraints:
//	UnitType must be in getMercenaries()
	public void hireUnit(Province province, ItemType unitType) {}
	
//	Constraints:
//	ItemType must be in getTrainable()
//	Free slots must be available
	public void trainUnit (Province province, ItemType unitType) {}
	
//	Constraints:
//	ItemType must be in getBuildable()
//	Free slots must be available
	public void buildInfrastructure(Province province, ItemType infraType) {} // also upgrade
	
//	Constraints:
//	InfrastructureEntity must be in getCurrentInfrastructure 
	public void cancelInfrastructure(BuildingSlotEntry entry) {}
	
//	Constraints:
//	TrainingEntity must be in getCurrentTraining
	public void cancelTraining(TrainingSlotEntry entry) {}
	
//	Constraints: None
	public void setTax (Province province, TaxLevel taxLevel) {}
	
//	Constraints:
//	attacker.faction != defender.faction
//	Province.adjacent(attacker, defender)
	public AttackInfo attack (Province attacker, Province defender) {return null;}
	
//	Constraints:
//	destination from getDestinations()
//	units.province is invariant
	public void move (List<Unit> units, Province destination) {}
	
//	Increases the turn counter by 1 
//	returns non-null VictoryInfo if the player ending their turn has won.
	public VictoryInfo endTurn() {return null;}
	
//	returns non-null VictoryInfo if the player ending their turn has won.
	public VictoryInfo checkVictory() {return null;}
	
/* Getters */
	
//	Called when a group of units is selected to determine which provinces to highlight
	public List<Province> getDestinations(List<Unit> unitGroup){return null;}
	
//	Called when highlighting provinces to attack
	public List<Province> getAttackable(Province province){return null;}
	
//	Called when the mercenary hire menu is opened
	public List<ItemType> getMercenaries(){return null;}
	
	public List<Faction> getFactions(){return null;}
	
//	returns provinces owned by a particular faction. Will return all provinces when Faction == null
	public List<Province> getProvinces(Faction faction){return null;}
	
//	returns the province under the specified location
	public Province getProvince(Point location) {return null;}
	
//	returns the province with this name (debug / testing only).
	public Province getProvince(String name){return null;}
	
//	Displayed on UI
	public int getYear() {return 0;}

}
