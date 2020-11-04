package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;

import util.DeserializableEntry;
import util.MappingIterable;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "type")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class Faction {
	public static final int STARTING_GOLD = 100;
	// does not "own" provinces, so does not need a properly initialized province
	// list
	public static final Faction NO_ONE = new Faction(FactionType.NO_ONE);

	private FactionType type = FactionType.ROME;
	private int gold = 0;

	@JsonIdentityReference(alwaysAsId = true)
	private Collection<Province> provinces = new ArrayList<Province>();


	private VictoryInfo vicInfo = new VictoryInfo();
	
	// Non-string key Maps arent serializable
	@JsonIgnore
	private Map<Province, Integer> lostEagles = new HashMap<>();

	@JsonCreator
	private Faction() {
	}

	Faction(FactionType type, Collection<Province> provinces, int gold) {
		this.type = type;
		this.gold = gold;
		this.provinces.addAll(provinces);
		provinces.forEach((p) -> p.loadOwner(this));

	}

	/**
	 * Start constructor
	 * 
	 * @param type
	 */
	public Faction(FactionType type) {
		this.type = type;
	}

	public FactionType getType() {
		return type;
	}

	public Collection<Province> getProvinces() {
		return provinces;
	}

	public int getNumProvinces() {
		return provinces.size();
	}

	public int getGold() {
		return gold;
	}

	void adjustGold(int cost) {
		gold -= cost;
	}

	public String getTitle() {
		return type.getTitle();
	}
	public String asPlural() {
		return type.getPlural();
	}
	public String asAdjective() {
		return type.getAdjective();
	}

	public int getTotalWealth() {
		// calculation for total wealth
		int wealth = 0;
		for (Province province : provinces) {
			wealth += province.getTotalWealth();
		}
		return wealth;
	}

	public void update() {
		for (Province province : provinces) {
			this.gold += province.updateWealth();
			province.update();
		}
	}

	public Province getProvince(String name) {
		for (Province province : provinces) {
			if (province.getName().equals(name)) {
				return province;
			}
		}
		return null;
	}

	public Collection<Province> getLostEagleProvinces() {
		return lostEagles.keySet();
	}

	// get sum of values in map.
	public int getNumLostEagles() {
		return lostEagles.values().stream().reduce(0, Integer::sum);
	}

	void takeProvince(Province p) {
		p.getOwner().removeProvince(p);
		p.changeOwner(this);
		redeemProvince(p);
		provinces.add(p);
	}

	// Used for loading only
	void loadProvince(Province p) {
		p.loadOwner(this);
		provinces.add(p);
	}

	private void removeProvince(Province p) {
		provinces.remove(p);
	}

	@Override
	public String toString() {
		return getTitle();
	}

	public VictoryInfo getVictoryInfo() {
		return vicInfo;
	}

	void putLostEagles(Province lostProvince, int numEagles) {
		int oldNumEagles = lostEagles.getOrDefault(lostProvince, 0);
		lostEagles.put(lostProvince, oldNumEagles + numEagles);
	}

	private void redeemProvince(Province takenProvince) {
		lostEagles.remove(takenProvince);
	}

	@JsonSetter("lostEagles")
	private void loadLostEagles(List<DeserializableEntry<Province, Integer>> entrySet) {
		entrySet.forEach((entry) -> lostEagles.put(entry.getKey(), entry.getValue()));
	}

	@JsonGetter("lostEagles")
	private List<DeserializableEntry<Province, Integer>> saveLostEagles() {
		return lostEagles.entrySet().stream().map(DeserializableEntry::new).collect(Collectors.toList());
	}

	/*
	 * @JsonGetter private List<Integer> saveLostEagles() { List<Province> keys =
	 * getSortedLostEagleKeys(); List<Integer> out = new ArrayList<>(keys.size());
	 * for (Province key : keys) { out.add(lostEagles.get(key)); } return out; }
	 */
}
