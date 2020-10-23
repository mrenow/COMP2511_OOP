package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.ANY)
public class Province {
	// Required, assigned in constructor
	@JsonIgnore private String name; 

	private Faction owner = Faction.NO_ONE; 

	private List<Province> adjacent = new ArrayList<Province>();
	
	private int buildingwealth = 0;
	private int townwealth = 0;
	private TaxLevel taxrate = TaxLevel.NORMAL_TAX;

	private boolean isLandlocked = true;

	private List<Unit> units = new ArrayList<Unit>(); 
	private List<BuildingSlotEntry> buildingSlots = new ArrayList<BuildingSlotEntry>();
	private List<TrainingSlotEntry> trainingSlots = new ArrayList<TrainingSlotEntry>();

	@JsonCreator
	Province(
			@JsonProperty("name") String name){
		this.owner = owner;
		this.name = name;
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

	public Faction getOwner() {return null;}
	
	public boolean isLandlocked() {return false;}
	
	public List<Province> getAdjacent() {return new ArrayList<Province>(adjacent);}
	
	public int getWealth() {return 0;}
	
	public String getName() {return null;}
	
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
	
	/**
	 * Only used to setup the province.
	 */
	void addConnection(Province p) {
		this.adjacent.add(p);
	}
	void setOwner(Faction owner) {
		this.owner = owner;
	}
}
