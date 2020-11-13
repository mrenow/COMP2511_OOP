package unsw.engine;

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

import util.Concatenator;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class Province {
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

	private int trainingSlotNum = 1;
	private int buildingSlotNum = 1;
	private int maxSlots = 1;
	private Faction player;

	@JsonCreator
	private Province() {
	}


	/**
	 * Should not be used outside setup
	 */
	void setLandlocked(boolean state) {
		isLandlocked = state;
	}

	public Faction getOwner() {
		return owner;
	}

	// Used for ports, which arent in the game >.<
	public boolean isLandlocked() {
		return isLandlocked;
	}

	public List<Province> getAdjacent() {
		return new ArrayList<>(adjacent);
	}

	public int getWealth() {
		return getTotalWealth();
	}

	public String getName() {
		return name;
	}

	public TaxLevel getTaxLevel() {
		return taxLevel;
	}

	public int getTrainingSlots() {
		return trainingSlotNum;
	}

	public int getBuildingSlots() {
		return buildingSlotNum;
	}

	public int buildingWealth() {
		int bWealth = 0;
		for (Infrastructure building : buildings) {
			bWealth += building.getBuildingWealth();
		}
		return bWealth;
	}

	public int getTownWealth() {
		return townWealth;
	}

	public int getTotalWealth() {
		return getTownWealth() + buildingWealth();
	}

	/**
	 * updateWealth of province
	 * 
	 * @return how much gold generated
	 */
	public int updateWealth() {
		double gold = (this.townWealth + this.buildingWealth()) * this.taxLevel.getTaxRate();
		this.townWealth += getWealthGrowth() + taxLevel.getwealthgen();
		this.townWealth = Integer.max(townWealth, 0);
		updateBuildingWealth();
		int g = (int) gold;
		double remain = gold % g;
		if (remain >= 0.5) {
			return g + 1;
		}
		return g;
	}

	private int getWealthGrowth() {
		int wealthGrowth = 0;
		for (Infrastructure building : buildings) {
			wealthGrowth += building.getWealthRate();
		}
		return wealthGrowth;
	}

	private void updateBuildingWealth() {
		// TODO :add updateBuildingWealth
	}

//	Ordered list corresponding to training slots
	public List<TrainingSlotEntry> getCurrentTraining() {
		List<TrainingSlotEntry> copy = new ArrayList<>(trainingSlots);
		return copy;
	}

//	As above. Only a single element list for milestone 2.
	public List<BuildingSlotEntry> getCurrentConstruction() {
		return this.buildingSlots;
	}

//	Called when the unit selection menu of a province is opened, and used to select units to move.
	public List<Unit> getUnits() {
		return new ArrayList<>(units);
	}

//	Called when displaying infrastructure
	public List<Infrastructure> getInfrastructure() {
		return buildings;
	}

//	Called when province building menu is opened
	public List<ItemType> getBuildable() {
		return null;
	}

//	Called when province training menu is opened
	public List<ItemType> getTrainable() {
		List<ItemType> trainableList = new ArrayList<>();
		trainableList.add(ItemType.TEST_TROOP);
		trainableList.add(ItemType.HEAVY_CAVALRY);
		trainableList.add(ItemType.JAVELIN_SKIRMISHER);
		trainableList.add(ItemType.ELEPHANTS);
		trainableList.add(ItemType.ROMAN_LEGIONARY);
		trainableList.add(ItemType.TREBUCHET);
		trainableList.add(ItemType.ARCHER);
		trainableList.add(ItemType.SPEARMEN);
		trainableList.add(ItemType.PIKEMEN);
		trainableList.add(ItemType.HOPILITE);
		trainableList.add(ItemType.BERSERKER);
		trainableList.add(ItemType.HORSE_ARCHER);
		trainableList.add(ItemType.LIGHT_CAVALRY);
		trainableList.add(ItemType.DRUID);
		trainableList.add(ItemType.CHARIOTS);
		return trainableList;
	}
	
	/**
	 * rough estimate of miltiatry strength. Does not factor in morale or speed.
	 * @return
	 */
	public int getMilitaryIndex() {
		return Unit.getMilitaryIndex(getUnits());
	}
	
	public int getDevelopmentIndex(Province p) {
		
		// no buildings
		return 0;
	}
	
	public int getMovCost() {
		return movCost;
	}

	public boolean isConquered() {
		return isConquered;
	}
	// public void moveUnits(List<Unit> units){this.units.add(units);}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Sets owner. If owner is different to previous, Item slots are cleared.
	 */
	void changeOwner(Faction owner) {
		System.out.println("Change owner");
		if (owner != this.owner) {
			this.owner = owner;
			onConquered();
		}
	}

	/**
	 * All state changes when province switches hands. IMPORTANT: New units entering
	 * a province must only enter *after* this method is called. (Or they will be deleted)
	 */
	private void onConquered() {
		trainingSlotNum += trainingSlots.size();
		buildingSlotNum += buildingSlots.size();
		buildingSlots.clear();
		trainingSlots.clear();
		units.clear();
		taxLevel = TaxLevel.NORMAL_TAX;
		isConquered = true;
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

	void removeUnits(List<Unit> units) {
		this.units.removeAll(units);
	}

	void removeUnit(Unit unit) {
		this.units.remove(unit);
	}

	// Used during load only.
	void loadOwner(Faction owner) {
		this.owner = owner;
	}

	/*
	 * INFRASTRUCTURE AND TRAINING
	 */

	void addBuilding(ItemType type) {
		Infrastructure t = new Infrastructure(type, 1);
		this.buildings.add(t);
	}

	void build(ItemType type) {
		// Start building
		BuildingSlotEntry t = new BuildingSlotEntry(type, 1);
		this.buildingSlots.add(t);
		// Adjust gold values
		int buildCost = type.getCost(1);
		owner.adjustGold(buildCost);
		// Adjust buildingslotnum
		buildingSlotNum -= 1;
	}

	void trainUnit(ItemType unit) {
		// Check if unit is trainable
		if (getTrainable().contains(unit)) {
			// Train Unit
			TrainingSlotEntry u = new TrainingSlotEntry(unit, 1, this);
			this.trainingSlots.add(u);
			// Adjust gold values
			int trainCost = unit.getCost(1);
			owner.adjustGold(trainCost);
			// Adjust trainingslotnum
			trainingSlotNum -= 1;
		}
	}

	void trainAdjustUnit(TrainingSlotEntry entry) {
		// Remove unit from slot
		this.trainingSlots.remove(entry);
		// Adjust trainingslotnum
		adjustTraining();
	}

	void adjustTraining() {
		trainingSlotNum += 1;
	}

	void buildFinishInfrastructure(ItemType type) {
		// Remove build from slot
		BuildingSlotEntry b = new BuildingSlotEntry(type, 1);
		this.buildingSlots.remove(b);
		adjustBuilding();
	}

	void adjustBuilding() {
		buildingSlotNum += 1;
	}

	void addUnit(ItemType type) {
		Unit u = Unit.newUnit(type, 1, this);
		this.units.add(u);
	}

	void update() {
		isConquered = false;
		List<ItemSlotEntry> copyTrainingSlots = new ArrayList<>(trainingSlots);
		List<ItemSlotEntry> copyBuildingSlots = new ArrayList<>(buildingSlots);
		new Concatenator<>(copyBuildingSlots).and(copyTrainingSlots).forEach(m -> m.update());
		units.forEach(u -> u.update());
	}

	public void putLostEagles(int numEagles) {
		owner.putLostEagles(this, numEagles);
		
	}
}
