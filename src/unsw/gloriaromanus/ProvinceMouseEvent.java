package unsw.gloriaromanus;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import unsw.engine.Faction;
import unsw.engine.Province;

public class ProvinceMouseEvent {
	private Province province;
	private MouseEvent source;
	public ProvinceMouseEvent(Province province, MouseEvent source) {
		super();
		this.province = province;
		this.source = source;
	}
	public Province getProvince() {
		return province;
	}
	public String getName() {
		return province.getName();
	}
	public Faction getOwner() {
		return province.getOwner();
	}
	public MouseEvent getSource() {
		return source;
	}
}
