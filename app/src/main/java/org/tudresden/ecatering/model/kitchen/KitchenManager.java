package org.tudresden.ecatering.model.kitchen;

import java.util.List;
import java.util.Optional;

import org.javamoney.moneta.Money;
import static org.salespointframework.core.Currencies.*;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.quantity.Quantity;
import org.tudresden.ecatering.model.stock.Ingredient;
import org.tudresden.ecatering.model.stock.StockManager;

public class KitchenManager {
	
	private MealRepository mealRepo;
	private RecipeRepository recipeRepo;
	private MenuRepository menuRepo;

	
public KitchenManager(MealRepository meals, RecipeRepository recipes, MenuRepository menus) {
		
		this.mealRepo = meals;
		this.recipeRepo = recipes;
		this.menuRepo = menus;
}

public Iterable<Meal> findAllMeals() {
	
	return this.mealRepo.findAll();
}

public Iterable<Meal> findMealsByName(String name) {
	
	return this.mealRepo.findByName(name);
}

public Iterable<Meal> findMealsByMealType(MealType type) {
	
	
	return this.mealRepo.findByType(type);
}

public Optional<Meal> findMealByIdentifier(ProductIdentifier identifier) {
	
	return this.mealRepo.findOne(identifier);
}

public Iterable<Recipe> findAllRecipes() {
	
	return this.recipeRepo.findAll();
}

public Optional<Recipe> findRecipeByMealIdentifier(ProductIdentifier mealID) {
	
	return this.recipeRepo.findByMealID(mealID);
	
}

public Iterable<Menu> findAllMenus() { 
	
	return this.menuRepo.findAll();
}

public Optional<Menu> findMenuOfCalendarWeek(int calendarWeek) {
	
	return this.menuRepo.findByCalendarWeek(calendarWeek);
}

public static Ingredient createIngredient(String name,Quantity quantity) {
	
	return StockManager.createIngredient(name, Money.of(0, EURO), quantity);
}

public static Recipe createRecipe(String description,List<Ingredient> ingredients, ProductIdentifier mealID) {

	Recipe recipe = new Recipe(description, ingredients, mealID);
	return recipe;
}



public static Meal createMeal(String name, Money price, MealType type ) {
	
	Meal meal = new Meal(name,price,type);
	
	return meal;	
}

public static DailyMenu createDailyMenu(Day day, List<Meal> dailyMeals)
{
	DailyMenu dailyMenu = new DailyMenu(day, dailyMeals);
	
	return dailyMenu;
}

public static Menu createMenu(int calendarWeek, List<DailyMenu> dailyMenus)
{
	
	Menu menu = new Menu(calendarWeek, dailyMenus);
	return menu;
}

public Menu saveMenu(Menu menu) {
	
	if(this.findMenuOfCalendarWeek(menu.getCalendarWeek()).isPresent())
		throw new IllegalArgumentException("Menu for this calendarWeek already exists!");
	
	return this.menuRepo.save(menu);
}

public Meal saveMeal(Meal meal) {
	
	return this.mealRepo.save(meal);
}

public Recipe saveRecipe(Recipe recipe) {
	
	Optional<Recipe> recipeExist = this.findRecipeByMealIdentifier(recipe.getMealID());
	Optional<Meal> mealExist = this.findMealByIdentifier(recipe.getMealID());
	
	if(!mealExist.isPresent())
	throw new IllegalArgumentException ( "Meal for recipe doesnt exist!" ) ;
	
	if(recipeExist.isPresent())
		throw new IllegalArgumentException ( "Recipe for this mealID already exists!" ) ;
	
	return this.recipeRepo.save(recipe);
}


}
