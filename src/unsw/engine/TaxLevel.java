package unsw.engine;

import util.ArrayUtil;

public enum TaxLevel {
	LOW_TAX			(10, 10),
	NORMAL_TAX		(0, 15),
	HIGH_TAX		(-10, 20),
	VERY_HIGH_TAX	(-30, 25); // Also -1 morale to units inside province with this taxrate;
	
	private String title;
	private double taxRate;
	private int taxPercentage;
	private int wealthGen;
		
	private TaxLevel(int wealthGen, int taxPercentage) {
		this.wealthGen = wealthGen;
		this.taxPercentage = taxPercentage;
		this.taxRate = (1.0/100) * taxPercentage;
		this.title = ArrayUtil.enumToTitle(this);
	}
	public double getTaxRate() {
		return taxRate;
	}
	public int getTaxPercentage() {
		return taxPercentage;
	}
	public int getWealthGen() {
		return wealthGen;
	}
	public String toString() {
		return title;
	}

}
