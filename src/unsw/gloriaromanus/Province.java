package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Province {
	private Faction owner;
	private boolean isLandlocked;
	private int buildingwealth;
	private int townwealth;
	private List<Unit> units; 
	private List<Province> adjacent;
	private TaxLevel taxrate;
	private List<BuildingSlotEntry> buildingSlots;
	private List<TrainingSlotEntry> trainingSlots;

	
	/**
	 * Load constructor
	 * @param owner
	 * @param isLandlocked
	 * @param buildingwealth
	 * @param townwealth
	 * @param units
	 * @param adjacent
	 * @param taxrate
	 */
	@JsonCreator
	public Province(
			@JsonProperty("owner") 			Faction owner,
			@JsonProperty("isLandlocked")	boolean isLandlocked,
			@JsonProperty("buildingwealth")	int buildingwealth,
			@JsonProperty("townwealth")		int townwealth,
			@JsonProperty("units")			List<Unit> units,
			@JsonProperty("adjacent")		List<Province> adjacent,
			@JsonProperty("taxrate")		TaxLevel taxrate,
			@JsonProperty("buildingSlots") 	List<BuildingSlotEntry> buildingSlots,
			@JsonProperty("trainingSlots") 	List<TrainingSlotEntry> trainingSlots) {
		super();
		this.owner = owner;
		this.isLandlocked = isLandlocked;
		this.buildingwealth = buildingwealth;
		this.townwealth = townwealth;
		this.units = units;
		this.adjacent = adjacent;
		this.taxrate = taxrate;
	}

	/**
	 * Called during game initialization.
	 * @return
	 */
	public Province(Faction owner, Collection<Province> adjacent, boolean isLandlocked) {
		this.isLandlocked = isLandlocked;
		this.owner = owner;
		
		this.adjacent = new ArrayList<Province>(adjacent);
		
		this.buildingwealth = 0;
		this.townwealth = 0;
		this.units = new ArrayList<Unit>();
		this.taxrate = TaxLevel.NORMAL_TAX;
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
}
