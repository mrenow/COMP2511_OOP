package unsw.gloriaromanus;

public enum TaxLevel {
	LOW_TAX			(10, 10),
	NORMAL_TAX		(0, 15),
	HIGH_TAX		(-10, 20),
	VERY_HIGH_TAX	(-30, 25); // Also -1 morale to units inside province with this taxrate;
	
	private double taxRate;
	private int wealthGen;
		
	private TaxLevel(int wealthgen, int taxpercentage) {
		this.wealthGen = wealthgen;
		this.taxRate = (1.0/100) * taxpercentage;
	}
	public double getTaxRate() {
		return taxRate;
	}
	public int getwealthgen() {
		return wealthGen;
	}

}
