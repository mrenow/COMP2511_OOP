package unsw.gloriaromanus;

/**
 * An Item slot for training a troop Troop
 * @author ezra
 */
public class TrainingSlotEntry extends ItemSlotEntry{

	public TrainingSlotEntry(ItemType type, int level) {}

	@Override
	void onFinish() {
		// Spawn relevant troop in provinces
		province.addUnit(type);
		province.trainFinishUnit(type);
	}
}
