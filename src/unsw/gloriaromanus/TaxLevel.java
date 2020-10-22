package unsw.gloriaromanus;

public enum TaxLevel {
	LOW_TAX			(10, 10),
	MEDIUM_TAX		(0, 15),
	HIGH_TAX		(-10, 20),
	VERY_HIGH_TAX	(-30, 25); // Also -1 morale to units inside province with this taxrate;
	
	private double taxrate;
	private int wealthgen;
		
	private TaxLevel(int wealthgen, int taxpercentage) {
		this.wealthgen = wealthgen;
		this.taxrate = (1.0/100) * taxpercentage;
	}
	public double getTaxRate() {
		return taxrate;
	}
	public int getwealthgen() {
		return wealthgen;
	}

}
