package unsw.gloriaromanus;

import static unsw.gloriaromanus.BattleSide.*;

import java.util.EnumMap;
import java.util.List;


/**
 * An intermediate state belonging to the battle system. Not serialized.
 * Contains all the information needed to simulate an engagement.
 * 
 * 
 * @author ezra
 */
public class CombatData {
	
	private boolean isRanged;
	private EnumMap<BattleSide, SideData> data = new EnumMap<>(BattleSide.class);

	private class SideData {
		private List<Unit> army;
		private Unit unit;
		private CombatStats stats;

		SideData(Unit unit, List<Unit> army) {
			this.unit = unit;
			this.army = army;
			this.stats = unit.getCombatStats();
		}
	}
	 
	public CombatData(Unit attackerUnit, Unit defenderUnit, List<Unit> attackerArmy, List<Unit> defenderArmy, boolean isRanged) {
		data.put(ATTACK, new SideData(attackerUnit, attackerArmy));
		data.put(DEFEND, new SideData(defenderUnit, defenderArmy));
	}

	public List<Unit> getArmy(BattleSide side) {
		return data.get(side).army;
	}

	public Unit getUnit(BattleSide side) {
		return data.get(side).unit;
	}

	/* Curse you demeter!!!!! */
	public boolean isRanged() {
		return isRanged;
	}
	
	public double getAttack(BattleSide side) {
		return data.get(side).stats.getAttack();
	}
	
	public double getArmour(BattleSide side) {
		return data.get(side).stats.getArmour();
	}
	public double getDefenseSkill(BattleSide side) {
		return data.get(side).stats.getDefenseSkill();
	}

	public double getShieldDefense(BattleSide side) {
		return data.get(side).stats.getShieldDefense();
	}
	public double getEffectiveArmour(BattleSide side) {
		return getShieldDefense(side) + getDefenseSkill(side) + getArmour(side);
	}

	void setArmour(BattleSide side, double armour) {
		data.get(side).stats.setArmour(armour);
	}

	void setAttack(BattleSide side, double attack) {
		data.get(side).stats.setAttack(attack);
	}

	void setShieldDefense(BattleSide side, double shieldDefense) {
		data.get(side).stats.setShieldDefense(shieldDefense);
	}

	void setDefenseSkill(BattleSide side, double defenseSkill) {
		data.get(side).stats.setDefenseSkill(defenseSkill);
	}

	void addArmour(BattleSide side, double armour) {
		data.get(side).stats.addArmour(armour);
	}

	void addAttack(BattleSide side, double attack) {
		data.get(side).stats.addAttack(attack);
	}

	void addDefenseSkill(BattleSide side, double defenseSkill) {
		data.get(side).stats.addDefenseSkill(defenseSkill);
	}

	void addShieldDefense(BattleSide side, double shieldDefense) {
		data.get(side).stats.addShieldDefense(shieldDefense);
	}

	void multArmour(BattleSide side, double armourMult) {
		data.get(side).stats.multArmour(armourMult);
	}

	void multAttack(BattleSide side, double attackMult) {
		data.get(side).stats.multAttack(attackMult);
	}

	void multShieldDefense(BattleSide side, double shieldDefense) {
		data.get(side).stats.multShieldDefense(shieldDefense);
	}

}
