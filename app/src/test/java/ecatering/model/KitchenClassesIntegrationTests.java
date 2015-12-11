package ecatering.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.javamoney.moneta.Money;
import static org.salespointframework.core.Currencies.*;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.kitchen.DailyMenu;
import org.tudresden.ecatering.model.kitchen.Day;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.Meal;
import org.tudresden.ecatering.model.kitchen.MealRepository;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.Menu;
import org.tudresden.ecatering.model.kitchen.MenuRepository;
import org.tudresden.ecatering.model.kitchen.Recipe;
import org.tudresden.ecatering.model.kitchen.RecipeRepository;
import org.tudresden.ecatering.model.stock.Ingredient;

import ecatering.AbstractIntegrationTests;

public class KitchenClassesIntegrationTests extends AbstractIntegrationTests {
	
	@Autowired MealRepository mealRepo;
	@Autowired RecipeRepository recipeRepo;
	@Autowired MenuRepository menuRepo;
	

	@Test
	public void helpingTests() {

		Helping testHelping = Helping.REGULAR;
		
		
		
		assertNotNull("Helping is null", testHelping);
		
		testHelping = Helping.REGULAR;	
		assertEquals(1.0, testHelping.getHelpingFactor(),0.1);
		
		testHelping = Helping.SMALL;
		assertEquals(0.5, testHelping.getHelpingFactor(),0.1);

	}
	
	@Test
	public void mealTests() {
		
		Meal testMeal = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		
		
		assertNotNull("Meal is null",testMeal);
		assertEquals("Meal Name wrong or null","Spaghetti",testMeal.getName());
		assertEquals("Money wrong or null",Money.of(4.50, EURO), testMeal.getPrice());
		assertEquals("MealType wrong or null",MealType.REGULAR, testMeal.getMealType());
		assertEquals("Helping wrong or null",Helping.REGULAR,testMeal.getHelping());
		
		testMeal.setHelping(Helping.SMALL);
		
	}
	
	@Test
	public void ingredientTests() {
		
		
		Ingredient testIngredient = KitchenManager.createIngredient("Quark", Quantity.of(0.500, Metric.KILOGRAM));
		
		assertNotNull("Ingredient is null",testIngredient);
		assertEquals("Name wrong or null","Quark",testIngredient.getProduct().getName());
		assertEquals("Quantity wrong or null",Quantity.of(0.500, Metric.KILOGRAM),testIngredient.getQuantity());
		assertEquals("Money wrong or null",Money.of(0, EURO),testIngredient.getProduct().getPrice());
		assertEquals("Expiration date is not null",null,testIngredient.getExpirationDate());
		
		
	}
	
	@Test
	public void recipeTests() {
		
		
		Meal meal1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		
		Quantity q1 = Quantity.of(0.500, Metric.KILOGRAM);
		Quantity q2 = Quantity.of(3.000, Metric.KILOGRAM);
		
		Ingredient in1 = KitchenManager.createIngredient("Quark",q1);
		Ingredient in2 = KitchenManager.createIngredient("Wurst",q2);
		
		List<Ingredient> inList = new ArrayList<Ingredient>();
		inList.add(in1);
		inList.add(in2);
		
		Recipe testRecipe = KitchenManager.createRecipe("Mit dem Loeffel ruehren", inList, meal1.getIdentifier());
		
		System.out.println("RecipeIdentifier for testRecipe:"+testRecipe.getID());

		
		assertNotNull("Recipe is null",testRecipe);
		assertEquals("Description wrong or null","Mit dem Loeffel ruehren",testRecipe.getDescription());
		assertEquals("Ingredients wrong or null",inList,testRecipe.getIngredients());
		assertEquals("Meal ID wrong or null",meal1.getIdentifier(),testRecipe.getMealID());
		
		testRecipe.setDescription("Mit dem Hammer schlagen");
		assertEquals("Description wrong or null","Mit dem Hammer schlagen",testRecipe.getDescription());
		
		inList = testRecipe.getIngredients();
		inList.add(KitchenManager.createIngredient("Kaese",q2));
		testRecipe.setIngredients(inList);
		assertEquals("List has different size",3,testRecipe.getIngredients().size());


		
	}
	
	@Test
	public void dailyMenuTests() {
		
		Meal meal1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		Meal meal2 = KitchenManager.createMeal("Feldsalat ohne allem", Money.of(2.50, EURO),MealType.DIET);
		Meal meal3 = KitchenManager.createMeal("Spaghetti vegan", Money.of(3.50, EURO),MealType.SPECIAL);

		
		List<Meal> meals = new ArrayList<Meal>();
		meals.add(meal3);
		meals.add(meal1);
		meals.add(meal2);
		
		DailyMenu testDailyMenu = KitchenManager.createDailyMenu(Day.MONDAY,meals);
		
		assertNotNull("testDailyMenu is null",testDailyMenu);
		assertEquals("wrong or null day ",Day.MONDAY,testDailyMenu.getDay());
		assertEquals("wrong or null mealList",meals,testDailyMenu.getDailyMeals());
		
	}
	
