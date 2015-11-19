package org.tudresden.ecatering.kitchen;

import javax.persistence.Entity;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

@Entity
public class Meal extends Product {

	
	private static final long serialVersionUID = 3652543642342345647L;
	
	public static enum MealType {
		REGULAR, DIET, SPECIAL;
	}

	private MealType type;
	private Helping helping;
	
	@SuppressWarnings({ "unused", "deprecation" })
	private Meal() {}
	
	public Meal(String name, Money price,MealType type) {
		
		super(name,price);
		this.type = type;
		this.helping = Helping.REGULAR;
		
	}
	
	//getter
	public MealType getMealType() {
		return this.type;
	}
	
	public Money getPrice() {
		return super.getPrice().multiply(this.helping.getHelpingFactor());
		
	}
	
	public Helping getHelping() {
		return this.helping;
	}
	
	//setter
	public void setHelping(Helping helping) {
		this.helping = helping;
	}
}
