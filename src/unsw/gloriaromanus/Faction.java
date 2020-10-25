package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.ANY)
public class Faction {
	public static final int STARTING_GOLD = 100;
	public static final Faction NO_ONE = new Faction(FactionType.NO_ONE);
	
	private FactionType type = FactionType.ROME;
	private int gold = 0;
	@JsonIdentityReference(alwaysAsId = true)
	private List<Province> provinces = new ArrayList<Province>();
	
	@JsonCreator
	private Faction() {};
	
	public Faction(	FactionType type,int gold,	List<Province> provinces) {
		this.type = type;
		this.gold = gold;
		this.provinces.addAll(provinces);
	}
	/**
	 * Start constructor
	 * @param type
	 */
	public Faction(FactionType type) {
		this.type = type;
	}

	public FactionType getType() {return type;}

	public List<Province> getProvinces(){return new ArrayList<Province>(provinces);}
	
	public int getGold() {return gold;}
	
	public String getTitle() {return type.getTitle();}
	
	public int getTotalWealth() {return 0;}
	
	
	public Province getProvince(String name) {
		return null;
	}
	
	void takeProvince(Province p) {
		p.getOwner().removeProvince(p);
		p.changeOwner(this);
		provinces.add(p);
	}
	
	private void removeProvince(Province p) {
		provinces.remove(p);
	}
	@Override
	public String toString() {
		return getTitle();
	}
}
