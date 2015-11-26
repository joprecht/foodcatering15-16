package org.tudresden.ecatering.kitchen;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.salespointframework.catalog.ProductIdentifier;
import org.tudresden.ecatering.stock.Ingredient;

import org.springframework.util.Assert;

import org.salespointframework.core.AbstractEntity;
import org.salespointframework.core.SalespointIdentifier;

@Entity
public class Recipe {

	@Id @GeneratedValue private long id;
	private static final long serialVersionUID = 360216464547732101L;

	private String description;

	@OneToMany(fetch = FetchType.EAGER) private List<Ingredient> ingredients;

	@SuppressWarnings("unused")
	private Recipe() {}

	public Recipe(String description, List<Ingredient> ingredients) {

		this.description = description;
		this.ingredients = ingredients;

		this.checkIngredients();

	}

	private void checkIngredients() {

		// check for multiple ingredients
		for (int i = 0; i < this.ingredients.size(); i++) {
			for (int j = i + 1; j < this.ingredients.size(); j++) {
				Assert.isTrue(!this.ingredients.get(i).getProduct().equals(this.ingredients.get(j).getProduct()),
						"identical ingredients found");
			}
		}
	}

	// getter
	public long getID() {
		return this.id;
	}
	
	public String getDescription() {

		return this.description;
	}

	public List<Ingredient> getIngredients() {

		return this.ingredients;
	}

	// setter
	public void setDescription(String description) {

		this.description = description;

	}

	public void setIngredients(List<Ingredient> ingredients) {

		this.ingredients = ingredients;
		this.checkIngredients();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recipe other = (Recipe) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Recipe [id=" + id + ", description=" + description + ", ingredients=" + ingredients + "]";
	}

}
