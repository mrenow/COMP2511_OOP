package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An entry containing a currently queued unit or building
 * Is responsible for managing the remaining turns until complete. Once complete,
 * it calls onFinish() which will spawn a troop or create a building.
 * @author ezra
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY,
setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE)
public abstract class ItemSlotEntry {
	protected ItemType type;
	protected Province province;

	protected int turnsRemaining;
	
	@JsonCreator
	protected ItemSlotEntry(){}
	
	/**
	 * The type of item in progress
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
