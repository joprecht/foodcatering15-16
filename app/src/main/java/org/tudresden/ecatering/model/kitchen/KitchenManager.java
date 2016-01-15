package org.tudresden.ecatering.model.kitchen;


import static org.salespointframework.core.Currencies.EURO;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudresden.ecatering.model.ReportGenerator;
import org.tudresden.ecatering.model.stock.Grocery;
import org.tudresden.ecatering.model.stock.StockItem;
import org.tudresden.ecatering.model.stock.StockManager;

@Component
public class KitchenManager {
	
@Autowired	private MealRepository mealRepo;
@Autowired	private RecipeRepository recipeRepo;
@Autowired	private MenuRepository menuRepo;
@Autowired private IngredientRepository ingredientRepo;
@Autowired private StockManager stockManager;
@Autowired private ReportGenerator<KitchenReport> reportGenerator;
	
public KitchenManager() {
	
/*	assertNotNull("MealRepo is null",mealRepo);
	assertNotNull("RecipeRepo is null",recipeRepo);
	assertNotNull("MenuRepo is null",menuRepo);
	assertNotNull("StockManager is null",stockManager);
*/

}

public Iterable<Meal> findAllMeals() {
	
	return this.mealRepo.findAll();
}

public Optional<Meal> findMealByName(String name) {
	
	return this.mealRepo.findByName(name);
}

public Optional<Meal> findMealByRecipe(Recipe recipe) {
	
	
	return this.mealRepo.findMealByRecipe(recipe);
}




public Iterable<Meal> findMealsByMealType(MealType type) {
	
	
	return this.mealRepo.findByType(type);
}

public Optional<Meal> findMealByID(Long id) {
	if(id==null)
		throw new IllegalArgumentException("id is null");
	
	if(this.mealRepo.findOne(id)==null)
		return Optional.empty();
	
	return Optional.of(this.mealRepo.findOne(id));
}



public Iterable<Recipe> findAllRecipes() {
	
	return this.recipeRepo.findAll();
}

public Optional<Recipe> findRecipeByName(String name) {
	
	return this.recipeRepo.findByName(name);
	
}



public Iterable<Recipe> findUsedRecipes() {
	
	Iterator<Meal> meals = this.findAllMeals().iterator();
	List<Recipe> usedRecipes = new ArrayList<Recipe>();
	while(meals.hasNext())
	{
		usedRecipes.add(meals.next().getRecipe());
	}
	
	return usedRecipes;
}

public Iterable<Recipe> findUnusedRecipes() {
	
	
	Iterable<Recipe> unusedRecipes = this.recipeRepo.findAll();
	Iterator<Recipe> iter = unusedRecipes.iterator();
	while(iter.hasNext()) {
		if(this.findMealByRecipe(iter.next()).isPresent())
			iter.remove();
	}
	
	return unusedRecipes;
}

public Optional<Recipe> findRecipeByID(Long identifier) {
	if(identifier == null)
		throw new IllegalArgumentException("identifier is null");
	
	if(recipeRepo.findOne(identifier)==null)
		return Optional.empty();
	
	return Optional.of(recipeRepo.findOne(identifier));
}

public Iterable<Menu> findAllMenus() { 
	
	
	Iterable<Menu> menus = this.menuRepo.findAll();
	
	List<Menu> sortedMenus = new ArrayList<Menu>();

	//sort Menus by Date
	for(Menu menu : menus)
	{
		sortedMenus.add(menu);
	}
	
	Collections.sort(sortedMenus, new Comparator<Menu>(){
		public int compare(Menu item1, Menu item2)
		{
			
			return (item1.getCalendarWeek() > item2.getCalendarWeek()) ? 1 : (item1.getCalendarWeek() < item2.getCalendarWeek()) ? -1 : 0;
			
		}		
	});
	
	return sortedMenus;
}

public Iterable<Menu> findMenusOfCalendarWeek(int calendarWeek) {
	
	
	return this.menuRepo.findByCalendarWeek(calendarWeek);
}

public Iterable<Menu> findMenusByDate(LocalDate date) {
	
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));	
	calendar.setMinimalDaysInFirstWeek(4);
	calendar.setFirstDayOfWeek(Calendar.MONDAY);

	return this.findMenusOfCalendarWeek(calendar.get(Calendar.WEEK_OF_YEAR));
}


