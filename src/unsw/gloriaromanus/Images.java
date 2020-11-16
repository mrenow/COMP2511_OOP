package unsw.gloriaromanus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Map;

import javafx.scene.image.Image;
import unsw.engine.ItemType;

public class Images {
	public static Image HEALTH_ICON;
	public static Image SHIELD_ICON;
	public static Image ATTACK_ICON;
	public static Image SKILL_ICON;
	public static Image ARMOUR_ICON;
	public static Image MORALE_ICON;
	public static Image SPEED_ICON;
	public static Image INVADE_ICON;
	public static Image MOVE_ICON;
	public static Image MOV_POINT_ICON;
	
	
	
	public static Map<ItemType, Image> ITEM_ICONS = new EnumMap<>(ItemType.class);
	public static String SPRITE_DIR = "images/CS2511Sprites_No_Background/";
	

	static {
		try {
			HEALTH_ICON = new Image(new FileInputStream(new File("images/UISprites/health_icon.png")),25,25,true,true);
			SHIELD_ICON = new Image(new FileInputStream(new File("images/UISprites/shield_icon.png")),25,25,true,true);
			ATTACK_ICON = new Image(new FileInputStream(new File("images/UISprites/attack_icon.png")),25,25,true,true);
			SKILL_ICON = new Image(new FileInputStream(new File("images/UISprites/skill_icon.png")),25,25,true,true);
			ARMOUR_ICON = new Image(new FileInputStream(new File("images/UISprites/armour_icon.png")),25,25,true,true);
			MORALE_ICON = new Image(new FileInputStream(new File("images/UISprites/morale_icon.png")),25,25,true,true);
			// iconfinder.com
			SPEED_ICON = new Image(new FileInputStream(new File("images/UISprites/speed_icon.png")),25,25,true,true);
			MOVE_ICON = new Image(new FileInputStream(new File("images/UISprites/move_icon.png")),50,50,true,true);
			MOV_POINT_ICON = new Image(new FileInputStream(new File("images/UISprites/move_icon.png")),25,25,true,true);
			INVADE_ICON = new Image(new FileInputStream(new File("images/UISprites/attack_icon.png")),50,50,true,true);
			
			for (ItemType type : ItemType.values()) {
				String location = type.getAttributeOrNull("image", 1);
				if(location == null) {
					continue;
				}
				ITEM_ICONS.put(type, new Image(new FileInputStream(new File(SPRITE_DIR + location)),25,25,true,true));
			}
			
		}catch(FileNotFoundException e){
			System.err.print(e.getMessage());
			e.printStackTrace();
		}
	}
}
