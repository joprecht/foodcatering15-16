package org.tudresden.ecatering.model.accountancy;

public enum Discount {
	
	NONE(1.0d), CHILDCARE(0.93d);
	
	private double discountFactor;
	   
	   private Discount(double discountFactor) {
		   this.discountFactor = discountFactor;
	   }

	  public double getDiscountFactor() {
		  
		  return discountFactor;
	  }

}
