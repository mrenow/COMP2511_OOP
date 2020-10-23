package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
public class Infrastructure {
	private ItemType type;
	private int level;
	private int buildingWealth;
	private int wealthRate;
	
	@JsonCreator
	public Infrastructure(
			@JsonProperty("type") 			ItemType type,
			@JsonProperty("level") 			int level,
			@JsonProperty("buildingWealth") int buildingWealth,
			@JsonProperty("wealthRate") 	int wealthRate) {
		this.type = type;
		this.level = level;
		this.buildingWealth = buildingWealth;
		this.wealthRate = wealthRate;
	}
	
	@JsonGetter
	public ItemType getType() {
		return type;
	}

	@JsonGetter
	public int getLevel() {
		return level;
	}

	@JsonGetter
	public int getBuildingWealth() {
		return buildingWealth;
	}

	@JsonGetter
	public int getWealthRate() {
		return wealthRate;
	}
	
}
