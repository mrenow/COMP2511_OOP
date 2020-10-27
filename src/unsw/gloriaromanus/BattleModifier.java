package unsw.gloriaromanus;

import java.util.List;
import java.util.Random;
import java.lang.Math;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Identifies a battle modifying strategy
 * Probably going to change this a lot yikes
 * @param e,
 * @return
 */
public enum BattleModifier {

	WALLS(ApplicationType.SUPPORT, CharacteristicType.DAMAGE) {
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			// Check if both are ranged
			// Check if both are meelee
			
			// Otherwise 
			
		}
	},
	TRY_RANGED(ApplicationType.SUPPORT, CharacteristicType.DAMAGE){
			@Override
			public void alterEngagement(EngagementData e, BattleSide side) {
				boolean isSelfRanged = e.units[side.ordinal()].isRanged();
				boolean isOtherRanged = e.units[side.ordinal()].isRanged();
				BattleCharacteristic rangedStats = null;
				BattleCharacteristic meleeStats = null;
				
				boolean isRangedEngagement = false;
				if(isSelfRanged && isOtherRanged) {
					e.unitCharacteristics[0].setDefenseSkill(Double.NEGATIVE_INFINITY);
					e.unitCharacteristics[1].setDefenseSkill(Double.NEGATIVE_INFINITY);
					return;
				}
				if(isSelfRanged) {
					rangedStats = e.unitCharacteristics[side.ordinal()];
					meleeStats = e.unitCharacteristics[side.other().ordinal()];
				}
				if(isOtherRanged) {
					rangedStats = e.unitCharacteristics[side.other().ordinal()];
					meleeStats = e.unitCharacteristics[side.ordinal()];
				}
				
				if(rangedStats == null) {
					return; // melee enagagment
				}
				double rangedChance = 0.5 + 0.1*(rangedStats.getSpeed() - meleeStats.getSpeed());
				
				if (GlobalRandom.generator.nextDouble() % 1 < rangedChance) {
					meleeStats.setAttack(Double.NEGATIVE_INFINITY);
					e.unitCharacteristics[0].setDefenseSkill(Double.NEGATIVE_INFINITY);
					e.unitCharacteristics[1].setDefenseSkill(Double.NEGATIVE_INFINITY);
				}
				
			}
	},
	DRUIDIC_FERVOUR(ApplicationType.SUPPORT, CharacteristicType.MORALE){
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
			e.unitCharacteristics[side.ordinal()].applyMoraleMult(moraleBuff);
			e.unitCharacteristics[side.other().ordinal()].applyMoraleMult(moraleDebuff);
		}
	},
	FIRE_ARROWS_MORALE(ApplicationType.ENGAGEMENT, CharacteristicType.MORALE){
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			e.unitCharacteristics[side.other().ordinal()].applyMoraleMult(0.8);
		}
	},
	// Guarantee: Unit is Ranged
	FIRE_ARROWS_DAMAGE(ApplicationType.ENGAGEMENT, CharacteristicType.DAMAGE){
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			BattleCharacteristic self = e.unitCharacteristics[side.ordinal()];
			self.applyAttackMult(0.9);
		}
	},
	// Guarantee: Unit is artillery
	ARTILLERY(ApplicationType.ENGAGEMENT, CharacteristicType.DAMAGE){
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			Unit enemy = e.units[side.other().ordinal()];			
			if(enemy.getClass() == Tower.class) {
				Artillery self = (Artillery)e.units[side.ordinal()];
				BattleCharacteristic selfCharacteristic = e.unitCharacteristics[side.ordinal()];
				selfCharacteristic.setAttack(self.getTowerAttack());
		
			}
		}
	},
	TOWER(ApplicationType.ENGAGEMENT, CharacteristicType.DAMAGE){
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			Unit enemy = e.units[side.ordinal()];
			BattleCharacteristic enemyCharacteristic = e.unitCharacteristics[side.ordinal()];
			if(enemy.getClass() != Artillery.class) {
				// non-artillery units cannot damage towers
				enemyCharacteristic.setAttack(Double.NEGATIVE_INFINITY);
			}
			// tower engagements are ranged, so they nullify shield defense
			enemyCharacteristic.setDefenseSkill(Double.NEGATIVE_INFINITY);
		}
	},
	SHIELD_CHARGE(ApplicationType.ENGAGEMENT, CharacteristicType.DAMAGE){
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			// TODO Every 4th charge? 
		}
	};
	
	private ApplicationType application;
	private CharacteristicType characteristic;
	
	
	
	public boolean isSupport(){
		switch(application) {
		case SUPPORT: return true;
		default: return false; 
		}
	};
	
	public boolean isMorale() {
		switch(characteristic) {
		case MORALE: return true;
		default: return false;
		}
	}
	
	public abstract void alterEngagement(EngagementData e, BattleSide side);
	private BattleModifier(ApplicationType application, CharacteristicType characteristic) {
		this.application = application;
		this.characteristic = characteristic;
		
	}
	enum ApplicationType {
		SUPPORT, ENGAGEMENT;
	}
	enum CharacteristicType{
		MORALE, DAMAGE;
	}
}
