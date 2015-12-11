package org.tudresden.ecatering.model.kitchen;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.salespointframework.catalog.ProductIdentifier;
import org.tudresden.ecatering.model.stock.Ingredient;




@Entity
public class Recipe implements Serializable {
	
	private static final long serialVersionUID = -3132500271571779420L;


	@Id 
	@GeneratedValue 
	@Column(name = "RECIPE_ID", insertable = false, updatable = false)
	private long id;


	private String description;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Ingredient> ingredients = new ArrayList<Ingredient>();
	
		
	private ProductIdentifier mealID;
	
	@SuppressWarnings("unused")
	private Recipe() {}	
	
	
	protected Recipe(String description, List<Ingredient> ingredients,ProductIdentifier mealID) {
		this.description = description;
		this.mealID = mealID;
		this.ingredients = ingredients;
		
		this.checkIngredients();
				
		
	}
	
	
	
	private void checkIngredients() {
		
		//check for multiple ingredients
				for(int i=0; i<this.ingredients.size(); i++)
				{
					for(int j=i+1; j<this.ingredients.size(); j++)
					{
						if(this.ingredients.get(i).getProduct().getName().equals(this.ingredients.get(j).getProduct().getName()))
							throw new IllegalArgumentException ( "Recipe has multiple ingredient!" ) ;
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
	

	public long getID() {
		return this.id;
	}
	

//setter
	public void setDescription(String description) {
		
		this.description = description;
		
	}
	
	public void setIngredients(List<Ingredient> ingredients) {
		
		this.ingredients = ingredients;
		this.checkIngredients();
	}


	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(17, 37).
	       append(super.hashCode()).
	       append(description).
	       append(mealID).
	       append(ingredients).
	       append(id).
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
