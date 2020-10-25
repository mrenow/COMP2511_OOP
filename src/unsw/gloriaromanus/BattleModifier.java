package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Identifies a battle modifying strategy
 * @param e
 * @return
 */
public enum BattleModifier {
	SHIELD_CHARGE{
		@Override
		public void alterEngagement(EngagementData e, boolean isAttacker) {
			// TODO Every 4th charge? 
		}
	};
	
	public abstract void alterEngagement(EngagementData e, boolean isAttacker);
}
