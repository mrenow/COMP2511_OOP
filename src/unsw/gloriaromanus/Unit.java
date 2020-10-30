package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import util.MappingIterable;
import util.MathUtil;

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
	private CombatStats baseCharacteristic;
	private EnumMap<ActiveType, List<CombatModifierMethod>> combatModifiers = new EnumMap<>(ActiveType.class);
	private EnumMap<ActiveType, List<MoraleModifierMethod>> moraleModifiers = new EnumMap<>(ActiveType.class);

	private int level = 1;
	private ItemType type = ItemType.TEST_TROOP;
	
	@JsonIdentityReference(alwaysAsId = true)
	private Province province;
	
	private boolean isMercenary = false;

	// Assigned based on type
	private boolean isRanged = false;
	private int health;
	private int maxMovPoints;
	private int movPoints;
	private double speed;
	private double morale;
	
	
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
		// By default take typeinfo
		this.maxMovPoints = (Integer)type.getAttribute("movPoints", level);
		this.health = (Integer) type.getAttribute("health", level);
		this.morale = ((Integer) type.getAttribute("morale", level)).doubleValue();
		this.speed = ((Integer) type.getAttribute("speed", level)).doubleValue();
		
		this.baseCharacteristic = new CombatStats(this.type, this.level);
		this.movPoints = this.maxMovPoints;
		
		// add modifiers
		String combatString = (String)type.getAttribute("combatModifiers", level);
		String moraleString = (String)type.getAttribute("moraleModifiers", level);
		try {
			Parsing.<CombatModifierMethod>getEnums(combatString).forEach(this::addCombatModifier);
			Parsing.<MoraleModifierMethod>getEnums(moraleString).forEach(this::addMoraleModifier);
		} catch (DataInitializationException e) {
			e.printStackTrace();
			// fatal
			System.exit(1);
		}
	}
	

	public ItemType getType() {
		return type;
	}
	public int getLevel() {
		return level;
	}

	public CombatStats getBaseCharacteristic() {
		return baseCharacteristic;
	}

	/**
	 * This function will be called a Gazillion times each battle. Iterators are used here
	 * to reduce the amount of processes spent packaging the methods.
	 * Packages the desired combat modifier methods into their morale modifier objects.
	 * Returns an Iterable so no lists need to be copied.
	 */
	public Iterable<CombatModifier> getCombatModifiers(ActiveType type, BattleSide side) {
		return new MappingIterable<>(combatModifiers.get(type), (m) -> new CombatModifier(m, side));
	}
	/**
	 * Packages the desired morale modifier methods into their morale modifier objects.
	 * @see Unit.getCombatModifiers
	 */
	public Iterable<MoraleModifier> getMoraleModifiers(ActiveType type, BattleSide side) {
		return new MappingIterable<>(moraleModifiers.get(type), (m) -> new MoraleModifier(m, side));
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
	public boolean isRanged() {
		return isRanged;
	}

	public int getHealth() {
		return health;
	}

	public double getMorale() {
		return morale;
	}

	public double getSpeed() {
		return speed;
	}

	public Province getProvince() {
		return province;
	}
	
	public void setProvince(Province province) {
		this.province = province;
	}

	public CombatStats getCombatStats() {
		
		return null;
	}
	
	public boolean isAlive() {
		return health > 0;
	}
	
	public void kill() {
		province.removeUnit(this);
	}
	
	void addMoraleModifier(MoraleModifierMethod m) {
		moraleModifiers.get(m.getActiveType()).add(m);
	}
	void addCombatModifier(CombatModifierMethod m) {
		combatModifiers.get(m.getActiveType()).add(m);
	}
	void damage(int damage) {
		health = MathUtil.max(health-damage, 0);
	}
	void expendMovement(int cost) {
		movPoints -= cost;
	}
	// Used in loading only
	void loadProvince(Province p) {
		this.province = p;
	} 
}


    
