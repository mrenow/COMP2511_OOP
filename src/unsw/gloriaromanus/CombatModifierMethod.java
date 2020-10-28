package unsw.gloriaromanus;

import static unsw.gloriaromanus.BattleSide.*;
import static unsw.gloriaromanus.ActiveType.*;
import java.util.List;
import java.util.Random;
import java.lang.Math;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;



enum ActiveType {
	SUPPORT, ENGAGEMENT;
}


/**
 * Identifies a battle modifying strategy
 * Probably going to change this a lot yikes
 * @param e,
 * @return
 */
public enum CombatModifierMethod {

	WALLS(SUPPORT) {
		@Override
		public void alterEngagement(CombatData e, BattleSide side) {
			// Check if both are ranged
			// Check if both are meelee
			
			// Otherwise 
			
		}
	},
	TRY_RANGED(SUPPORT){
			@Override
			public void alterEngagement(CombatData e, BattleSide side) {
				if(!e.getUnit(side).isRanged()) {
					e.setAttack(side, Double.NEGATIVE_INFINITY);	
				}			
				e.setDefenseSkill(side, Double.NEGATIVE_INFINITY);
				
			}
	},

	// Guarantee: Unit is Ranged
	FIRE_ARROWS_DAMAGE(ENGAGEMENT){
		@Override
		public void alterEngagement(CombatData e, BattleSide side) {
			e.multAttack(side, 0.9);
		}
	},
	// Guarantee: Unit is artillery
	ARTILLERY(ENGAGEMENT){
		@Override
		public void alterEngagement(CombatData e, BattleSide side) {
			Unit enemy = e.getUnit(side.other());			
			if(enemy.getClass() == Tower.class) {
				Artillery self = (Artillery)e.getUnit(side);
				e.setAttack(side, self.getTowerAttack());
		
			}
		}
	},
	TOWER(ENGAGEMENT){
		@Override
		public void alterEngagement(CombatData e, BattleSide side) {
			Unit enemy = e.getUnit(side);
			if(enemy.getClass() != Artillery.class) {
				// non-artillery units cannot damage towers
				e.setAttack(side.other(), Double.NEGATIVE_INFINITY);
			}
			// tower engagements are ranged, so they nullify shield defense
			e.setDefenseSkill(side.other(), Double.NEGATIVE_INFINITY);
		}
	},
	SHIELD_CHARGE(ENGAGEMENT){
		@Override
		public void alterEngagement(CombatData e, BattleSide side) {
			// TODO Every 4th charge? 
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
	
	
	public abstract void alterEngagement(CombatData e, BattleSide side);
	
}
