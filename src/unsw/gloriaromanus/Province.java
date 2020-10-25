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

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.ANY)
public class Province {
	// Required, assigned in constructor
	@JsonIgnore private String name; 

	// Optional
	@JsonIdentityReference(alwaysAsId = true)
	private Faction owner = Faction.NO_ONE; 

	@JsonIdentityReference(alwaysAsId = true)
	private List<Province> adjacent = new ArrayList<>();
	
	private int buildingWealth = 0;
	private int townWealth = 0;
	private TaxLevel taxRate = TaxLevel.NORMAL_TAX;

	private boolean isLandlocked = false;

	private List<Unit> units = new ArrayList<>();
	private List<BuildingSlotEntry> buildingSlots = new ArrayList<>();
	private List<TrainingSlotEntry> trainingSlots = new ArrayList<>();

	@JsonCreator
	Province(
			@JsonProperty("name") String name){
		this.name = name;
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
	
	public List<Province> getAdjacent() {return new ArrayList<Province>(adjacent);}
	
	public int getWealth() {return 0;}
	
	public String getName() {return name;}
	
	public TaxLevel getTaxLevel() {return null;}
	
	public int getTrainingSlots() {return 0;}
	
	public int getInfrastructureSlots() {return 0;}
	
//	Ordered list corresponding to training slots
	public List<TrainingSlotEntry> getCurrentTraining(){return null;}
	
//	As above. Only a single element list for milestone 2.
	public List<BuildingSlotEntry> getCurrentConstruction(){return null;}
	
//	Called when the unit selection menu of a province is opened, and used to select units to move.
	public List<Unit> getUnits(){return null;}
	
//	Called when displaying infrastructure
	public List<Infrastructure> getInfrastructure(){return null;}
	
//	Called when province building menu is opened
	public List<ItemType> getBuildable(){return null;}
	
//	Called when province training menu is opened
	public List<ItemType> getTrainable(){return null;}

	//public void moveUnits(List<Unit> units){this.units.add(units);}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Sets owner. If owner is different to previous, Item slots are cleared.
	 */
	void changeOwner(Faction owner) {
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
		taxRate = TaxLevel.NORMAL_TAX;
	}

	
}
