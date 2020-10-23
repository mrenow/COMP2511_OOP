package unsw.gloriaromanus;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.ANY)
public class Unit {
	private ItemType type = ItemType.TEST_TROOP;
	private BattleCharacteristic baseCharacteristic;
	private List<BattleModifier> supportModifiers;
	private List<BattleModifier> engagementModifiers;
	
	private int maxMovPoints;
	private int movPoints;
	private Province province;
	private boolean isMercenary;
	private int health;

	@JsonCreator
	public Unit() {}

	public Unit(ItemType type, BattleCharacteristic baseCharacteristic, List<BattleModifier> supportModifiers,
			List<BattleModifier> engagementModifiers, int maxMovpoints, int movPoints, Province province,
			boolean isMercenary, int health) {
		super();
		this.type = type;
		this.baseCharacteristic = baseCharacteristic;
		this.supportModifiers = supportModifiers;
		this.engagementModifiers = engagementModifiers;
		this.maxMovPoints = maxMovpoints;
		this.movPoints = movPoints;
		this.province = province;
		this.isMercenary = isMercenary;
		this.health = health;
	}

    
}