public Money getIngredientsPriceForRecipeWithHelping(Recipe recipe, Helping helping) {
	if(recipe == null || helping == null)
		throw new IllegalArgumentException("recipe or helping null");
	
	double price = 0;
	Iterator<Ingredient> ingredients = recipe.getIngredients().iterator();
	while(ingredients.hasNext())
	{
		Ingredient ingredient = ingredients.next();
		price = BigDecimal.valueOf(price).add(BigDecimal.valueOf(ingredient.getQuantity()).multiply(BigDecimal.valueOf(ingredient.getGrocery().getPrice().getNumber().doubleValue()))).doubleValue();
	}
	
	
	return Money.of(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(helping.getHelpingFactor())).setScale(2, BigDecimal.ROUND_UP), EURO);
}

public Money getMealPriceForMealWithHelping(Meal meal, Helping helping) {
	if(meal == null || helping == null)
		throw new IllegalArgumentException("meal or helping null");
	
	return Money.of(this.getIngredientsPriceForRecipeWithHelping(meal.getRecipe(), helping).getNumberStripped().multiply(BigDecimal.valueOf(meal.getGainFactor())).setScale(2, BigDecimal.ROUND_UP), EURO);
}

public KitchenReport getKitchenReportForDate(LocalDate date) {
	return reportGenerator.generateReport(date);
}

public Ingredient createIngredient(Grocery grocery,double quantity) {
	
	if(!stockManager.findGroceryByID(grocery.getID()).isPresent())
		throw new IllegalArgumentException("Grocery does not exist!");
	return new Ingredient(grocery, quantity);
}

public Recipe createRecipe(String name,String description,List<Ingredient> ingredients) {

	if(name==null || name.trim().isEmpty())
		throw new IllegalArgumentException ( "Name is null or empty!" ) ;

	if(description==null || description.trim().isEmpty())
		throw new IllegalArgumentException ( "Description is null or empty!" ) ;
	
	if(ingredients.isEmpty())
		throw new IllegalArgumentException ( "No ingredients in Recipe!" ) ;

	
Optional<Recipe> similarRecipe = this.findRecipeByName(name);
	
	if(similarRecipe.isPresent())
			throw new IllegalArgumentException ( "Another Recipe with this name already exists!" ) ;
	
	return new Recipe(name,description, ingredients);
}



public Meal createMeal(Recipe recipe, MealType type, double gainFactor) {
	
	if(recipe==null)
		throw new IllegalArgumentException("Recipe is null!");
	
	Recipe recipeForMeal = this.recipeRepo.findOne(recipe.getID());
	if(recipeForMeal==null)
		throw new IllegalArgumentException("Recipe is not in repo!");
	

	if(this.findMealByRecipe(recipe).isPresent())
		throw new IllegalArgumentException("There is already a meal for this recipe!");
	

	
	return new Meal(gainFactor,type,recipe);	
}

public MenuItem createMenuItem(Meal meal, Helping helping, Day day) {
	if(meal==null)
		throw new IllegalArgumentException("meal is null!");
	
	
	if(!this.findMealByID(meal.getID()).isPresent())
		throw new IllegalArgumentException("meal is not in repo!");

	return new MenuItem(meal,this.getMealPriceForMealWithHelping(meal, helping),helping,day);

}

public DailyMenu createDailyMenu(List<MenuItem> dailyMeals)
{
	
	
	DailyMenu menu = new DailyMenu(dailyMeals);
	
	
	return menu;
}

public Menu createMenu(int calendarWeek, List<DailyMenu> dailyMenus)
{
	Menu menu = new Menu(calendarWeek, dailyMenus);
	
	Iterable<Menu> menus = this.findMenusOfCalendarWeek(calendarWeek);
	Iterator<Menu> iter = menus.iterator();
	while(iter.hasNext())
	{
		if(iter.next().getHelping().equals(menu.getHelping()))
			throw new IllegalArgumentException("Menu with same Helping for this calendarWeek already exists!");
	}
	
	
	return menu;
}

public Menu saveMenu(Menu menu) {
	
	
	return this.menuRepo.save(menu);
}

public Meal saveMeal(Meal meal) {

			
	return this.mealRepo.save(meal);
}


public Recipe saveRecipe(Recipe recipe) {
	
	this.ingredientRepo.save(recipe.getIngredients());
	
	return this.recipeRepo.save(recipe);
}


}
