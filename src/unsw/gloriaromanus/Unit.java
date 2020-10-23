package unsw.gloriaromanus;

import java.util.List;

/**
 * Represents a basic unit of soldiers
 * 
 * incomplete - should have heavy infantry, skirmishers, spearmen, lancers, heavy cavalry, elephants, chariots, archers, slingers, horse-archers, onagers, ballista, etc...
 * higher classes include ranged infantry, cavalry, infantry, artillery
 * 
 * current version represents a heavy infantry unit (almost no range, decent armour and morale)
 */
public class Unit {
	private ItemType type;
	private BattleCharacteristic baseCharacteristic;
	private List<BattleModifier> supportModifiers;
	private List<BattleModifier> engagementModifiers;
	
	private int maxmovpoints;
	private int movPoints;
	private Province province;
	private boolean ismercenary;
	private int health;


    public int getNumTroops(){
        return numTroops;
    }


	public Unit(ItemType type, BattleCharacteristic baseCharacteristic, List<BattleModifier> supportModifiers,
			List<BattleModifier> engagementModifiers, int maxmovpoints, int movPoints, Province province,
			boolean ismercenary, int health) {
		super();
		this.type = type;
		this.baseCharacteristic = baseCharacteristic;
		this.supportModifiers = supportModifiers;
		this.engagementModifiers = engagementModifiers;
		this.maxmovpoints = maxmovpoints;
		this.movPoints = movPoints;
		this.province = province;
		this.ismercenary = ismercenary;
		this.health = health;
	}

    
}
