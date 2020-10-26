package unsw.gloriaromanus;

import java.util.List;
/**
 * An intermediate state belonging to the battle system. Not serialized.
 * Contains all the information needed to simulate an engagement.
 * 
 * @author ezra
 */
public class EngagementData {
	public List<Unit>[] armies;
	public Unit[] units; 
	public BattleCharacteristic[] unitCharacteristics;
}
