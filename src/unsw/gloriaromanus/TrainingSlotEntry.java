package unsw.gloriaromanus;

/**
 * An Item slot for training a troop Troop
 * @author ezra
 */
public class TrainingSlotEntry extends ItemSlotEntry{
	private String name;
	private double cost;


	public TrainingSlotEntry(ItemType type, int level) {
		super(type, level);
		//this.name = (String) getType().getAttribute("type", level);
		//this.cost = (Integer) getType().getAttribute("level", level);
	}

	@Override
	void onFinish() {
		// Spawn relevant troop in provinces
		province.addUnit(type);
		province.trainFinishUnit(type);
	}
}
