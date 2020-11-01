package unsw.gloriaromanus;
import static unsw.gloriaromanus.ActiveType.*;

import java.util.List;
/**
 * Storage struct to pair a side and method
 * 
 */
class MoraleModifier{
	
	private MoraleModifierMethod method;
	private BattleSide side;
	
	MoraleModifier(MoraleModifierMethod method, BattleSide side) {
		this.side = side;
		this.method = method;
	}
	@Override
	public String toString() {
		return method.toString();
	}
	
	void modify(MoraleData data) {
		method.alterMorale(data, side);
	}
	
}

enum MoraleModifierMethod {
	_HEROIC_CHARGE_MORALE(ENGAGEMENT) {
		@Override
		void alterMorale(MoraleData data, BattleSide side) {
			// When army has <50% of enemy units, apply this
			// Double charge attack dmg, 50% inc morale
			Unit enemy = data.getUnit(side.other());
			Unit self = data.getUnit(side);

			if (self.getHealth() * 2 < enemy.getHealth()) {
				data.multMorale(side, 1.5);
			}
		}
	},
	DRUIDIC_FERVOUR(SUPPORT){
		// This is really dumb but 2511 has forced my hand
		@Override
		void alterMorale(MoraleData data, BattleSide side) {
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
		void alterMorale(MoraleData data, BattleSide side) {
			data.multMorale(side.other(), 0.8);
		}
	},
	LEGIONARY_EAGLE(SUPPORT) {
		@Override
		void alterMorale(MoraleData data, BattleSide side) {
			data.addMorale(side, 1);
		}
	},
	LOST_EAGLE(SUPPORT){
		void alterMorale(MoraleData data, BattleSide side) {
			data.addMorale(side, -0.2);
		}
	};
	

	private ActiveType active;
	
	private MoraleModifierMethod(ActiveType active) {
		this.active = active;
	}
	
	ActiveType getActiveType(){
		return active;
	}

	abstract void alterMorale(MoraleData e, BattleSide side);

	
	
}
