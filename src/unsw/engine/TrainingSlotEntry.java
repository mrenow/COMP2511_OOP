package unsw.engine;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An Item slot for training a troop Troop
 * @author ezra
 */
public class TrainingSlotEntry extends ItemSlotEntry{

	@JsonCreator
	private TrainingSlotEntry() {}

	public TrainingSlotEntry(ItemType type, int level, Province p) {
		super(type, level, p);
	}

	@Override
	void onFinish() {
		// Spawn relevant troop in provinces
		province.addUnit(type);
		province.trainAdjustUnit(this);
	}
}
