package org.tudresden.ecatering.kitchen;

public enum Helping {
	
	REGULAR(1.0d), SMALL(0.5d);
	
	   private double helpingFactor;
	   
	   private Helping(double helpingFactor) {
		   this.helpingFactor = helpingFactor;
	   }

	  public double getHelpingFactor() {
		  
		  return helpingFactor;
	  }
}
