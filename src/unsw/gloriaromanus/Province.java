package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class Province{
	// Required
	private String name; 
	
	// Optional
	@JsonIdentityReference(alwaysAsId = true)
	private Faction owner = Faction.NO_ONE; 

	@JsonIdentityReference(alwaysAsId = true)
	private Collection<Province> adjacent = new ArrayList<>();
	
	private List<Infrastructure> buildings = new ArrayList<>();
	
	// Generated every turn
	private int townWealth = 0;
	private TaxLevel taxLevel = TaxLevel.NORMAL_TAX;
	
	private int movCost = 4;
	private boolean isLandlocked = false;
	private boolean isConquered = false;
	
	private List<Unit> units = new ArrayList<>();
	
	private List<BuildingSlotEntry> buildingSlots = new ArrayList<>();
	private List<TrainingSlotEntry> trainingSlots = new ArrayList<>();
	
	@JsonCreator
	private Province(){
	}

	/*
	 * Used for init only.
	 */
	void assignName(String name){
		if(this.name == null) {
			this.name = name;
		}
	}
	
	/**
	 * Called during game initialization.
	 * @return
	 */
	public Province(Faction owner, Collection<Province> adjacent, boolean isLandlocked) {
		this.isLandlocked = isLandlocked;
		this.owner = owner;	
		this.adjacent.addAll(adjacent);
	}
	
	/**
	 * Should not be used outside setup
	 */
	void addConnection(Province p) {
		this.adjacent.add(p);
	}
	
	/**
	 * Should not be used outside setup
	 */
	void setLandlocked(boolean state) {
		isLandlocked = state;
	}

	public Faction getOwner() {return owner;}
	
	public boolean isLandlocked() {return isLandlocked;}
	
	public List<Province> getAdjacent() {return new ArrayList<>(adjacent);}
	
	public int getWealth() {return 0;}
	
	public String getName() {return name;}
	
	public TaxLevel getTaxLevel() {return null;}
	
	public int getTrainingSlots() {return 0;}
	
	public int getInfrastructureSlots() {return 0;}
	
	public int buildingWealth() {
		return 0;
	}
	
	public int getTownWealth() {
		return 0;
	}
	public int getTotalWealth() {
		return 0;
	}
	public int updateWealth(){
		//do update
		double gold=0;
		gold += (this.townWealth + this.buildingWealth()) * this.taxLevel.getTaxRate();
		this.townWealth += getWealthGrowth() + taxLevel.getwealthgen();
		this.townWealth = Integer.max(townWealth, 0);
		updateBuildingWealth();
		return (int)gold;
	}
	private int getWealthGrowth(){
		//TODO : calculate WealthGrowth
		return 0;
	}
	private void updateBuildingWealth(){
		//TODO :add updateBuildingWealth
	}
//	Ordered list corresponding to training slots
	public List<TrainingSlotEntry> getCurrentTraining(){return null;}
	
//	As above. Only a single element list for milestone 2.
	public List<BuildingSlotEntry> getCurrentConstruction(){return null;}
	
//	Called when the unit selection menu of a province is opened, and used to select units to move.
	public List<Unit> getUnits(){
		return new ArrayList<>(units);
	}
	
//	Called when displaying infrastructure
	public List<Infrastructure> getInfrastructure(){return null;}
	
//	Called when province building menu is opened
	public List<ItemType> getBuildable(){return null;}
	
//	Called when province training menu is opened
	public List<ItemType> getTrainable(){return null;}
	
	public int getMovCost() {
		return movCost;
	}
	public boolean isConquered() {
		return isConquered;
	}
	//public void moveUnits(List<Unit> units){this.units.add(units);}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Sets owner. If owner is different to previous, Item slots are cleared.
	 */
	void changeOwner(Faction owner) {
		System.out.println("Change owner");
		if(owner != this.owner) {
			this.owner = owner;
			onConquered();
		}
	}
	
	/**
	 * All state changes when province switches hands.
	 * IMPORTANT: New units entering a province must only enter *after* this method is called.
	 */
	private void onConquered() {
		buildingSlots.clear();
		trainingSlots.clear();
		units.clear();
		taxLevel = TaxLevel.NORMAL_TAX;
	}

	void setTaxLevel(TaxLevel taxLevel) {
		this.taxLevel = taxLevel;
	}
	/*
	 * Helper method. Directly adds units onto this province.
	 * 
	 */
	void addUnits(List<Unit> units) {
		this.units.addAll(units);
	}
	void removeUnit(Unit unit) {
		this.units.remove(unit);
	}

	// Used during load only.
	void loadOwner(Faction owner) {
		this.owner = owner;
	}

	void addBuilding(ItemType type) {
		Infrastructure t = new Infrastructure(type, 1);
		this.buildings.add(t);
	}
	
	void build(ItemType type) {
		// Start building
		BuildingSlotEntry t = new BuildingSlotEntry(type, 1);
		this.buildingSlots.add(t);
	}

	void trainUnit(ItemType unit) {
		// Train Unit
		TrainingSlotEntry u = new TrainingSlotEntry(unit, 1);
		this.trainingSlots.add(u);
	}

	void addUnit(ItemType type) {
		Unit u = new Unit(type, 1);
		this.units.add(u);
	}

}
