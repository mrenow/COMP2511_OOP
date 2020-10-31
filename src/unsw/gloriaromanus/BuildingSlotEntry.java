package unsw.gloriaromanus;

/**
 * Enity which contains the information for infrastructure currently in progress
 * @author ezra
 */
public class BuildingSlotEntry extends ItemSlotEntry{

	public BuildingSlotEntry(ItemType type, int level) {
		super(type, level);
	}

	@Override
	void onFinish() {
		// Should construct and add the new building to the province's building list.
		province.addBuilding(type);
		province.buildFinishInfrastructure(type);
	}

}
