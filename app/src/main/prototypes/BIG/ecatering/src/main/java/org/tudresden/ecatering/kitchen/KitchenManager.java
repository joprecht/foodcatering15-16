package org.tudresden.ecatering.kitchen;

import java.util.Iterator;

import org.javamoney.moneta.Money;
import org.tudresden.ecatering.kitchen.Meal.MealType;

public class KitchenManager {
	
	private MealRepository meals;
	
public KitchenManager(MealRepository meals) {
		
		this.meals = meals;

}

public Iterable<Meal> findAllMeals() {
	
	return this.meals.findAll();
}

public Iterable<Meal> findMealsByName(String name) {
	
	return this.meals.findByName(name);
}

public Iterable<Meal> findMealsByMealType(MealType type) {
	
	Iterable<Meal> allMeals = this.findAllMeals();
	Iterator<Meal> iter = allMeals.iterator();
	
	while(iter.hasNext())
	{
		if(!iter.next().getMealType().equals(type))
			iter.remove();
	}
	
	return allMeals;
}

public Meal createMeal(String name, Money price, MealType type ) {
	
	Meal meal = new Meal(name,price,type);
	
	return meal;	
}

public Meal saveMeal(Meal meal) {
	
	return this.meals.save(meal);
}


}
