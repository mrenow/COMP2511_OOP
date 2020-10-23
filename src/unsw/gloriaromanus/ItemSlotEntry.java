package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
public class ItemSlotEntry {
	private ItemType type;
	private Province province;
	private int remaining;
	

}
