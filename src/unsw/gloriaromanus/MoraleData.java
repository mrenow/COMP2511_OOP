package unsw.gloriaromanus;

import static unsw.gloriaromanus.BattleSide.*;
import java.util.EnumMap;
import java.util.List;



public class MoraleData {
	public EnumMap<BattleSide, SideData> data = new EnumMap<>(BattleSide.class);
	
	private class SideData {
		private List<Unit> army;
		private Unit unit;
		private double moraleBase;
		private double moraleAdd = 0;
		private double moraleMult = 1;
		SideData(Unit unit, List<Unit> army){
			this.unit = unit;
			this.army = army;
			this.moraleBase = unit.getMorale();
		}
	}
	
	public MoraleData(Unit attackerUnit, Unit defenderUnit, List<Unit> attackerArmy, List<Unit> defenderArmy) {
		data.put(ATTACK, new SideData(attackerUnit, attackerArmy));
		data.put(ATTACK, new SideData(defenderUnit, defenderArmy));
		
	}
	
	
	public List<Unit> getArmy(BattleSide side){
		return data.get(side).army;
	}
	
	public Unit getUnit(BattleSide side){
		return data.get(side).unit;
	}
	
	/**
	 * This should be called only once with non-infinite values for val.
	 * 
	 */
	public void setMorale(BattleSide side, double val) {
		// Neg inf proceeds all
		if(data.get(side).moraleBase != Double.NEGATIVE_INFINITY) {
			data.get(side).moraleBase = val;
		}
	}
	
	public void addMorale(BattleSide side, double val) {
		data.get(side).moraleAdd += val;
	}
	
	public void multMorale(BattleSide side, double val) {
		data.get(side).moraleMult *= val;
	}
	/**
	 * 
	 * @param side
	 * @return The final morale calculation for the unit of the given side.
	 */
	public double getMorale(BattleSide side) {
		if(data.get(side).moraleBase == Double.NEGATIVE_INFINITY) {
			return 0;
		}else if (data.get(side).moraleBase == Double.POSITIVE_INFINITY) {
			return data.get(side).moraleBase;
		}
		return (data.get(side).moraleBase + data.get(side).moraleAdd) * data.get(side).moraleMult;
	}
}

