package unsw.gloriaromanus;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;

import unsw.engine.Faction;
import unsw.engine.Province;

public class ProvinceFeatureInfo implements Comparable<ProvinceFeatureInfo>{
	// Index into graphics id lists.
	private int id;

	private Province province;
	private Polygon shape;
	private Point centre;
	
	public ProvinceFeatureInfo(int id, Province p) {
		this.id = id;
		this.province = p;
	}
	
	public Faction getOwner() {
		return province.getOwner();
	}

	public String getName() {
		return province.getName();
	}
	
	public Polygon getShape() {
		return shape;
	}
	
	public void setShape(Polygon shape) {
		this.shape =  shape;
	}
	
	public Point getCentre() {
		return centre;
	}
	public void setCentre(Point centre) {
		this.centre = centre;
	}
	
	public int getId() {
		return id;
	}

	@Override
	public int compareTo(ProvinceFeatureInfo other) {
		return this.id - other.id;
	}	
}
