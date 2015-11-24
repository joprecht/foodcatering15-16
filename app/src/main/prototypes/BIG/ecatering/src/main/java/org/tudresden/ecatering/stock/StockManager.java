package org.tudresden.ecatering.stock;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;

import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.quantity.Quantity;

public class StockManager {
	
	private IngredientRepository ingredients;
	
	public StockManager(IngredientRepository ingredients) {
		
		this.ingredients = ingredients;
	}
	
	public Iterable<Ingredient> findAllIngredients() {
		
		return this.ingredients.findAll();
	}
	
	public Optional<Ingredient> findIngredientByProductIdentifier(ProductIdentifier identifier) {
		
		return this.ingredients.findByProductIdentifier(identifier);
	}
	
	public Optional<Ingredient> findIngredientByProduct(Product product) {
		
		return this.ingredients.findByProduct(product);
	}
	
	
	public Iterable<Ingredient> findExpiredIngredients() {
		
		Iterable<Ingredient> allIngredients = this.findAllIngredients();
		Iterator<Ingredient> iter = allIngredients.iterator();
		
		while(iter.hasNext())
		{
			if(iter.next().getExpirationDate().isAfter(LocalDateTime.now()))
			{
				iter.remove();
			}
		}
		
		return allIngredients;
		
	}
	
	public Ingredient createIngredient(Product product,Quantity quantity,LocalDateTime expirationDate) {
		Ingredient ingredient = new Ingredient(product,quantity,expirationDate);
		return ingredient;
	}
	
	public Ingredient saveIngredient(Ingredient ingredient) {

		Iterable<Ingredient> allIngredients = this.findAllIngredients();
		Iterator<Ingredient> iter = allIngredients.iterator();
		
		while(iter.hasNext())
		{
			if(iter.next().getProduct().getName().equals(ingredient.getProduct().getName()))
					{
						System.out.println("Gleiche items im system:"+iter.next().getProduct().getName()+" und "+ingredient.getProduct().getName());
						return ingredient;
					}
		}
		
		
		this.ingredients.save(ingredient);
		return ingredient;
	}
	
	

}
