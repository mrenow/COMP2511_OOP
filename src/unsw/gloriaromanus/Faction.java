package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Faction {
	public static final int STARTING_GOLD = 100;
	
	private FactionType type;
	private int gold;
	
	private List<Province> provinces;
	
	/**
	 * Load constructor
	 * @param type
	 * @param gold
	 * @param provinces
	 */
	@JsonCreator
	public Faction(
			@JsonProperty("type") 		FactionType type,
			@JsonProperty("gold") 		int gold,
			@JsonProperty("provinces") 	List<Province> provinces) {
		this.type = type;
		this.gold = gold;
		this.provinces = new ArrayList<Province>(provinces);
	}
	/**
	 * Start constructor
	 * @param type
	 */
	public Faction(FactionType type, Collection<Province> startingProvinces) {
		this.type = type;
		this.provinces = new ArrayList<Province>(startingProvinces);
	}

	@JsonGetter
	public FactionType getType() {return type;}

	@JsonGetter
	public List<Province> getProvinces(){return new ArrayList<Province>(provinces);}
	
	@JsonGetter
	public int getGold() {return gold;}
	
	public String getTitle() {return type.getTitle();}
	
	public int getTotalWealth() {return 0;}
	
	
	public Province getProvince(String name) {
		return null;
	}
	
}
