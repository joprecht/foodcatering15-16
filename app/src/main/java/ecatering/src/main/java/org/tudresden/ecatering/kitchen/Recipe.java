package org.tudresden.ecatering.kitchen;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.salespointframework.catalog.ProductIdentifier;
import org.tudresden.ecatering.stock.Ingredient;

import org.springframework.util.Assert;

import org.salespointframework.core.AbstractEntity;
import org.salespointframework.core.SalespointIdentifier;




@Entity
public class Recipe extends AbstractEntity<SalespointIdentifier> {
	
	@Id
	@Column(name="RECIPE_ID")
	@GeneratedValue
	private int id;
	private static final long serialVersionUID = 360216464547732101L;

	
	private String description;
	
	@OneToMany(targetEntity=Ingredient.class, mappedBy="recipe") private List<Ingredient> ingredients;
		
	private ProductIdentifier mealID;
	
	private RecipeIdentifier recipeID;
	
	

	
	@SuppressWarnings("unused")
	private Recipe() {}	
	
	public Recipe(String description, List<Ingredient> ingredients,ProductIdentifier mealID) {
		super();
		
		this.description = description;
		this.mealID = mealID;
		this.ingredients = ingredients;
		this.recipeID = new RecipeIdentifier();
		
		this.checkIngredients();

	}
	
	
	
	private void checkIngredients() {
		
		//check for multiple ingredients
				for(int i=0; i<this.ingredients.size(); i++)
				{
					for(int j=i+1; j<this.ingredients.size(); j++)
					{
						Assert.isTrue(!this.ingredients.get(i).getProduct().equals(this.ingredients.get(j).getProduct()), "identical ingredients found");		
						}
				}
	}
	
//getter	
	public String getDescription() {
		
		return this.description;
	}
	
	public List<Ingredient> getIngredients() {
		
		return this.ingredients;
	}
	
	
	public ProductIdentifier getMealID() {
		
		return this.mealID;
	}
	
	 public RecipeIdentifier getIdentifier() {
		
		return this.recipeID;
	}
	
	

//setter
	public void setDescription(String description) {
		
		this.description = description;
		
	}
	
	public void setIngredients(List<Ingredient> ingredients) {
		
		this.ingredients = ingredients;
		this.checkIngredients();
	}
	
	
}
