package ecatering.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.javamoney.moneta.Money;
import static org.salespointframework.core.Currencies.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.kitchen.DailyMenu;
import org.tudresden.ecatering.model.kitchen.Day;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.Ingredient;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.Meal;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.Menu;
import org.tudresden.ecatering.model.kitchen.MenuItem;
import org.tudresden.ecatering.model.kitchen.Recipe;
import org.tudresden.ecatering.model.stock.Grocery;
import org.tudresden.ecatering.model.stock.StockManager;

import ecatering.AbstractIntegrationTests;

public class KitchenClassesIntegrationTests extends AbstractIntegrationTests {
	
	@Autowired private KitchenManager kitchenManager;
	@Autowired private StockManager stockManager;

	
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
	public void ingredientTests() {
		
		assertNotNull("KitchenManager is null!", kitchenManager);
		assertNotNull("StockManager is null!", stockManager);

		Ingredient ingredient;
		
		//create Ingredient with an unknown grocery
		try {
		 ingredient = kitchenManager.createIngredient(stockManager.createGrocery("Kakao", Metric.KILOGRAM, Money.of(2, EURO)), 0.200);
	} catch(IllegalArgumentException e) {
		System.out.print(e+"\n");	
	}
		
		//create Ingredient with Zucker
		stockManager.saveGrocery(stockManager.createGrocery("Zucker", Metric.KILOGRAM,  Money.of(1.89, EURO)));
		 ingredient = kitchenManager.createIngredient(stockManager.findGroceryByName("Zucker").get(), 0.200);
	
			assertNotNull("Ingredient is null!", ingredient);
			assertEquals("grocery wrong or null","Zucker",ingredient.getGrocery().getName());
			assertEquals(0.200,ingredient.getQuantity(),0.1);

		 
	}
	
