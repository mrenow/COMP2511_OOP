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

	
	
public abstract class BattleModifier {
	//
	public static final BattleModifier WALLS = 
			new BattleModifier(ApplicationType.SUPPORT, CharacteristicType.DAMAGE) {
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			// Check if both are ranged
			// Check if both are meelee
			
			// Otherwise 
			
		}
	};
	public static final BattleModifier TRY_RANGED = 
		new BattleModifier(ApplicationType.SUPPORT, CharacteristicType.DAMAGE){
			@Override
			public void alterEngagement(EngagementData e, BattleSide side) {
				// Check if both are ranged
				// Check if both are meelee
				
				// Otherwise 
				
		
			}
	};
	public static final BattleModifier DRUIDIC_FERVOUR =
		new BattleModifier(ApplicationType.SUPPORT, CharacteristicType.MORALE){
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
	};	
	public static final BattleModifier FIRE_ARROWS_MORALE =
		new BattleModifier(ApplicationType.ENGAGEMENT, CharacteristicType.MORALE){
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			e.unitCharacteristics[side.other().ordinal()].applyMoraleMult(0.8);
		}
	};
	public static final BattleModifier FIRE_ARROWS_DAMAGE =
		new BattleModifier(ApplicationType.ENGAGEMENT, CharacteristicType.DAMAGE){
		@Override
		public void alterEngagement(EngagementData e, BattleSide side) {
			BattleCharacteristic self = e.unitCharacteristics[side.ordinal()];
			self.applyAttackMult(0.9);
		}
	};
	public static final BattleModifier ARTILLERY =
		new BattleModifier(ApplicationType.ENGAGEMENT, CharacteristicType.DAMAGE){

		@Override
		// 
		public void alterEngagement(EngagementData e, BattleSide side) {
			Unit enemy = e.units[side.other().ordinal()];			
			if(enemy.getClass() == Tower.class) {
				Artillery self = (Artillery)e.units[side.ordinal()];
				BattleCharacteristic selfCharacteristic = e.unitCharacteristics[side.ordinal()];
				selfCharacteristic.setAttack(self.getTowerAttack());
		
			}
		}
	};
	public static final BattleModifier TOWER
	= new BattleModifier(ApplicationType.ENGAGEMENT, CharacteristicType.DAMAGE){
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
	};
	public static final BattleModifier SHIELD_CHARGE
	= new BattleModifier(ApplicationType.ENGAGEMENT, CharacteristicType.DAMAGE){
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

		
	}
	enum ApplicationType {
		SUPPORT, ENGAGEMENT;
	}
	enum CharacteristicType{
		MORALE, DAMAGE;
	}
}
