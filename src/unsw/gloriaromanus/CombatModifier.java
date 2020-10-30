package unsw.gloriaromanus;

import static unsw.gloriaromanus.ActiveType.*;
import static unsw.gloriaromanus.BattleSide.*;

/**
 * A Strategy used to modify combatStats before casualties are 
 * inflicted according to some ability or special rule.
 */
class CombatModifier{
	private CombatModifierMethod method;
	private BattleSide side;
	
	CombatModifier(CombatModifierMethod method, BattleSide side) {
		this.side = side;
		this.method = method;
	}
	
	void modify(CombatData engagement) {
		method.alterEngagement(engagement, side);
	}
}


/**
 * Identifies a battle modifying strategy
 * Probably going to change this a lot yikes
 * Update: DID
 * @param engagement,
 * @return
 */
enum CombatModifierMethod {
	// ActiveType = null means an exception will be thrown when 
	// unit.addCombatModifier(RANGED) is called.
	RANGED(null){
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			if(!engagement.getUnit(ATTACK).isRanged()) {
				engagement.setAttack(ATTACK, Double.NEGATIVE_INFINITY);	
			}
			if(!engagement.getUnit(DEFEND).isRanged()) {
				engagement.setAttack(DEFEND, Double.NEGATIVE_INFINITY);		
			}
			engagement.setDefenseSkill(DEFEND, Double.NEGATIVE_INFINITY);
			engagement.setDefenseSkill(ATTACK, Double.NEGATIVE_INFINITY);
		}
	},

	// Guarantee: Source unit is ranged
	FIRE_ARROWS_COMBAT(ENGAGEMENT){
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			engagement.multAttack(side, 0.9);
		}
	},
	// Guarantee: Source unit is artillery
	ARTILLERY(ENGAGEMENT){
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			Unit enemy = engagement.getUnit(side.other());			
			if(enemy instanceof Tower) {
				Artillery self = (Artillery)engagement.getUnit(side);
				engagement.setAttack(side, self.getSeigeAttack());
			}
		}
	},
	// Guarantee: Source unit is tower
	TOWER(ENGAGEMENT){
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			Unit enemy = engagement.getUnit(side);
			if(!(enemy instanceof Artillery)) {
				// non-artillery units cannot damage towers
				engagement.setAttack(side.other(), Double.NEGATIVE_INFINITY);
			}
			// Engagement is given to be ranged. See RANGED(null)
		}
	},
	SHIELD_CHARGE(ENGAGEMENT){
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			if(GlobalRandom.nextUniform() < 0.25) {
				engagement.addAttack(side, engagement.getShieldDefense(side));				
			}
		}
	},
	SKIRMISHER_ANTI_ARMOUR(ENGAGEMENT) {
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			if (engagement.isRanged()) {
				// Enemy only has half armour value
				engagement.multArmour(side.other(), 0.5);
			}
		}
	},
	/* Done in Unit Creation instead
	BERSERKER_RAGE(ENGAGEMENT) {
		@Override
		void alterEngagement(CombatData engagement, BattleSide side){
			// Unit has infinite morale, double meleeATK, no armour, no shield
			engagement.multAttack(side, 2);
			engagement.multArmour(side, Double.NEGATIVE_INFINITY);
			engagement.setDefenseSkill(side, Double.NEGATIVE_INFINITY);
		}
	},
	PHALANX(ENGAGEMENT) {
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			// TODO Unit has double meleeDEF, half speed
			engagement.multShieldDefense(side, 2);
		}
	},
	*/
	CANTABRIAN_CIRCLE(ENGAGEMENT) {
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			// TODO Enemy missile units 50% loss missileatkDMG
			Unit enemy = engagement.getUnit(side.other());
			if (enemy.isRanged()) {
				engagement.multAttack(side.other(), 0.5);
			}
		}
	},
	// Guarantee: Unit is cavalry and melee
	HEROIC_CHARGE_COMBAT(ENGAGEMENT) {
		@Override
		void alterEngagement(CombatData engagement, BattleSide side) {
			// When army has <50% of enemy units, apply this
			// Double attack dmg, 50% inc morale
			Unit enemy = engagement.getUnit(side.other());
			MeleeCavalry self = (MeleeCavalry) engagement.getUnit(side);

			double chargeAttack = self.getChargeAttack();
			if (self.getHealth() * 2 < enemy.getHealth()) {
				chargeAttack *= 2;
			}
			engagement.addAttack(side, chargeAttack);
			
		}
	};
	
	
	private ActiveType active;
	private CombatModifierMethod(ActiveType active) {
		this.active = active;
	}

	ActiveType getActiveType(){
		return active;
	}
	
	
	abstract void alterEngagement(CombatData engagement, BattleSide side);
	
}