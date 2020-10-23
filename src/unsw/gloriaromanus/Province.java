package unsw.gloriaromanus;

import java.util.List;
import java.util.Set;

public class Province {
	private Faction owner;
	private int buildingwealth;
	private int townwealth;
	private List<Unit> units; 
	private TaxLevel taxrate;

	
	public Province(Faction owner, int buildingwealth, int townwealth, List<Unit> units, TaxLevel taxrate) {
		super();
		this.owner = owner;
		this.buildingwealth = buildingwealth;
		this.townwealth = townwealth;
		this.units = units;
		this.taxrate = taxrate;
	}

	public Faction getOwner() {return null;}
	
	public boolean isLandlocked() {return false;}
	
	public List<Province> getAdjacent() {return null;}
	
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
