package org.tudresden.ecatering.model.stock;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

import org.javamoney.moneta.Money;
import static org.salespointframework.core.Currencies.*;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.quantity.Quantity;
public class StockManager {
	
	private IngredientRepository ingredients;
	
	public StockManager(IngredientRepository ingredients) {
		
		
		this.ingredients = ingredients;
	}
	
	public Iterable<Ingredient> findAllIngredients() {
		
		Iterable<Ingredient> resultIngredient = this.ingredients.findAll();
		Iterator<Ingredient> iter = resultIngredient.iterator();
		
		while(iter.hasNext())
		{
			if(iter.next().getProduct().getPrice().equals(Money.of(0, EURO)))
				iter.remove();
		}
		
		return resultIngredient;
	}
	
	public Iterable<Ingredient> findIngredientsByName(String name) {
		
		Iterable<Ingredient> ingredientsResult = this.findAllIngredients();
		Iterator<Ingredient> iter = ingredientsResult.iterator();
		while(iter.hasNext())
		{
			if(!iter.next().getProduct().getName().equals(name))
				iter.remove();
		}
		
		return ingredientsResult;
	}
	
	public Optional<Ingredient> findIngredientByIdentifier(InventoryItemIdentifier identifier) {
		return this.ingredients.findOne(identifier);
	}
	
	
	public Iterable<Ingredient> findExpiredIngredients() {
		
		Iterable<Ingredient> allIngredients = this.findAllIngredients();
		Iterator<Ingredient> iter = allIngredients.iterator();
		
		while(iter.hasNext())
		{
			LocalDate expirationDate = iter.next().getExpirationDate();
				if(expirationDate == null || expirationDate.isAfter(LocalDate.now()))
					{
					iter.remove();
					}
				
		}
		
		return allIngredients;
		
	}
	
	public static Ingredient createIngredient(String name,Money price,Quantity quantity,LocalDate expirationDate) {
		Ingredient ingredient = new Ingredient(name,price,quantity,expirationDate);
		return ingredient;
	}
	
	public static Ingredient createIngredient(String name,Money price,Quantity quantity) {
		Ingredient ingredient = new Ingredient(name,price,quantity);
		return ingredient;
	}
	
	public Ingredient saveIngredient(Ingredient ingredient) {
		
		
		this.ingredients.save(ingredient);
		return ingredient;
	}
	
	

}
