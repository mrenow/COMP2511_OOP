package unsw.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Unit implements Comparable<Unit>{
	private Map<ActiveType, List<CombatModifierMethod>> combatModifiers = new EnumMap<>(ActiveType.class);
	private Map<ActiveType, List<MoraleModifierMethod>> moraleModifiers = new EnumMap<>(ActiveType.class);

	private int level = 1;
	private ItemType type = ItemType.TEST_TROOP;
	
	@JsonIgnore
	private UnitClass unitClass; // Dependent on ItemType
	
	@JsonIdentityReference(alwaysAsId = true)
	private Province province;
	
	// Unused
	private boolean isMercenary = false;

	// Assigned based on type
	private int health;
	private int maxMovPoints;
	private int movPoints;
	private boolean canAttack = true;
	
	private double speed;
	private double morale;
	private double armour; 
	private double attack; 
	private double defenseSkill;
	private double shieldDefense;
	
	private Unit() {
		combatModifiers.put(ActiveType.SUPPORT, new ArrayList<>());
		combatModifiers.put(ActiveType.ENGAGEMENT, new ArrayList<>());
		moraleModifiers.put(ActiveType.SUPPORT, new ArrayList<>());
		moraleModifiers.put(ActiveType.ENGAGEMENT, new ArrayList<>());
	}
	
	public Unit(ItemType type, UnitClass unitClass ,int level) {
		this();
		
		this.type = type;
		this.level = level;
		this.unitClass = unitClass;
		// By default take typeinfo
		this.maxMovPoints = (Integer)type.getAttribute("movPoints", level);
		this.health = (Integer) type.getAttribute("health", level);
		this.morale = ((Integer) type.getAttribute("morale", level)).doubleValue();
		this.speed = ((Integer) type.getAttribute("speed", level)).doubleValue();
		
		this.movPoints = this.maxMovPoints;
		
		this.armour = ((Integer)type.getAttribute("armour", level)).doubleValue();
		this.attack = ((Integer)type.getAttribute("attack", level)).doubleValue();
		this.defenseSkill = ((Integer)type.getAttribute("defenseSkill", level)).doubleValue();
		this.shieldDefense = ((Integer)type.getAttribute("shieldDefense", level)).doubleValue();
		
		
		switch(type) {
		case BERSERKER:
			armour = Double.NEGATIVE_INFINITY;
			this.morale = Double.POSITIVE_INFINITY;
			break;
		default:
			break;
		}
		
		switch(unitClass) {
		case MELEE_INFANTRY:
			addCombatModifier(CombatModifierMethod._SHIELD_CHARGE);
			break;
		case MELEE_CAVALRY:
			addCombatModifier(CombatModifierMethod._HEROIC_CHARGE_COMBAT);
			addMoraleModifier(MoraleModifierMethod._HEROIC_CHARGE_MORALE);
			break;
		case ARTILLERY:
			addCombatModifier(CombatModifierMethod._ARTILLERY);
			break;
		default:
			break;
		}
		// add remaining modifiers
		// Supports multiple modifiers
		String combatString = (String)type.getAttribute("combatModifiers", level);
		String moraleString = (String)type.getAttribute("moraleModifiers", level);

		Parsing.getEnums(combatString, CombatModifierMethod.class).forEach(this::addCombatModifier);
		Parsing.getEnums(moraleString, MoraleModifierMethod.class).forEach(this::addMoraleModifier);
	}
	@JsonCreator
	public static Unit newUnit(
			@JsonProperty("type") ItemType type,
			@JsonProperty("level") int level,
			@JsonProperty("home") Province home) {
		if(type == null) {
			type = ItemType.TEST_TROOP;
		}
		if(level <= 0) {
			level = 1;
		}
		UnitClass unitClass = null;
		try {
			unitClass = Parsing.getEnum((String)type.getAttribute("class", level), UnitClass.class);
		} catch (NoSuchElementException e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
		} 
		Unit out;
		switch(unitClass) {
		case MELEE_INFANTRY:
		case RANGED:
			out = new Unit(type, unitClass, level);
			break;
		case MELEE_CAVALRY:
			out = new MeleeCavalry(type,level);
			break;
		case ARTILLERY:
			out = new Artillery(type, level);
			break;
// 		case TOWER:
//			out = new Tower(type, level);
//			break;
		default:
			out = null;//should never run
			break;
		}
		
		if(home != null && home.getTaxLevel() == TaxLevel.VERY_HIGH_TAX) {
			out.addMoraleModifier(MoraleModifierMethod.VERY_HIGH_TAX);
		}
		out.province = home;
		
		return out;
	}
	
	/**
	 * Conditions : u.movPoints >= movPoints.
	 * @param army
	 * @param movPoints
	 */
	public static Collection<Unit> getCasualties(Collection<Unit> army){
		Collection<Unit> out = new ArrayList<>();
		army.forEach(u -> {if(u.isAlive()) { out.add(u);}});
		return out;
	}
	public static int getMilitaryIndex(Collection<Unit> army) {
		double totMilitaryIndex = 0;
		for(Unit u: army) {
			totMilitaryIndex += Math.log(u.getHealth()*u.getArmour())*u.getAttack();
		}
		return (int)Math.round(totMilitaryIndex);
	}
	static void expendMovement(Collection<Unit> army, int movPoints) {
		army.forEach(u-> u.movPoints -= movPoints);
		
	}
	
	static void expendMovement(Collection<Unit> army) {
		army.forEach(u-> u.movPoints = 0);
	}
	
	static void transferArmy(List<Unit> army, Province dest) {
		if(army.size() == 0) {
			return;
		}
		Province start = army.get(0).getProvince();
		start.removeUnits(army);
		dest.addUnits(army);
		army.forEach(u->u.province = dest);
	}
	static void expendInvade(Collection<Unit> army) {
		army.forEach(u->u.canAttack = false);
	}
	public String getName() {
		return type.getName(level);
		
	}
	public String getDescription() {
		return type.getDescription(level);
	}
	
	public String statRep() {
		return String.format("%d♥ %d 🗡 %d(%d) 🛡 %d 🎖️ %d 👞 ",
				health, (int)attack,  (int)shieldDefense + (int)defenseSkill + (int)armour, (int)(shieldDefense + armour), (int)morale, (int)speed);
	}

	public ItemType getType() {
		return type;
	}
	
	
	public UnitClass getUnitClass() {
		return unitClass;
	}
	public int getLevel() {
		return level;
	}

	/**
	 * This function will be called a Gazillion times each battle. Iterators are used here
	 * to reduce the amount of processes spent packaging the methods.
	 * Packages the desired combat modifier methods into their morale modifier objects.
	 * Returns an Iterable so no lists need to be copied.
	 */
	public Iterable<Modifier<CombatData>> getCombatModifiers(ActiveType type, BattleSide side) {
		return new MappingIterable<>(combatModifiers.get(type), m -> new Modifier<>(m, side));
	}
	/**
	 * Packages the desired morale modifier methods into their morale modifier objects.
	 * @see Unit.getCombatModifiers
	 */
	public Iterable<Modifier<MoraleData>> getMoraleModifiers(ActiveType type, BattleSide side) {
		return new MappingIterable<>(moraleModifiers.get(type), m -> new Modifier<>(m, side));
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
		return unitClass == UnitClass.RANGED;
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
	public double getArmour() {
		return armour;
	}

	public double getAttack() {
		return attack;
	}

	public double getDefenseSkill() {
		return defenseSkill;
	}

	public double getShieldDefense() {
		return shieldDefense;
	}

	public Province getProvince() {
		return province;
	}
	public Faction getOwner() {
		return province.getOwner();
	}
	
	public void setProvince(Province province) {
		this.province = province;
	}
	public boolean canAttack() {
		return canAttack;
	}
	
	public boolean isAlive() {
		return health > 0;
	}
	
	public String toString() {
		return getName() + "\t\t" + health + "❤" + movPoints;
	}
	/**
	 * Provinces are the only things that track units.
	 */
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
	/**
	 * Update at the end of a turn
	 * 
	 */
	void update() {
		movPoints = maxMovPoints;
		canAttack = true;
	}

	@Override
	public int compareTo(Unit other) {
		int out;
		if((out = getName().compareTo(other.getName())) != 0) {
			return out;
		}else if ((out = getHealth() - other.getHealth()) != 0) {
			return out;
		}
		return 0;
	}
}


    
