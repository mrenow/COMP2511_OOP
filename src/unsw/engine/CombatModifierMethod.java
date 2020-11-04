package unsw.engine;

import static unsw.engine.ActiveType.*;
import static unsw.engine.BattleSide.*;




/**
 * Identifies a battle modifying strategy
 * Probably going to change this a lot yikes
 * Update: DID
 * @param data,
 * @return
 */
enum CombatModifierMethod implements ModifierMethod<CombatData>{
	// ActiveType = null means an exception will be thrown when 
	// unit.addCombatModifier(RANGED) is called.
	_RANGED(null){
		@Override
		public void modify(CombatData data, BattleSide side) {
			if(!data.getUnit(ATTACK).isRanged()) {
				data.setAttack(ATTACK, Double.NEGATIVE_INFINITY);	
			}
			if(!data.getUnit(DEFEND).isRanged()) {
				data.setAttack(DEFEND, Double.NEGATIVE_INFINITY);		
			}
			data.setDefenseSkill(DEFEND, Double.NEGATIVE_INFINITY);
			data.setDefenseSkill(ATTACK, Double.NEGATIVE_INFINITY);
		}

	},
	// Guarantee: Source unit is artillery
	_ARTILLERY(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side) {
			Unit enemy = data.getUnit(side.other());			
//			if(enemy.getUnitClass() == UnitClass.TOWER) {
//				Artillery self = (Artillery)data.getUnit(side);
//				data.setAttack(side, self.getSeigeAttack());
//			}
		}
	},
	// Guarantee: Source unit is tower
	_TOWER(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side) {
			Unit enemy = data.getUnit(side);
			if(!(enemy instanceof Artillery)) {
				// non-artillery units cannot damage towers
				data.setAttack(side.other(), Double.NEGATIVE_INFINITY);
			}
			// data is given to be ranged. See RANGED(null)
		}
	},	// Guarantee: unit is melee infantry
	_SHIELD_CHARGE(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side) {
			if(GlobalRandom.nextUniform() < 0.25) {
				data.addAttack(side, data.getShieldDefense(side));				
			}
		}
	},	// Guarantee: Unit is cavalry and melee
	_HEROIC_CHARGE_COMBAT(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side) {
			// When army has <50% of enemy units, apply this
			// Double attack dmg, 50% inc morale
			Unit enemy = data.getUnit(side.other());
			MeleeCavalry self = (MeleeCavalry) data.getUnit(side);

			double chargeAttack = self.getChargeAttack();
			if (self.getHealth() * 2 < enemy.getHealth()) {
				chargeAttack *= 2;
			}
			data.addAttack(side, chargeAttack);
			
		}
	},
	
	// Guarantee: Source unit is ranged
	FIRE_ARROWS_COMBAT(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side) {
			data.multAttack(side, 0.9);
		}
	},

	SKIRMISHER_ANTI_ARMOUR(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side) {
			if (data.isRanged()) {
				// Enemy only has half armour value
				data.multArmour(side.other(), 0.5);
			}
		}
	},
	/* Done in Unit Creation instead
	BERSERKER_RAGE(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side){
			// Unit has infinite morale, double meleeATK, no armour, no shield
			data.multAttack(side, 2);
			data.multArmour(side, Double.NEGATIVE_INFINITY);
			data.setDefenseSkill(side, Double.NEGATIVE_INFINITY);
		}
	},
	PHALANX(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side) {
			// TODO Unit has double meleeDEF, half speed
			data.multShieldDefense(side, 2);
		}
	},
	*/
	CANTABRIAN_CIRCLE(ENGAGEMENT){
		@Override
		public void modify(CombatData data, BattleSide side) {
			// TODO Enemy missile units 50% loss missileatkDMG
			Unit enemy = data.getUnit(side.other());
			if (enemy.isRanged()) {
				data.multAttack(side.other(), 0.5);
			}
		}
	};
	
	private ActiveType active;
	private String description;
	
	private CombatModifierMethod(ActiveType active) {
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