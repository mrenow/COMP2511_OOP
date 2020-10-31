package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An Item slot for training a troop Troop
 * @author ezra
 */
public class TrainingSlotEntry extends ItemSlotEntry{
	//private String name = "";
	//private double cost = 1;
	//private Province province;

	@JsonCreator
	private TrainingSlotEntry() {}
/*
	public TrainingSlotEntry(String name, double cost) {
		super(type, level);
		this.name = name;
		this.cost = cost;
	}
*/
	
	public TrainingSlotEntry(ItemType type, int level, Province p) {
		super();
		this.name = type.getName(level);
		this.cost = type.getCost(level);
		this.province = p;

		/*
		this.name = ((String)type.getAttribute("names", level));
		this.cost = ((Integer)type.getAttribute("costs", level)).doubleValue();
		*/
	}

	@Override
	void onFinish() {
		// Spawn relevant troop in provinces
		province.addUnit(type);
		province.trainFinishUnit(type);
	}
}
