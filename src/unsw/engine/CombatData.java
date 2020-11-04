package unsw.gloriaromanus;

import static unsw.gloriaromanus.BattleSide.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * An intermediate state belonging to the battle system. Not serialized.
 * Contains all the information needed to simulate an engagement.
 * 
 * 
 * @author ezra
 */
class CombatData {

	private boolean hasWalls;
	private boolean isRanged;
	private Map<BattleSide, SideData> data = new EnumMap<>(BattleSide.class);

	
	private class SideData {
		private List<Unit> army;
		private Unit unit;
		//private CombatStats stats;	// Should not change 
		private double armourBase = 1; 
		private double attackBase = 1; 
		private double defenseSkill = 0;
		private double shieldDefense = 0;
		
		private double armourMult = 1;
		private double attackMult = 1;
		private double armourAdd = 0;
		private double attackAdd = 0;  


		SideData(Unit unit, List<Unit> army) {
			this.unit = unit;
			this.army = army;
			this.armourBase = unit.getArmour();
			this.attackBase = unit.getAttack();
			this.defenseSkill = unit.getDefenseSkill();
			this.shieldDefense = unit.getShieldDefense();
		}
	}
	 
	CombatData(Unit attackerUnit, Unit defenderUnit, List<Unit> attackerArmy, List<Unit> defenderArmy, boolean isRanged, boolean hasWalls) {
		data.put(ATTACK, new SideData(attackerUnit, attackerArmy));
		data.put(DEFEND, new SideData(defenderUnit, defenderArmy));
		this.isRanged = isRanged;
		this.hasWalls = hasWalls;
	}

	List<Unit> getArmy(BattleSide side) {
		return data(side).army;
	}

	Unit getUnit(BattleSide side) {
		return data(side).unit;
	}

	/* Curse you demeter!!!!! */
	boolean isRanged() {
		return isRanged;
	}
	boolean hasWalls() {
		return hasWalls;
	}

	public double getEffectiveArmour(BattleSide side) {
		return getShieldDefense(side) + getDefenseSkill(side) + getArmour(side);
	}
	
	public double getArmour(BattleSide side) {
		if (data(side).armourBase == Double.NEGATIVE_INFINITY) {
			return 0;
		}
		if (data(side).armourBase == Double.POSITIVE_INFINITY) {
			return data(side).armourBase;
		}
		return Math.max(1, (data(side).armourBase + data(side).armourAdd) * data(side).armourMult);
	}
	
	public double getAttack(BattleSide side) {
		if (data(side).attackBase == Double.NEGATIVE_INFINITY) {
			return 0;
		}
		if (data(side).attackBase == Double.POSITIVE_INFINITY) {
			return data(side).attackBase;
		}
		return Math.max(1, (data(side).attackBase + data(side).attackAdd) * data(side).attackMult);
	}
	
	public double getDefenseSkill(BattleSide side) {

		return Math.max(0, data(side).defenseSkill);
	}
	
	public double getShieldDefense(BattleSide side) {
		return Math.max(0, data(side).shieldDefense);
	}
	
	void setArmour(BattleSide side, double armour) {
		if (data(side).armourBase == Double.NEGATIVE_INFINITY) {
			return;
		}
		data(side).armourBase = armour;
	}
	void setAttack(BattleSide side, double attack) {
		if (data(side).attackBase == Double.NEGATIVE_INFINITY) {
			return;
		}
		data(side).attackBase = attack;
	}
	void setShieldDefense(BattleSide side, double shieldDefense) {
		if (data(side).shieldDefense == Double.NEGATIVE_INFINITY) {
			return;
		}
		data(side).shieldDefense = shieldDefense;
	}
	void setDefenseSkill(BattleSide side, double defenseSkill) {
		if (data(side).defenseSkill == Double.NEGATIVE_INFINITY) {
			return;
		}
		data(side).defenseSkill = defenseSkill;
	}
	
	void addArmour(BattleSide side, double armour) {
		data(side).armourAdd += armour;
	}
	void addAttack(BattleSide side, double attack) {
		data(side).attackAdd += attack;
	}
	
	void addDefenseSkill(BattleSide side, double defenseSkill) {
		data(side).defenseSkill += defenseSkill;
	}
	void addShieldDefense(BattleSide side, double shieldDefense) {
		data(side).shieldDefense += shieldDefense;
	}
	
	void multArmour(BattleSide side, double armourMult) {
		data(side).armourMult *= armourMult;
	}

	void multAttack(BattleSide side,double attackMult) {
		data(side).attackMult *= attackMult;
	}
	
	void multShieldDefense(BattleSide side,double shieldDefense) {
		data(side).shieldDefense *= shieldDefense;
	}
	private SideData data(BattleSide side) {
		return data.get(side);
	}

}
