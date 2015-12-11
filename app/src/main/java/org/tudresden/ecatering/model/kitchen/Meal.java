package org.tudresden.ecatering.model.kitchen;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.javamoney.moneta.Money;
import static org.salespointframework.core.Currencies.*;




@Entity
public class Meal implements Serializable {

	
	
	@Id 
	@GeneratedValue
	@Column(name = "MEAL_ID", insertable = false, updatable = false)
	private long id;
	
	
	private static final long serialVersionUID = 8652543642342345647L;
	

	private String name;
	private MealType type;
	private double gainFactor;
	
	@OneToOne
	private Recipe recipe;

	
	


	
	@SuppressWarnings({ "unused" })
	private Meal() {	}
	
	protected Meal(double gainFactor,MealType type,Recipe recipe) {
		if(type==null)
			throw new IllegalArgumentException("MealType is null!");
		if(recipe==null)
			throw new IllegalArgumentException("Recipe is null!");
		this.setGainFactor(gainFactor);
		this.type = type;
		this.recipe = recipe;
		this.name = recipe.getName();

	}
	
	
	//getter
	
	public MealType getMealType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public double getGainFactor() {
		return gainFactor;
	}
	
	public long getID() {
		return id;
	}
	
	public Recipe getRecipe() {
		return recipe;
	}
	
	public List<Ingredient> getIngredientsForHelping(Helping helping) {
		List<Ingredient> ingredients = recipe.getIngredients();
		Iterator<Ingredient> iter = ingredients.iterator();
		
		while(iter.hasNext()) 
		{
			Ingredient ingredient = iter.next();
			ingredient.setQuantity(ingredient.getQuantity()*helping.getHelpingFactor());
		}
		
		return ingredients;
		
	}
	
	public Money getIngredientsPriceForHelping(Helping helping) {
		
		double price = 0;
		Iterator<Ingredient> ingredients = recipe.getIngredients().iterator();
		while(ingredients.hasNext())
		{
			Ingredient ingredient = ingredients.next();
			price = price + ingredient.getQuantity()*ingredient.getGrocery().getPrice().getNumber().doubleValue();
		}
		
		return Money.of(price*helping.getHelpingFactor(), EURO);
	}
	
	public Money getMealPriceForHelping(Helping helping) {
		double price = this.getIngredientsPriceForHelping(helping).getNumber().doubleValue();
		
		
		return Money.of(price*gainFactor, EURO);
	}
	
	//setter
	
	
	public void setGainFactor(double gainFactor) {
		if(gainFactor<1)
			throw new IllegalArgumentException("GainFactor must be greater or equal 1!");
		
		this.gainFactor = gainFactor;
		
	}
	
	
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(11, 39).
	       append(super.hashCode()).
	       append(type).
	       append(recipe).
	       append(gainFactor).
	       append(id).
	       append(name).
	       toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		   return ToStringBuilder.reflectionToString(this);
	}	
	
}
