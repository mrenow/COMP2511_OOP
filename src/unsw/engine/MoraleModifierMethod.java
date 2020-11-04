package unsw.engine;
import static unsw.engine.ActiveType.*;

import java.util.List;


enum MoraleModifierMethod implements ModifierMethod<MoraleData> {
	_HEROIC_CHARGE_MORALE(ENGAGEMENT) {
		@Override
		public void modify(MoraleData data, BattleSide side) {
			// When army has <50% of enemy units, apply this
			// Double charge attack dmg, 50% inc morale
			Unit enemy = data.getUnit(side.other());
			Unit self = data.getUnit(side);

			if (self.getHealth() * 2 < enemy.getHealth()) {
				data.multMorale(side, 1.5);
			}
		}
	},
	VERY_HIGH_TAX(ENGAGEMENT){
		@Override
		public void modify(MoraleData data, BattleSide side) {
			data.addMorale(side, -1);
		}
	},
	DRUIDIC_FERVOUR(SUPPORT){
		// This is really dumb but 2511 has forced my hand
		@Override
		public void modify(MoraleData data, BattleSide side) {
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
		public void modify(MoraleData data, BattleSide side) {
			data.multMorale(side.other(), 0.8);
		}
	},
	
	LEGIONARY_EAGLE(SUPPORT) {
		@Override
		public void modify(MoraleData data, BattleSide side) {
			data.addMorale(side, 1);
		}
	},
	
	LOST_EAGLE(SUPPORT){
		@Override
		public void modify(MoraleData data, BattleSide side) {
			data.addMorale(side, -0.2);
		}
	};
	

	private ActiveType active;
	private String description;
	
	private MoraleModifierMethod(ActiveType active) {
		this.active = active;
		this.description = ModifierMethod.DESCRIPTIONS.get(super.toString());
	}
	
	
	public ActiveType getActiveType(){
		return active;
	}
	
	public String getDescription() {
		return description;
	}

	public String toString() {
		return super.toString();
		
	}
}
