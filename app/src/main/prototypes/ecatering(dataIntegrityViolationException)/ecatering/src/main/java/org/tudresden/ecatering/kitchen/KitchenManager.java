package org.tudresden.ecatering.kitchen;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.tudresden.ecatering.kitchen.MealType;
import org.tudresden.ecatering.stock.Ingredient;

@Service
public class KitchenManager {

	@Autowired private MealRepository meals;
	@Autowired private RecipeRepository recipes;

	@SuppressWarnings("unused")
	private KitchenManager() {};

	public Iterable<Meal> findAllMeals() {

		return this.meals.findAll();
	}

	public Iterable<Meal> findMealsByName(String name) {

		return this.meals.findByName(name);
	}

	public Iterable<Meal> findMealsByMealType(MealType type) {

		Iterable<Meal> allMeals = this.findAllMeals();
		Iterator<Meal> iter = allMeals.iterator();

		while (iter.hasNext()) {
			if (!iter.next().getMealType().equals(type))
				iter.remove();
		}

		return allMeals;
	}

	public Optional<Meal> findMealByIdentifier(ProductIdentifier identifier) {

		Iterable<Meal> allMeals = this.findAllMeals();
		Iterator<Meal> iter = allMeals.iterator();

		while (iter.hasNext()) {
			Meal meal = iter.next();

			if (meal.getIdentifier().equals(identifier)) {
				return Optional.of(meal);
			}
		}

		return Optional.empty();
	}

	public Iterable<Recipe> findAllRecipes() {

		return this.recipes.findAll();
	}

	public Recipe createRecipe(String description, List<Ingredient> ingredients) {

		Recipe recipe = new Recipe(description, ingredients);
		return recipe;
	}

	public Meal createMeal(String name, Money price, MealType type) {

		Meal meal = new Meal(name, price, type, null);

		return meal;
	}

	public Meal saveMeal(Meal meal) {

		return this.meals.save(meal);
	}

	public Recipe saveRecipe(Recipe recipe) {

		return this.recipes.save(recipe);
	}

}
