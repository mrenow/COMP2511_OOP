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

	public TrainingSlotEntry(ItemType type, int level, Province p) {
		super();
		this.name = type.getName(level);
		this.cost = type.getCost(level);
		this.province = p;
	}

	@Override
	void onFinish() {
		// Spawn relevant troop in provinces
		province.addUnit(type);
		province.trainFinishUnit(type);
	}
}
