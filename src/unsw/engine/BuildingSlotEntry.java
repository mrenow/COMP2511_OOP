package unsw.engine;

/**
 * Enity which contains the information for infrastructure currently in progress
 * @author ezra
 */
public class BuildingSlotEntry extends ItemSlotEntry{
	private String name;
	private double cost;
	
	public BuildingSlotEntry(ItemType type, int level) {
		super();
		this.name = (String) getType().getAttribute("type", level);
		this.cost = (Integer) getType().getAttribute("level", level);
	}

	@Override
	void onFinish() {
		// Should construct and add the new building to the province's building list.
		province.addBuilding(type);
		province.buildFinishInfrastructure(type);
	}

}
