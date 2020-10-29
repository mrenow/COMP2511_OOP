package unsw.gloriaromanus;

import static unsw.gloriaromanus.ActiveType.ENGAGEMENT;
import static unsw.gloriaromanus.ActiveType.SUPPORT;

/**
 * A Strategy used to modify combatStats before casualties are 
 * inflicted according to some ability or special rule.
 */
public class CombatModifier{
	private CombatModifierMethod method;
	private BattleSide side;
	
	public CombatModifier(CombatModifierMethod method, BattleSide side) {
		this.side = side;
		this.method = method;
	}
	
	public void modify(CombatData data) {
		method.alterEngagement(data, side);
	}
}


/**
 * Identifies a battle modifying strategy
 * Probably going to change this a lot yikes
 * @param data,
 * @return
 */
enum CombatModifierMethod {
	
	WALLS(SUPPORT) {
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			// Check if both are ranged
			// Check if both are meelee
			
			// Otherwise 
			
		}
	},
	RANGED(null){
			@Override
			public void alterEngagement(CombatData data, BattleSide side) {
				if(!data.getUnit(side).isRanged()) {
					data.setAttack(side, Double.NEGATIVE_INFINITY);	
				}			
				data.setDefenseSkill(side, Double.NEGATIVE_INFINITY);
			}
	},

	// Guarantee: Unit is Ranged
	FIRE_ARROWS_DAMAGE(ENGAGEMENT){
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			data.multAttack(side, 0.9);
		}
	},
	// Guarantee: Unit is artillery
	ARTILLERY(ENGAGEMENT){
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			Unit enemy = data.getUnit(side.other());			
			if(enemy.getClass() == Tower.class) {
				Artillery self = (Artillery)data.getUnit(side);
				data.setAttack(side, self.getTowerAttack());
		
			}
		}
	},
	TOWER(ENGAGEMENT){
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			Unit enemy = data.getUnit(side);
			if(enemy.getClass() != Artillery.class) {
				// non-artillery units cannot damage towers
				data.setAttack(side.other(), Double.NEGATIVE_INFINITY);
			}
			// tower datangagements are ranged, so they nullify shield defense
			data.setDefenseSkill(side.other(), Double.NEGATIVE_INFINITY);
		}
	},
	CAVALRY(ENGAGEMENT){
		public void alterEngagement(CombatData data, BattleSide side) {
			// TODO
			Cavalry c = (Cavalry)data.getUnit(side);
			
			
		}

	},
	SHIELD_CHARGE(ENGAGEMENT){
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			if(GlobalRandom.nextUniform() < 0.25) {
				data.addAttack(side, data.getShieldDefense(side));				
			}
		}
	},
	SKIRMISHER_ANTI_ARMOUR(ENGAGEMENT) {
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			// Check if ranged engagement
			// TODO
			if () {
				// Enemy only has half armour value
				data.multArmour(side.other(), 0.5);
			}
		}
	},
	BERSERKER_RAGE(ENGAGEMENT) {
		@Override
		public void alterEngagement(CombatData data, BattleSide side){
			// TODO Unit has infinite morale, double meleeATK, no armour, no shield
				
		}
	},
	PHALANX(ENGAGEMENT) {
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			// TODO Unit has double meleeDEF, half speed

		}
	},
	CANTABRIAN_CIRCLE(ENGAGEMENT) {
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			// TODO Enemy missile units 50% loss missileatkDMG

		}
	},
	HEROIC_CHARGE(ENGAGEMENT) {
		@Override
		public void alterEngagement(CombatData data, BattleSide side) {
			// When army has <50% of enemy units, apply this
			// TODO Double attack dmg, 50% inc morale

		}
	};
	
	
	private ActiveType active;
	private CombatModifierMethod(ActiveType active) {
		this.active = active;
	}
	
	
	public boolean isSupport(){
		switch(active) {
		case SUPPORT: return true;
		default: return false; 
		}
	}

	public ActiveType getActiveType(){
		return active;
	}
	
	
	public abstract void alterEngagement(CombatData data, BattleSide side);
	
}
/**
 * Denotes whether the method is global or local to the given datangagement.
 * null values are accepted: they indicate that this method does not belong to a unit
 * and an error will be thrown when one attempts to add them to a unit.
 */
enum ActiveType {
	SUPPORT, ENGAGEMENT;
}