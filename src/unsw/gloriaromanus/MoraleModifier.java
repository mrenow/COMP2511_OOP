package unsw.gloriaromanus;

import java.util.List;

import static unsw.gloriaromanus.StatType.*;
import static unsw.gloriaromanus.ActiveType.*;

public enum MoraleModifier {
	DRUIDIC_FERVOUR(SUPPORT, MORALE){
		// This is really dumb but 2511 has forced my hand
		@Override
		public void alterMorale(MoraleData d, BattleSide side) {
			List<Unit> allies = d.getArmy(side);
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
			d.multMorale(side, moraleBuff);
			d.multMorale(side.other(), moraleDebuff);
		}
	},
	FIRE_ARROWS_MORALE(ENGAGEMENT, MORALE){
		@Override
		public void alterMorale(MoraleData d, BattleSide side) {
			d.multMorale(side.other(), 0.8);
		}
	},
	;
	
	private ActiveType active;
	private StatType stat;
	
	
	
	public boolean isSupport(){
		switch(active) {
		case SUPPORT: return true;
		default: return false; 
		}
	};
	
	public boolean isMorale() {
		switch(stat) {
		case MORALE: return true;
		default: return false;
		}
	}
	
	private MoraleModifier(ActiveType active, StatType stat) {
		this.active = active;
		this.stat = stat;
	}

	public abstract void alterMorale(MoraleData e, BattleSide side);
	
	
}
