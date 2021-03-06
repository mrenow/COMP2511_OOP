package unsw.engine;
/**
 * A Strategy used to modify combatStats before casualties are 
 * inflicted according to some ability or special rule.
 * Object is created during battle time. 
 */
class Modifier<T>{
	private ModifierMethod<T> method;
	private BattleSide side;
	
	Modifier(ModifierMethod<T> method, BattleSide side) {
		this.side = side;
		this.method = method;
	}
	
	public void modify(T data) {
		method.modify(data, side);
	}
	@Override
	public String toString() {
		return method.toString();
	}
}
