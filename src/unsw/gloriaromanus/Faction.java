package unsw.gloriaromanus;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.*;

public class Faction {
	private FactionType type;
	private int gold;
	private List<Province> provinces;

	public FactionType getType() {return type;}
	
	@JsonGetter
	public String getTitle() {return type.getTitle();}
	
	public int getTreasury() {return 0;}
	
	public int getTotalWealth() {return 0;}
	
	public List<Province> getProvinces(){return null;}
	
	public Province getProvince(String name) {
		return null;
	}
	
}
