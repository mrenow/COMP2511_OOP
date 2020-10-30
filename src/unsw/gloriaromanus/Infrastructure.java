package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class Infrastructure {
	// REQUIRED
	private ItemType type = ItemType.TEST_BUILDING;
	private int level = 1;
	
	// OPTIONAL
	private int buildingWealth;
	private int wealthRate;
	
	@JsonCreator
	// These properties must enter into the constructor first before default values are determined.
	public Infrastructure(
			@JsonProperty("type") ItemType newType,
			@JsonProperty("level") int newLevel) {
		if(newType != null) {
			this.type = newType;
		}
		if(newLevel != 0) {
			this.level = newLevel;
		}
		
		// Normal values for building wealth can be overridden in the save file
		this.buildingWealth = (Integer)type.getAttribute("buildingWealth", newLevel);
		this.wealthRate = (Integer)type.getAttribute("wealthRate", newLevel);
	}
	
	public ItemType getType() {
		return type;
	}

	public int getLevel() {
		return level;
	}

	public int getBuildingWealth() {
		return buildingWealth;
	}

	public int getWealthRate() {
		return wealthRate;
	}
	
}