	@Test
	public void recipeTests() {
		
		assertNotNull("KitchenManager is null!", kitchenManager);
		assertNotNull("StockManager is null!", stockManager);
		
		//fill up stock with groceries
		stockManager.saveGrocery(stockManager.createGrocery("Spaghetti", Metric.KILOGRAM,  Money.of(1.89, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Rindfleisch", Metric.KILOGRAM,  Money.of(2.45, EURO)));
		Grocery milch = stockManager.saveGrocery(stockManager.createGrocery("Milch", Metric.LITER,  Money.of(1.50, EURO)));
		Grocery reis = stockManager.saveGrocery(stockManager.createGrocery("Reis", Metric.KILOGRAM,  Money.of(0.60, EURO)));
		
		assertThat(stockManager.findAllGroceries(), is(iterableWithSize(12)));

		//ingredients list
		
			Ingredient in1 = kitchenManager.createIngredient(stockManager.findGroceryByName("Spaghetti").get(), 0.150);
			Ingredient in2 = kitchenManager.createIngredient(stockManager.findGroceryByName("Rindfleisch").get(), 0.080);

		
			List<Ingredient> ingredients = new ArrayList<Ingredient>();
			ingredients.add(in1);
			ingredients.add(in2);
			

		//create and save recipe	
		kitchenManager.saveRecipe(kitchenManager.createRecipe("Spaghetti Bolognese", "Zubereitung...", ingredients));
		
		//try to create another recipe with already used name
		try {
			kitchenManager.saveRecipe(kitchenManager.createRecipe("Spaghetti Bolognese", "Zubereitung...", ingredients));

		} catch(IllegalArgumentException e) {
			System.out.print(e+"\n");	
		}
		
		//save another recipe
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(milch, 0.100));
		ingredients.add(kitchenManager.createIngredient(reis, 0.070));
		
		kitchenManager.saveRecipe(kitchenManager.createRecipe("Milchreis", "Zubereitung...", ingredients));
		
		assertThat(kitchenManager.findAllRecipes(), is(iterableWithSize(6)));
		assertTrue(kitchenManager.findRecipeByName("Milchreis").isPresent());
		assertThat(kitchenManager.findUsedRecipes(), is(iterableWithSize(4)));
		

		//find and test recipe
		Recipe testRecipe = kitchenManager.findRecipeByName("Milchreis").get();
		
		assertNotNull("testRecipe is null!", testRecipe);
		assertEquals("Milchreis",testRecipe.getName());
		assertEquals("Zubereitung...",testRecipe.getDescription());
		assertEquals(ingredients, testRecipe.getIngredients());
		assertEquals(1.50,testRecipe.getIngredients().get(0).getGrocery().getPrice().getNumber().doubleValue(),0.1);


		//check for updated prices
		
		Grocery grocery = stockManager.findGroceryByName("Milch").get();
		grocery.setPrice(Money.of(1.25,EURO));
		stockManager.saveGrocery(grocery);
		
		
		testRecipe = kitchenManager.findRecipeByName("Milchreis").get();
		assertEquals(1.25,testRecipe.getIngredients().get(0).getGrocery().getPrice().getNumber().doubleValue(),0.1);

		
	}
	
	
	@Test
	public void mealTests() {
		
		assertNotNull("KitchenManager is null!", kitchenManager);
		assertNotNull("StockManager is null!", stockManager);

		assertThat(kitchenManager.findAllMeals(), is(iterableWithSize(3)));
		
		//building a recipe
		Grocery kartoffelpuree = stockManager.saveGrocery(stockManager.createGrocery("Kartoffelpüree", Metric.KILOGRAM,  Money.of(1.50, EURO)));
		Grocery beefsteak = stockManager.saveGrocery(stockManager.createGrocery("Beefsteak", Metric.KILOGRAM,  Money.of(5.60, EURO)));
		
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(kartoffelpuree, 0.200));
		ingredients.add(kitchenManager.createIngredient(beefsteak, 0.080));

		Recipe notListedRecipe = kitchenManager.createRecipe("Nicht vorhanden", "...", ingredients);
		
		//try to create a meal with an unknown recipe
		try{
			kitchenManager.createMeal(notListedRecipe, MealType.REGULAR, 1.0);
		}
		catch(IllegalArgumentException e) {
			System.out.print(e+"\n");	
		}
		

		Recipe listedRecipe = kitchenManager.createRecipe("Kartoffelbrei und Beefsteak", "...", ingredients);
		kitchenManager.saveRecipe(listedRecipe);
		
		//try to create a meal of a known recipe but a wrong gain factor
		try{
			kitchenManager.createMeal(listedRecipe, MealType.REGULAR, 0.5);
		}
		catch(IllegalArgumentException e) {
			System.out.print(e+"\n");	
		}
		
		Meal testMeal = kitchenManager.createMeal(listedRecipe, MealType.REGULAR, 2);
		kitchenManager.saveMeal(testMeal);

		
		assertNotNull("Meal is null!", testMeal);
		assertEquals("Kartoffelbrei und Beefsteak",testMeal.getName());
		assertEquals(2.0,testMeal.getGainFactor(),0.1);
		assertEquals( MealType.REGULAR,testMeal.getMealType());
		assertEquals(0.748,kitchenManager.getIngredientsPriceForRecipeWithHelping(testMeal.getRecipe(), Helping.REGULAR).getNumber().doubleValue(),0.001);
		assertEquals(1.496,kitchenManager.getMealPriceForMealWithHelping(testMeal, Helping.REGULAR).getNumber().doubleValue(),0.001);
		
		


		//try to create a meal with a recipe already used for another meal
		try{
			kitchenManager.createMeal(kitchenManager.findRecipeByName("Kartoffelbrei und Beefsteak").get(), MealType.REGULAR, 0.5);
		}
		catch(IllegalArgumentException e) {
			System.out.print(e+"\n");	
		}
		
		//check for price calculation
		testMeal.setGainFactor(1);
		assertEquals(0.748,kitchenManager.getIngredientsPriceForRecipeWithHelping(testMeal.getRecipe(), Helping.REGULAR).getNumber().doubleValue(),0.001);
		assertEquals(0.748,kitchenManager.getMealPriceForMealWithHelping(testMeal, Helping.REGULAR).getNumber().doubleValue(),0.001);
		
		assertEquals(0.374,kitchenManager.getIngredientsPriceForRecipeWithHelping(testMeal.getRecipe(), Helping.SMALL).getNumber().doubleValue(),0.001);
		assertEquals(0.374,kitchenManager.getMealPriceForMealWithHelping(testMeal, Helping.SMALL).getNumber().doubleValue(),0.001);
		


	}
	
	

	@Test
	public void menuTests() {
		
		assertNotNull("KitchenManager is null!", kitchenManager);
		assertNotNull("StockManager is null!", stockManager);

		
		assertThat(kitchenManager.findAllMeals(), is(iterableWithSize(3)));
		assertThat(kitchenManager.findUsedRecipes(), is(iterableWithSize(3)));

				
		//building a Menu
		
				Meal meal1 = kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next();
				Meal meal2 = kitchenManager.findMealsByMealType(MealType.DIET).iterator().next();
				Meal meal3 = kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next();
				System.out.println(meal1.getName()+" Identifier:"+meal1.getID()+"\n");
				System.out.println(meal2.getName()+" Identifier:"+meal2.getID()+"\n");
				System.out.println(meal3.getName()+" Identifier:"+meal3.getID()+"\n");

		
				
				
				List<MenuItem> mondayMeals = new ArrayList<MenuItem>();
				mondayMeals.add(kitchenManager.createMenuItem(meal1,Helping.REGULAR,Day.MONDAY));
				mondayMeals.add(kitchenManager.createMenuItem(meal2,Helping.REGULAR,Day.MONDAY));
				mondayMeals.add(kitchenManager.createMenuItem(meal3,Helping.REGULAR,Day.MONDAY));
				
				List<MenuItem> tuesdayMeals = new ArrayList<MenuItem>();
				tuesdayMeals.add(kitchenManager.createMenuItem(meal1,Helping.REGULAR,Day.TUESDAY));
				tuesdayMeals.add(kitchenManager.createMenuItem(meal2,Helping.REGULAR,Day.TUESDAY));
				tuesdayMeals.add(kitchenManager.createMenuItem(meal3,Helping.REGULAR,Day.TUESDAY));
				
				List<MenuItem> wednesdayMeals = new ArrayList<MenuItem>();
				wednesdayMeals.add(kitchenManager.createMenuItem(meal1,Helping.REGULAR,Day.WEDNESDAY));
				wednesdayMeals.add(kitchenManager.createMenuItem(meal2,Helping.REGULAR,Day.WEDNESDAY));
				wednesdayMeals.add(kitchenManager.createMenuItem(meal3,Helping.REGULAR,Day.WEDNESDAY));
				
				List<MenuItem> thursdayMeals = new ArrayList<MenuItem>();
				thursdayMeals.add(kitchenManager.createMenuItem(meal1,Helping.REGULAR,Day.THURSDAY));
				thursdayMeals.add(kitchenManager.createMenuItem(meal2,Helping.REGULAR,Day.THURSDAY));
				thursdayMeals.add(kitchenManager.createMenuItem(meal3,Helping.REGULAR,Day.THURSDAY));
				
				List<MenuItem> fridayMeals = new ArrayList<MenuItem>();
				fridayMeals.add(kitchenManager.createMenuItem(meal1,Helping.REGULAR,Day.FRIDAY));
				fridayMeals.add(kitchenManager.createMenuItem(meal2,Helping.REGULAR,Day.FRIDAY));
				fridayMeals.add(kitchenManager.createMenuItem(meal3,Helping.REGULAR,Day.FRIDAY));

				
				List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
				dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

				
				
				Menu testMenu = kitchenManager.createMenu(51, dailyMenus);
				kitchenManager.saveMenu(testMenu);
				
				

				
			//find menu and check for meals	
				assertThat(kitchenManager.findMenusOfCalendarWeek(51), is(iterableWithSize(1)));
				
				testMenu = kitchenManager.findMenusOfCalendarWeek(51).iterator().next();
				
				
				assertEquals(3, testMenu.getDailyMenus().get(1).getDailyMeals().size());
				assertEquals("Schweinefleisch mit Nudeln",testMenu.getDailyMenus().get(1).getDailyMeals().get(0).getMeal().getName());
				assertEquals(0.455, testMenu.getDailyMenus().get(1).getDailyMeals().get(0).getPrice().getNumber().doubleValue(),0.001);

			
			//check for price persistence
				Meal changedMeal = kitchenManager.findMealByName("Schweinefleisch mit Nudeln").get();
				changedMeal.setGainFactor(3);
				kitchenManager.saveMeal(changedMeal);
				
				testMenu = kitchenManager.findMenusOfCalendarWeek(51).iterator().next();
				assertEquals(1.05, kitchenManager.getMealPriceForMealWithHelping(testMenu.getDailyMenus().get(1).getDailyMeals().get(0).getMeal(), Helping.REGULAR).getNumber().doubleValue(),0.001);
				assertEquals(0.455, testMenu.getDailyMenus().get(1).getDailyMeals().get(0).getPrice().getNumber().doubleValue(),0.001);
				

				
				
				
	}
	
	
}