	@Test
	public void menuTests() {
		
		Meal meal1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		Meal meal2 = KitchenManager.createMeal("Feldsalat ohne allem", Money.of(2.50, EURO),MealType.DIET);
		Meal meal3 = KitchenManager.createMeal("Spaghetti vegan", Money.of(3.50, EURO),MealType.SPECIAL);

		
		List<Meal> meals = new ArrayList<Meal>();
		meals.add(meal3);
		meals.add(meal1);
		meals.add(meal2);
		
		DailyMenu dailyMenu1 = KitchenManager.createDailyMenu(Day.MONDAY,meals);
		DailyMenu dailyMenu2 = KitchenManager.createDailyMenu(Day.TUESDAY,meals);
		DailyMenu dailyMenu3 = KitchenManager.createDailyMenu(Day.WEDNESDAY,meals);
		DailyMenu dailyMenu4 = KitchenManager.createDailyMenu(Day.THURSDAY,meals);
		DailyMenu dailyMenu5 = KitchenManager.createDailyMenu(Day.FRIDAY,meals);

		
		List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
		dailyMenus.add(dailyMenu1);
		dailyMenus.add(dailyMenu2);
		dailyMenus.add(dailyMenu3);
		dailyMenus.add(dailyMenu4);
		dailyMenus.add(dailyMenu5);

		Menu testMenu = KitchenManager.createMenu(32,dailyMenus);
		
		assertNotNull("testMenu is null",testMenu);
		assertEquals("wrong or null calendarWeek",32,testMenu.getCalendarWeek());
		assertEquals("wrong or null dailyMenus",dailyMenus,testMenu.getDailyMenus());


		
	}
	
	@Test
	public void kitchenManagerTests() {
		
		assertNotNull("Meal Repo is null", mealRepo);
		assertNotNull("Recipe Repo is null", recipeRepo);

		KitchenManager manager = new KitchenManager(mealRepo, recipeRepo, menuRepo);	


		assertNotNull("KitchenManager is null", manager);
		
		//2 meals from data initializer
		assertThat(manager.findAllMeals(), is(iterableWithSize(2)));
		assertThat(manager.findMealsByMealType(MealType.REGULAR), is(iterableWithSize(1)));
		assertThat(manager.findMealsByName("Milchreis"), is(iterableWithSize(1)));




		assertThat(manager.findMealsByName("Spaghetti"), is(iterableWithSize(0)));
	
	//meal handling section
		Meal m1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);	
		manager.saveMeal(m1);	
		
				
		assertThat(manager.findMealsByName("Spaghetti"), is(iterableWithSize(1)));
		assertThat(manager.findMealsByMealType(MealType.REGULAR), is(iterableWithSize(2)));
		
		Meal m2 = KitchenManager.createMeal("Pizza", Money.of(6.50, EURO),MealType.REGULAR);	
		manager.saveMeal(m2);
		Optional<Meal> result = manager.findMealByIdentifier(m2.getIdentifier());
		
		assertTrue("meal doesnt exists", result.isPresent());
		assertEquals("Meal has not the same identifier",m2.getIdentifier(),result.get().getIdentifier());
		
		
	//ingredient handling section
		Quantity q1 = Quantity.of(0.120, Metric.KILOGRAM);		
		Quantity q2 = Quantity.of(0.025, Metric.LITER);
		
				
		Ingredient in1 = KitchenManager.createIngredient("Pizzateig", q1);
		Ingredient in2 = KitchenManager.createIngredient("Tomatensauce", q2);
		
		List<Ingredient> inList = new ArrayList<Ingredient>();
		inList.add(in1);
		inList.add(in2);
		
	//recipe handling section
		Recipe r1 = KitchenManager.createRecipe("Pizza machen", inList, m2.getIdentifier());
		manager.saveRecipe(r1);
		

		
		List<Ingredient> inList2 = new ArrayList<Ingredient>();
		inList2.add(KitchenManager.createIngredient("Nudeln", Quantity.of(0.130, Metric.KILOGRAM)));
		inList2.add(KitchenManager.createIngredient("Tomatensauce", Quantity.of(0.100, Metric.LITER)));
		
		Recipe r2 = KitchenManager.createRecipe("Nudeln kochen...", inList2, m1.getIdentifier());
		manager.saveRecipe(r2);

		
		//error throwing when meal isnt in repo (for testing, uncomment it)		
	/*	Meal fakeMeal = manager.createMeal("Gibts gar nicht", Money.of(4.50, EURO),MealType.REGULAR);
		Recipe r3 = manager.createRecipe("Pizza machen", inList, fakeMeal.getIdentifier());
		manager.saveRecipe(r3);
	*/	

