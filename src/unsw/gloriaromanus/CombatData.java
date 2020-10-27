package unsw.gloriaromanus;

import static unsw.gloriaromanus.BattleSide.*;

import java.util.EnumMap;
import java.util.List;


/**
 * An intermediate state belonging to the battle system. Not serialized.
 * Contains all the information needed to simulate an engagement.
 * 
 * @author ezra
 */
public class CombatData {
	private EnumMap<BattleSide, SideData> data = new EnumMap<>(BattleSide.class);

	public CombatData(Unit attackerUnit, Unit defenderUnit, List<Unit> attackerArmy, List<Unit> defenderArmy) {
		data.put(ATTACKER, new SideData(attackerUnit, attackerArmy));
		data.put(DEFENDER, new SideData(defenderUnit, defenderArmy));
	}

	public List<Unit> getArmy(BattleSide side) {
		return get(side).army;
	}

	public Unit getUnit(BattleSide side) {
		return get(side).unit;
	}

	/* Curse you demeter!!!!! */
	public double getArmour(BattleSide side) {
		return get(side).stats.getArmour();
	}

	public double getSpeed(BattleSide side) {
		return get(side).stats.getSpeed();
	}

	public double getAttack(BattleSide side) {
		return get(side).stats.getAttack();
	}

	public double getDefenseSkill(BattleSide side) {
		return get(side).stats.getDefenseSkill();
	}

	public double getShieldDefense(BattleSide side) {
		return get(side).stats.getShieldDefense();
	}

	void setArmour(BattleSide side, double armour) {
		get(side).stats.setArmour(armour);
	}

	void setMorale(BattleSide side, double morale) {
		get(side).stats.setMorale(morale);
	}

	void setSpeed(BattleSide side, double speed) {
		get(side).stats.setSpeed(speed);
	}

	void setAttack(BattleSide side, double attack) {
		get(side).stats.setAttack(attack);
	}

	void setShieldDefense(BattleSide side, double shieldDefense) {
		get(side).stats.setShieldDefense(shieldDefense);
	}

	void setDefenseSkill(BattleSide side, double defenseSkill) {
		get(side).stats.setDefenseSkill(defenseSkill);
	}

	void addArmour(BattleSide side, double armour) {
		get(side).stats.addArmour(armour);
	}

	void addSpeed(BattleSide side, double speed) {
		get(side).stats.addSpeed(speed);
	}

	void addAttack(BattleSide side, double attack) {
		get(side).stats.addAttack(attack);
	}

	void addDefenseSkill(BattleSide side, double defenseSkill) {
		get(side).stats.addDefenseSkill(defenseSkill);
	}

	void addShieldDefense(BattleSide side, double shieldDefense) {
		get(side).stats.addShieldDefense(shieldDefense);
	}

	void multArmour(BattleSide side, double armourMult) {
		get(side).stats.multArmour(armourMult);
	}

	void multSpeed(BattleSide side, double speedMult) {
		get(side).stats.multSpeed(speedMult);
	}

	void multAttack(BattleSide side, double attackMult) {
		get(side).stats.multAttack(attackMult);
	}
	
	private SideData get(BattleSide side) {
		return data.get(side);
	}

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
}
