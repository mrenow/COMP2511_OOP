package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An entry containing a currently queued unit or building
 * Is responsible for managing the remaining turns until complete. Once complete,
 * it calls onFinish() which will spawn a troop or create a building.
 * @author ezra
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public abstract class ItemSlotEntry {
	protected ItemType type;
	protected Province province;

	protected int level;
	protected int turnsRemaining;
	protected String name;
	protected double cost = 1;
	
	@JsonCreator
	protected ItemSlotEntry() {}

	public ItemSlotEntry(ItemType type, int level, Province p) {
		this.name = type.getName(level);
		this.cost = type.getCost(level);
		this.province = p;
		this.turnsRemaining = type.getDuration(level);
	}

	/**
	 * The type of item in progress
	 * 
	 * @return
	 */
	public ItemType getType() {
		return type;
	}
	
	/**
	 * @return The province that this item belongs to 
	 */
	public Province getProvince() {
		return province;
	}

	/**
	 * @return The number of turns until this item is complete
	 */
	public int getTurnsRemaining() {
		return turnsRemaining;
	}
	
	/**
	 * Reduces the number of turns remaining, and triggers event if remaining turns is zero.
	 */
	void update() {

		turnsRemaining--;
		if(turnsRemaining == 0) {
			onFinish();
		}
	}
	/**
	 * The action which is to be done when this item is complete.
	 * This could be spawning the troop in the province, or adding a new bulding.
	 */
	abstract void onFinish();


}
