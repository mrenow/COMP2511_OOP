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
		public EngagementData alterEngagement(EngagementData e) {
			// TODO Every 4th charge? 
			return e;
		}
	};
	
	public abstract EngagementData alterEngagement(EngagementData e);
}
