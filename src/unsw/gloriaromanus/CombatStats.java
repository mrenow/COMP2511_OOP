package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	private double armourBase = 1; 
	private double attackBase = 1; 
	private double defenseSkill = 0;
	private double shieldDefense = 0;

	// Should not change 

	@JsonIgnore
	private double armourMult = 1;
	@JsonIgnore
	private double attackMult = 1;
	@JsonIgnore
	private double armourAdd = 0;
	@JsonIgnore
	private double attackAdd = 0;  


	@JsonCreator
	private CombatStats() {}
	
	public CombatStats(double armour, double attack, double defenseSkill,
			double shieldDefense) {
		super();
		this.armourBase = armour;
		this.attackBase = attack;
		this.defenseSkill = defenseSkill;
		this.shieldDefense = shieldDefense;
	}

	public CombatStats(CombatStats c) {
		this(c.armourBase, c.attackBase, c.defenseSkill, c.shieldDefense);
	}
	public CombatStats(ItemType type, int level) {
		this.armourBase = ((Integer)type.getAttribute("armour", level)).doubleValue();
		this.attackBase = ((Integer)type.getAttribute("attack", level)).doubleValue();
		this.defenseSkill = ((Integer)type.getAttribute("defenseSkill", level)).doubleValue();
		this.shieldDefense = ((Integer)type.getAttribute("shieldDefense", level)).doubleValue();
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

		return Math.max(0, defenseSkill);
	}
	
	public double getShieldDefense() {
		return Math.max(0, shieldDefense);
	}
	
	void setArmour(double armour) {
		if (armourBase == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.armourBase = armour;
	}
	void setAttack(double attack) {
		if (attackBase == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.attackBase = attack;
	}
	void setShieldDefense(double shieldDefense) {
		if (this.shieldDefense == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.shieldDefense = shieldDefense;
	}
	void setDefenseSkill(double defenseSkill) {
		if (this.defenseSkill == Double.NEGATIVE_INFINITY) {
			return;
		}
		this.defenseSkill = defenseSkill;
	}
	
	void addArmour(double armour) {
		this.armourAdd += armour;
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

	void multAttack(double attackMult) {
		this.attackMult *= attackMult;
	}
	
	void multShieldDefense(double shieldDefense) {
		this.shieldDefense *= shieldDefense;
	}
	@Override
	public String toString() {
		return String.format("A %.1f : A %.1f S %.1f D %.1f ", getAttack(), getArmour(), shieldDefense, defenseSkill);
	}
	
}
