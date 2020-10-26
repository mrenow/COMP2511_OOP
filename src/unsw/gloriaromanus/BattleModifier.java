package unsw.gloriaromanus;

import java.util.List;
import java.util.Random;
import java.lang.Math;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Identifies a battle modifying strategy
 * @param e
 * @return
 */

	
	
public enum BattleModifier {
	//
	DRUIDIC_FERVOUR{
		// This is really dumb but 2511 has forced my hand
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			List<Unit> allies = e.armies[side.ordinal()];
			// search allies for druids
			int numDruids = 0;
			for (Unit u : allies) { 
				if(u.getType() == ItemType.DRUID) {
					numDruids++;	
				}
			}
			// divvy up the contributions over all the druids in the army.
			double moraleBuff= Math.pow(1 + 0.1*Math.min(numDruids, 5), 1.0/numDruids);
			double moraleDebuff = Math.pow(1 - 0.1*Math.min(numDruids, 5), 1.0/numDruids);
			// Apply morale bonus
			e.unitCharacteristics[side.ordinal()].morale *= moraleBuff;
			e.unitCharacteristics[side.other().ordinal()].morale *= moraleDebuff;
		}
	},	
	FIRE_ARROWS{
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			
			
		}
	},
	ARTILLERY{

		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			
			
		}
	},
	TOWER{
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
		
		}
		
	},

	ELEPHANTS_AMOK{
		@Override
		//10% chance that casualties will be inflicted on enemy.
		public void alterEngagement(EngagementData e, BattleSide side) {
			Random r = new Random();
			if(r.nextInt(10) != 0) {
				return;
			}
			List<Unit> allies = e.armies[side.ordinal()];
			// Switch the unit which will be damaged by this unit.
			e.units[side.other().ordinal()] = allies.get(r.nextInt(allies.size()));
		}
		
	},
	SHIELD_CHARGE{
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			// TODO Every 4th charge? 
		}
	};
	
	
	public abstract void alterEngagement(EngagementData e, BattleSide side);
}
