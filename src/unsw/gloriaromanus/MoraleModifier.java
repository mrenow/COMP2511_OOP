package unsw.gloriaromanus;
import static unsw.gloriaromanus.ActiveType.*;

import java.util.List;
/**
 * Storage struct to pair a side and method
 * 
 */
public class MoraleModifier{
	
	private MoraleModifierMethod method;
	private BattleSide side;
	
	public MoraleModifier(MoraleModifierMethod method, BattleSide side) {
		this.side = side;
		this.method = method;
	}
	
	public void modify(MoraleData data) {
		method.alterMorale(data, side);
	}
}

enum MoraleModifierMethod {
	DRUIDIC_FERVOUR(SUPPORT){
		// This is really dumb but 2511 has forced my hand
		@Override
		public void alterMorale(MoraleData data, BattleSide side) {
			List<Unit> allies = data.getArmy(side);
			// search allies for druids
			int numDruids = 0;
			for (Unit u : allies) { 
				if(u.getType() == ItemType.DRUID) {
					numDruids++;	
				}
			}
			// divvy up the contributions over all the druids in the army.
			double moraleBuff = Math.pow(1 + 0.1*Math.min(numDruids, 5), 1.0/numDruids);
			double moraleDebuff = Math.pow(1 - 0.1*Math.min(numDruids, 5), 1.0/numDruids);
			// Apply morale bonus
			data.multMorale(side, moraleBuff);
			data.multMorale(side.other(), moraleDebuff);
		}
	},
	FIRE_ARROWS_MORALE(ENGAGEMENT){
		@Override
		public void alterMorale(MoraleData data, BattleSide side) {
			data.multMorale(side.other(), 0.8);
		}
	},
	LEGIONARY_EAGLE(SUPPORT) {
		@Override
		public void alterMorale(MoraleData data, BattleSide side) {
			Unit myUnit = data.getUnit(side);
			Province p = myUnit.getProvince();
			// TODO +1 Morale to all units in province
			for ( SOMETHINGHERE : listofunits) {
				data.addMorale(side, 1);
			}
		}
	},
	HEROIC_CHARGE(ENGAGEMENT) {
		@Override
		public void alterMorale(MoraleData data, BattleSide side) {
			// When army has <50% of enemy units, apply this
			// Double attack dmg, 50% inc morale
			Unit enemy = data.getUnit(side.other());
			Unit myUnit = data.getUnit(side);

			int value = (myUnit.getHealth() / enemy.getHealth()) * 100;
			if (value < 50) {
				data.multMorale(side, 1.5);
			}
		}
	};
	

	private ActiveType active;
	
	private MoraleModifierMethod(ActiveType active) {
		this.active = active;
	}

	public abstract void alterMorale(MoraleData e, BattleSide side);
	
	public boolean isSupport(){
		switch(active) {
		case SUPPORT: return true;
		default: return false; 
		}
	}
	public ActiveType getActiveType(){
		return active;
	}
	
	
}
