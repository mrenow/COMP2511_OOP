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
	private int skirmishIndex = 0;
	public BattleInfo() {
		casualties.put(ATTACK, new ArrayList<>());
		casualties.put(DEFEND, new ArrayList<>());
	}
	// inserts a skirmish before the currently active one
	public void beginSkirmish(Unit att, Unit def) {
		skirmishes.add(skirmishIndex, new SkirmishInfo(att, def));
	}
	
	public void setSkirmishResult(BattleSide side, SkirmishResult result) {
		skirmishes.get(skirmishIndex).putResult(side, result);
	}
	// move on to next skirmish
	public void finishSkirmish() {
		skirmishes.get(skirmishIndex).finish();
		skirmishIndex++;
	}
	
	public void discardSkirmish() {
		skirmishes.remove(skirmishIndex);
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
