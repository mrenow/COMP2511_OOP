package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An Item slot for training a troop Troop
 * @author ezra
 */
public class TrainingSlotEntry extends ItemSlotEntry{

	@JsonCreator
	private TrainingSlotEntry() {}

	public TrainingSlotEntry(ItemType type, int level, Province p) {
		super();
		this.name = type.getName(level);
		this.cost = type.getCost(level);
		this.province = p;	
		this.turnsRemaining = type.getDuration(level);
	}

	@Override
	void onFinish() {
		// Spawn relevant troop in provinces
		province.addUnit(type);
		province.trainFinishUnit(this);
	}
}
