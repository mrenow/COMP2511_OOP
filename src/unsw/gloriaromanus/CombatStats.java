package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An information struct which contains all the data needed to determine the result of an engagement.
 * 
 */

@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class CombatStats {
	
	// Base characteristics
	// base = Double.POSITIVE_INFINITY -> infinite of that characteristic, regardless of modifiers
	// base = Double.NEGATIVE_INFINITY -> exactly 0 of that characteristic, regardless of modifiers.
	// Otherwise, the characteristic is calculated using armourAdd and armourMult.
	private double armourBase = 0; 
	private double moraleBase = 0;
	private double speedBase = 0; 
	private double attackBase = 0; 
	

	private double defenseSkill = 0;
	private double shieldDefense = 0;

	// Should not change 
	private double armourMult = 1;
	private double speedMult = 1;
	private double attackMult = 1;
	
	private double armourAdd = 0;
	private double speedAdd = 0;
	private double attackAdd = 0;  


	@JsonCreator
	private CombatStats() {}
	
	public CombatStats(double armour, double morale, double speed, double attack, double defenseSkill,
			double shieldDefense) {
		super();
		this.armourBase = armour;
		this.moraleBase = morale;
		this.speedBase = speed;
		this.attackBase = attack;
		this.defenseSkill = defenseSkill;
		this.shieldDefense = shieldDefense;
	}

	public CombatStats(ItemType type) {
		// TODO Load all relevant fields from type.
	}
	public double getArmour() {
		if (armourBase == Double.NEGATIVE_INFINITY) {
			return 0;
		}
		if (armourBase == Double.POSITIVE_INFINITY) {
			return armourBase;
		}
		return Math.max(1, (armourBase + armourAdd) * armourMult);
	}
	public double getSpeed() {
		if (speedBase == Double.NEGATIVE_INFINITY) {
			return 0;
		}
		if (speedBase == Double.POSITIVE_INFINITY) {
			return speedBase;
		}
		return Math.max(1, (speedBase + speedAdd) * speedMult);
	}
	public double getAttack() {
		if (attackBase == Double.NEGATIVE_INFINITY) {
			return 0;
		}
		if (attackBase == Double.POSITIVE_INFINITY) {
			return attackBase;
		}
		return Math.max(1, (attackBase + attackAdd) * attackMult);
	}
	
	public double getDefenseSkill() {
		if (defenseSkill == Double.NEGATIVE_INFINITY) {
			return 0;
		}
		return defenseSkill;
	}
	public double getShieldDefense() {
		if (shieldDefense == Double.NEGATIVE_INFINITY) {
			return 0;
		}
		return shieldDefense;
	}
	
	public void setArmour(double armour) {
		if (armourBase == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.armourBase = armour;
	}
	public void setMorale(double morale) {
		if (moraleBase == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.moraleBase = morale;
	}
	public void setSpeed(double speed) {
		if (speedBase == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.speedBase = speed;
	}
	public void setAttack(double attack) {
		if (attackBase == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.attackBase = attack;
	}
	public void setShieldDefense(double shieldDefense) {
		if (shieldDefense == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.shieldDefense = shieldDefense;
	}
	public void setDefenseSkill(double defenseSkill) {
		if (defenseSkill == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.defenseSkill = defenseSkill;
	}
	
	void addArmour(double armour) {
		this.armourAdd += armour;
	}
	void addSpeed(double speed) {
		this.speedAdd += speed;
	}
	void addAttack(double attack) {
		this.attackAdd += attack;
	}
	
	void addDefenseSkill(double defenseSkill) {
		this.defenseSkill += defenseSkill;
	}
	void addShieldDefense(double shieldDefense) {
		this.shieldDefense += shieldDefense;
	}
	
	void multArmour(double armourMult) {
		this.armourMult *= armourMult;
	}

	void multSpeed(double speedMult) {
		this.speedMult *= speedMult;
	}
	void multAttack(double attackMult) {
		this.attackMult *= attackMult;
	}
	
}
