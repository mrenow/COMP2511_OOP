package unsw.gloriaromanus;

/**
 * Enity which contains the information for infrastructure currently in progress
 * @author ezra
 */
public class BuildingSlotEntry extends ItemSlotEntry{

	@Override
	void onFinish() {
		// Should construct and add the new building to the province's building list.
		province.addBuilding(type);
	}

}
