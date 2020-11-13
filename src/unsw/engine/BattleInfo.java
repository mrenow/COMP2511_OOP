package unsw.engine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static unsw.engine.BattleSide.*;

/**
 * 
 * @author ezra
 */
public class BattleInfo {
	private BattleResult result;
	private List<SkirmishInfo> skirmishes = new ArrayList<SkirmishInfo>();
	private Map<BattleSide, List<Unit>> casualties = new EnumMap<>(BattleSide.class);
	public BattleInfo() {
		casualties.put(ATTACK, new ArrayList<>());
		casualties.put(DEFEND, new ArrayList<>());
	}
	public void addSkirmish(Unit ua, SkirmishResult ra, Unit ud, SkirmishResult rd) {
		skirmishes.add(new SkirmishInfo(ua, ra, ud, rd));
	}
	public void addSkirmish(Unit ua, Unit ud) {
		skirmishes.add(new SkirmishInfo(ua, ua.isAlive()? SkirmishResult.WON : SkirmishResult.KILLED,
										ud, ud.isAlive()? SkirmishResult.WON : SkirmishResult.KILLED));
	}
	
	public void addCasualties(BattleSide side, Unit u) {
		casualties.get(side).add(u);
	}
	public void setResult(BattleResult result) {
		this.result = result;
	}
	
	public BattleResult getResult() {
		return result;
	}
	public List<Unit> getCasualties(BattleSide side){
		return casualties.get(side);
	}
	public List<SkirmishInfo> getSkirmishes(){
		return new ArrayList<>(skirmishes);
	}
}
