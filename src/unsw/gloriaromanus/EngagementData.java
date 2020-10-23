package unsw.gloriaromanus;

import java.util.List;
/**
 * An intermediate state belonging to the battle system. Not serialized.
 * 
 * @author ezra
 */
public class EngagementData {
	public List<Unit>[] armies;
	public BattleCharacteristic[] unitCharacteristics;
}
