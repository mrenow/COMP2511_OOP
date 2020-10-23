package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.ANY)
public class Infrastructure {
	private ItemType type = ItemType.TEST_BUILDING;
	private int level;
	private int buildingWealth;
	private int wealthRate;
	
	@JsonCreator
	private Infrastructure() {}
	
	public Infrastructure(ItemType type, int level, int buildingWealth,int wealthRate) {
		this.type = type;
		this.level = level;
		this.buildingWealth = buildingWealth;
		this.wealthRate = wealthRate;
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