		assertThat(manager.findAllRecipes(), is(iterableWithSize(3)));
		Iterator<Recipe> iter = manager.findAllRecipes().iterator();
		Recipe recipe = iter.next();
				recipe = iter.next();
				recipe = iter.next();

				
		assertEquals("description wrong","Nudeln kochen..." ,recipe.getDescription());
		assertTrue(!recipe.getIngredients().isEmpty());
		
		Ingredient ingredient = recipe.getIngredients().get(0);
		assertEquals("wrong ingredient","Nudeln",ingredient.getProduct().getName());
		
		
		System.out.println("RecipeIdentifier for Recipe:"+recipe.getID());
		
		assertTrue("Recipe for mealID not found",manager.findRecipeByMealIdentifier(m1.getIdentifier()).isPresent());
		
	//menu handling section
		
		Meal mealMonday1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		Meal mealMonday2 = KitchenManager.createMeal("Feldsalat ohne allem", Money.of(2.50, EURO),MealType.DIET);
		Meal mealMonday3 = KitchenManager.createMeal("Spaghetti vegan", Money.of(3.50, EURO),MealType.SPECIAL);
		
		Meal mealTuesday1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		Meal mealTuesday2 = KitchenManager.createMeal("Feldsalat ohne allem", Money.of(2.50, EURO),MealType.DIET);
		Meal mealTuesday3 = KitchenManager.createMeal("Spaghetti vegan", Money.of(3.50, EURO),MealType.SPECIAL);
		
		Meal mealWednesday1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		Meal mealWednesday2 = KitchenManager.createMeal("Feldsalat ohne allem", Money.of(2.50, EURO),MealType.DIET);
		Meal mealWednesday3 = KitchenManager.createMeal("Spaghetti vegan", Money.of(3.50, EURO),MealType.SPECIAL);
		
		Meal mealThursday1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		Meal mealThursday2 = KitchenManager.createMeal("Feldsalat ohne allem", Money.of(2.50, EURO),MealType.DIET);
		Meal mealThursday3 = KitchenManager.createMeal("Spaghetti vegan", Money.of(3.50, EURO),MealType.SPECIAL);
		
		Meal mealFriday1 = KitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);
		Meal mealFriday2 = KitchenManager.createMeal("Feldsalat ohne allem", Money.of(2.50, EURO),MealType.DIET);
		Meal mealFriday3 = KitchenManager.createMeal("Spaghetti vegan", Money.of(3.50, EURO),MealType.SPECIAL);

		
		List<Meal> mondayMeals = new ArrayList<Meal>();
		mondayMeals.add(mealMonday1);
		mondayMeals.add(mealMonday2);
		mondayMeals.add(mealMonday3);
		
		List<Meal> tuesdayMeals = new ArrayList<Meal>();
		tuesdayMeals.add(mealTuesday1);
		tuesdayMeals.add(mealTuesday2);
		tuesdayMeals.add(mealTuesday3);
		
		List<Meal> wednesdayMeals = new ArrayList<Meal>();
		wednesdayMeals.add(mealWednesday1);
		wednesdayMeals.add(mealWednesday2);
		wednesdayMeals.add(mealWednesday3);
		
		List<Meal> thursdayMeals = new ArrayList<Meal>();
		thursdayMeals.add(mealThursday1);
		thursdayMeals.add(mealThursday2);
		thursdayMeals.add(mealThursday3);
		
		List<Meal> fridayMeals = new ArrayList<Meal>();
		fridayMeals.add(mealFriday1);
		fridayMeals.add(mealFriday2);
		fridayMeals.add(mealFriday3);
		
		
		
		DailyMenu dailyMenu1 = KitchenManager.createDailyMenu(Day.MONDAY,mondayMeals);
		DailyMenu dailyMenu2 = KitchenManager.createDailyMenu(Day.TUESDAY,tuesdayMeals);
		DailyMenu dailyMenu3 = KitchenManager.createDailyMenu(Day.WEDNESDAY,wednesdayMeals);
		DailyMenu dailyMenu4 = KitchenManager.createDailyMenu(Day.THURSDAY,thursdayMeals);
		DailyMenu dailyMenu5 = KitchenManager.createDailyMenu(Day.FRIDAY,fridayMeals);

		
		List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>(); 
		dailyMenus.add(dailyMenu1);
		dailyMenus.add(dailyMenu2);
		dailyMenus.add(dailyMenu3);
		dailyMenus.add(dailyMenu4);
		dailyMenus.add(dailyMenu5);

	
		
		Menu testMenu = KitchenManager.createMenu(53, dailyMenus);
		
		assertThat(manager.findAllMenus(), is(iterableWithSize(0)));
		
		manager.saveMenu(testMenu);
		
		assertThat(manager.findAllMenus(), is(iterableWithSize(1)));
		
		Optional<Menu> validMenu = manager.findMenuOfCalendarWeek(53);
		
		assertTrue("menu not found", validMenu.isPresent());
		
	}

}

