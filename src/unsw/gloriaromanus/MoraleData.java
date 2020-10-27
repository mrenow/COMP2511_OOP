package unsw.gloriaromanus;

import static unsw.gloriaromanus.BattleSide.*;
import java.util.EnumMap;
import java.util.List;



public class MoraleData {
	public EnumMap<BattleSide, SideData> data = new EnumMap<>(BattleSide.class);
	
	public MoraleData(Unit attackerUnit, Unit defenderUnit, List<Unit> attackerArmy, List<Unit> defenderArmy) {
		data.put(ATTACKER, new SideData(attackerUnit, attackerArmy));
		data.put(ATTACKER, new SideData(defenderUnit, defenderArmy));
		
	}
	
	public List<Unit> getArmy(BattleSide side){
		return get(side).army;
	}
	public Unit getUnit(BattleSide side){
		return get(side).unit;
	}
	
	public void setMorale(BattleSide side, double val) {
		if(get(side).morale != Double.NEGATIVE_INFINITY) {
			get(side).morale = val;
		}
	}
	
	public void addMorale(BattleSide side, double val) {
		get(side).moraleAdd += val;
	}
	
	public void multMorale(BattleSide side, double val) {
		get(side).moraleMult *= val;
	}
	
	public double getMorale(BattleSide side) {
		if(get(side).morale == Double.NEGATIVE_INFINITY) {
			return 0;
		}
		return (get(side).morale + get(side).moraleAdd) * get(side).moraleMult;
	}
	
	private SideData get(BattleSide side) {
		return data.get(side);
	}
	
	private class SideData {
		private List<Unit> army;
		private Unit unit;
		private double morale;
		private double moraleAdd = 0;
		private double moraleMult = 1;
		SideData(Unit unit, List<Unit> army){
			this.unit = unit;
			this.army = army;
			this.morale = unit.getMorale();
		}
	}
}

