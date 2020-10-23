package unsw.gloriaromanus;

import java.util.List;

public class Faction {
	private FactionType type;
	
	public FactionType getType() {return type;}
	public String getTitle() {return type.getTitle();}
	
	public int getTreasury() {return 0;}
	
	public int getTotalWealth() {return 0;}
	
	public List<Province> getProvinces(){return null;}
	
	public Province getProvince(String name) {
		return null;
	}
	
}
