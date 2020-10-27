package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents a basic unit of soldiers
 * 
 * incomplete - should have heavy infantry, skirmishers, spearmen, lancers, heavy cavalry, elephants, chariots, archers, slingers, horse-archers, onagers, ballista, etc...
 * higher classes include ranged infantry, cavalry, infantry, artillery
 * 
 * current version represents a heavy infantry unit (almost no range, decent armour and morale)
 */

@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class Unit {
	private BattleCharacteristic baseCharacteristic;
	private List<BattleModifier> supportModifiers = new ArrayList<>();
	private List<BattleModifier> engagementModifiers = new ArrayList<>();

	private int level = 1;
	private ItemType type = ItemType.TEST_TROOP;
	
	@JsonIdentityReference(alwaysAsId = true)
	private Province province;
	private boolean isMercenary = false;
	private int health;
	private int maxMovPoints;
	private int movPoints;
	
	@JsonCreator
	public Unit(
			@JsonProperty("type") ItemType newType,
			@JsonProperty("level") int newLevel) {
		if(newType != null) {
			this.type = newType;
		}
		if(newLevel != 0) {
			this.level = newLevel;
		}
		this.maxMovPoints = (Integer)this.type.getAttribute("movPoints", this.level);
		this.health = (Integer)this.type.getAttribute("health", this.level);
		this.baseCharacteristic = new BattleCharacteristic(this.type);
		this.movPoints = this.maxMovPoints;
	}

	public Unit(ItemType type, int level, BattleCharacteristic baseCharacteristic, Province province) {
		super();
		this.type = type;
		this.movPoints = this.maxMovPoints;
		this.maxMovPoints = (Integer)this.type.getAttribute("movPoints", this.level);
		this.health = (Integer)this.type.getAttribute("health", this.level);
		this.movPoints = maxMovPoints;
		this.province = province;
	}
	
	public ItemType getType() {
		return type;
	}

	public BattleCharacteristic getBaseCharacteristic() {
		return baseCharacteristic;
	}

	public List<BattleModifier> getSupportModifiers() {
		return new ArrayList<>(supportModifiers);
	}

	public List<BattleModifier> getEngagementModifiers() {
		return new ArrayList<>(engagementModifiers);
	}

	public int getMaxMovPoints() {
		return maxMovPoints;
	}

	public int getMovPoints() {
		return movPoints;
	}

	public boolean isMercenary() {
		return isMercenary;
	}

	public int getHealth() {
		return health;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}



    
}
