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




@Entity
public class Recipe implements Serializable {
	
	private static final long serialVersionUID = -3132500271571779420L;


	@Id 
	@GeneratedValue 
	@Column(name = "RECIPE_ID", insertable = false, updatable = false)
	private long id;


	private String description;
	private String name;
	
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.DETACH)
	private List<Ingredient> ingredients = new ArrayList<Ingredient>();
	
		
	
	@SuppressWarnings("unused")
	private Recipe() {}	
	
	
	protected Recipe(String name, String description, List<Ingredient> ingredients) {
		
		if(name==null || name.trim().equals(""))
			throw new IllegalArgumentException("Name is null or empty!");
		if(description==null || description.trim().equals(""))
			throw new IllegalArgumentException("Description is null or empty!");
		if(ingredients==null || ingredients.isEmpty())
			throw new IllegalArgumentException("Ingredients is null or empty!");

		
		this.name = name;
		this.description = description;
		this.ingredients = ingredients;
		this.checkIngredients();
				
		
	}
	
	
	
	private void checkIngredients() {
		
		//check for multiple ingredients
				for(int i=0; i<this.ingredients.size(); i++)
				{
					for(int j=i+1; j<this.ingredients.size(); j++)
					{
						if(this.ingredients.get(i).getGrocery().getName().equals(this.ingredients.get(j).getGrocery().getName()))
							throw new IllegalArgumentException ( "Recipe has multiple ingredient!" ) ;
						}
				}
	}
	
//getter	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		
		return description;
	}
	

	public List<Ingredient> getIngredients() {
		
		return ingredients;
	}
	
	

	public long getID() {
		return id;
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
	       append(name).
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
