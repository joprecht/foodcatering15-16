package org.tudresden.ecatering.kitchen;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

@Entity
public class Meal extends Product {

	
	private static final long serialVersionUID = 8652543642342345647L;
	

	private MealType type;
	private Helping helping;
	@OneToOne private Recipe recipe;
	
	@SuppressWarnings({ "unused", "deprecation" })
	private Meal() {}
	
	public Meal(String name, Money price,MealType type, Recipe recipe) {
		
		super(name,price);
		this.type = type;
		this.helping = Helping.REGULAR;
		this.recipe = recipe;
		
	}
	
	//getter
	public MealType getMealType() {
		return this.type;
	}
	
	public Money getPrice() {
		return super.getPrice().multiply(this.helping.getHelpingFactor());
		
	}
	
	public Recipe getRecipe() {
		return this.recipe;
	}
	
	public Helping getHelping() {
		return this.helping;
	}
	
	//setter
	public void setHelping(Helping helping) {
		this.helping = helping;
	}
}
