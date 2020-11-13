package unsw.engine;

import static unsw.engine.BattleSide.ATTACK;
import static unsw.engine.BattleSide.DEFEND;

import java.util.EnumMap;
import java.util.Map;

public class SkirmishInfo{
	private Map<BattleSide, SkirmishResult> results = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, Unit> units = new EnumMap<>(BattleSide.class);
	
	SkirmishInfo(Unit ua, SkirmishResult ra, Unit ud, SkirmishResult rd) {
		units.put(ATTACK, ua);
		results.put(ATTACK, ra);
		units.put(DEFEND, ud);
		results.put(DEFEND, rd);
	}
	public Unit getUnit(BattleSide side) {
		return units.get(side);
	}
	
	public SkirmishResult getResult(BattleSide side) {
		return results.get(side);
	}
}