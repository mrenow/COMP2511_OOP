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
	
	public CombatData(Unit attackerUnit, Unit defenderUnit, List<Unit> attackerArmy, List<Unit> defenderArmy) {
		data.put(ATTACKER, new SideData(attackerUnit, attackerArmy));
		data.put(DEFENDER, new SideData(defenderUnit, defenderArmy));
	}

	public List<Unit> getArmy(BattleSide side) {
		return data.get(side).army;
	}

	public Unit getUnit(BattleSide side) {
		return data.get(side).unit;
	}

	/* Curse you demeter!!!!! */
	public double getArmour(BattleSide side) {
		return data.get(side).stats.getArmour();
	}

	public double getSpeed(BattleSide side) {
		return data.get(side).stats.getSpeed();
	}

	public double getAttack(BattleSide side) {
		return data.get(side).stats.getAttack();
	}

	public double getDefenseSkill(BattleSide side) {
		return data.get(side).stats.getDefenseSkill();
	}

	public double getShieldDefense(BattleSide side) {
		return data.get(side).stats.getShieldDefense();
	}

	void setArmour(BattleSide side, double armour) {
		data.get(side).stats.setArmour(armour);
	}

	void setMorale(BattleSide side, double morale) {
		data.get(side).stats.setMorale(morale);
	}

	void setSpeed(BattleSide side, double speed) {
		data.get(side).stats.setSpeed(speed);
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

	void addSpeed(BattleSide side, double speed) {
		data.get(side).stats.addSpeed(speed);
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

	void multSpeed(BattleSide side, double speedMult) {
		data.get(side).stats.multSpeed(speedMult);
	}

	void multAttack(BattleSide side, double attackMult) {
		data.get(side).stats.multAttack(attackMult);
	}

}
