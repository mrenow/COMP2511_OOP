package unsw.engine;

import static unsw.engine.BattleSide.ATTACK;
import static unsw.engine.BattleSide.DEFEND;

import java.util.EnumMap;
import java.util.Map;

public class SkirmishInfo{
	private Map<BattleSide, SkirmishResult> results = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, Unit> units = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, Integer> prevHealths = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, Integer> nextHealths = new EnumMap<>(BattleSide.class);
	
	SkirmishInfo(Unit att, Unit def){
		units.put(ATTACK, att);
		units.put(DEFEND, def);
		prevHealths.put(ATTACK, att.getHealth());
		prevHealths.put(DEFEND, def.getHealth());
		results.put(ATTACK, SkirmishResult.HELD);
		results.put(DEFEND, SkirmishResult.HELD);
	}
	
	public Unit getUnit(BattleSide side) {
		return units.get(side);
	}
	
	public SkirmishResult getResult(BattleSide side) {
		return results.get(side);
	}
	
	public int getPrevHealth(BattleSide side) {
		return 	prevHealths.get(side);
	}
	public int getNextHealth(BattleSide side) {
		return nextHealths.get(side);
	}
	
	public void putResult(BattleSide side, SkirmishResult result) {
		results.put(side, result);
	}
	
	public void finish() {
		nextHealths.put(ATTACK, units.get(ATTACK).getHealth());
		nextHealths.put(DEFEND, units.get(DEFEND).getHealth());
	}
}