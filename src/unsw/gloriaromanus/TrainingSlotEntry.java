package unsw.gloriaromanus;

/**
 * An Item slot for training a troop Troop
 * @author ezra
 */
public class TrainingSlotEntry extends ItemSlotEntry{

	public TrainingSlotEntry(ItemType type, int level) {}

	@Override
	void onFinish() {
		// TODO Spawn relevant troop in provinces
		province.addUnit(type);
	}
}
