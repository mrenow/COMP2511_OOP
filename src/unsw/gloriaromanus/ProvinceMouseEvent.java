package unsw.gloriaromanus;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import unsw.engine.Faction;
import unsw.engine.Province;

public class ProvinceMouseEvent {
	private Province province;
	private MouseEvent source;
	private boolean canAttack;
	private boolean canMove;
	
	public ProvinceMouseEvent(Province province, MouseEvent source, boolean canMove, boolean canAttack) {
		super();
		this.province = province;
		this.source = source;
		this.canAttack = canAttack;
		this.canMove = canMove;
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
	public boolean canAttack() {
		return canAttack;
	}
	public boolean canMove() {
		return canMove;
	}
}
