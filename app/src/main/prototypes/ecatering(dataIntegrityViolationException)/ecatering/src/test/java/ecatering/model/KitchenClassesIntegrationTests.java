package ecatering.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.javamoney.moneta.Money;
import static org.salespointframework.core.Currencies.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.kitchen.Helping;
import org.tudresden.ecatering.kitchen.KitchenManager;
import org.tudresden.ecatering.kitchen.Meal;
import org.tudresden.ecatering.kitchen.MealRepository;
import org.tudresden.ecatering.kitchen.MealType;
import org.tudresden.ecatering.kitchen.Recipe;
import org.tudresden.ecatering.kitchen.RecipeRepository;
import org.tudresden.ecatering.stock.Ingredient;

import ecatering.AbstractIntegrationTests;

public class KitchenClassesIntegrationTests extends AbstractIntegrationTests {
	
	@Autowired MealRepository mealRepo;
	@Autowired RecipeRepository recipeRepo;
	@Autowired Inventory<Ingredient> inventoryRepo;
	@Autowired Catalog<Product> productRepo;

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
		
		Meal testMeal = new Meal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR,null);
		
		System.out.println("ProductIdentifier for testMeal:"+testMeal.getIdentifier());
		
		assertNotNull("Meal is null",testMeal);
		assertEquals("Meal Name wrong or null","Spaghetti",testMeal.getName());
		assertEquals("Money wrong or null",Money.of(4.50, EURO), testMeal.getPrice());
		assertEquals("MealType wrong or null",MealType.REGULAR, testMeal.getMealType());
		assertEquals("Helping wrong or null",Helping.REGULAR,testMeal.getHelping());
		
		testMeal.setHelping(Helping.SMALL);
		assertEquals("Helping wrong or null",Helping.SMALL,testMeal.getHelping());
		
		
	}
	
	@Test
	public void ingredientTests() {
		
		Product p1 = new Product("Quark",Money.of(0.79, EURO));
		Quantity q1 = Quantity.of(1);
		LocalDateTime expirationDate = LocalDateTime.of(2015, 12, 24, 0, 0);
		
		Ingredient testIngredient = new Ingredient(p1,q1, expirationDate);
		
		assertNotNull("Ingredient is null",testIngredient);
		assertEquals("Product wrong or null",p1,testIngredient.getProduct());
		assertEquals("Quantity wrong or null",q1,testIngredient.getQuantity());
		assertEquals("Expiration date wrong or null",expirationDate,testIngredient.getExpirationDate());

		
	}
	
	@Test
	public void recipeTests() {
		
		Product p1 = new Product("Quark",Money.of(0.79, EURO));
		Quantity q1 = Quantity.of(1);
		LocalDateTime expDate1 = LocalDateTime.of(2015, 12, 24, 0, 0);
		
		Product p2 = new Product("Wurst",Money.of(2.49, EURO));
		Quantity q2 = Quantity.of(3);
		LocalDateTime expDate2 = LocalDateTime.of(2015, 11, 5, 0, 0);
		
		Meal meal1 = new Meal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR,null);
		
		Ingredient in1 = new Ingredient(p1,q1, expDate1);
		Ingredient in2 = new Ingredient(p2,q2, expDate2);
		
		List<Ingredient> inList = new ArrayList<Ingredient>();
		inList.add(in1);
		inList.add(in2);
		
		Recipe testRecipe = new Recipe("Mit dem Loeffel ruehren", inList);
		
		System.out.println("RecipeIdentifier for testRecipe:");

		
		assertNotNull("Recipe is null",testRecipe);
		assertEquals("Description wrong or null","Mit dem Loeffel ruehren",testRecipe.getDescription());
		assertEquals("Ingredients wrong or null",inList,testRecipe.getIngredients());
	//	assertEquals("Meal ID wrong or null",meal1.getIdentifier(),testRecipe.getMealID());
		
		testRecipe.setDescription("Mit dem Hammer schlagen");
		assertEquals("Description wrong or null","Mit dem Hammer schlagen",testRecipe.getDescription());
		
		inList = testRecipe.getIngredients();
		inList.add(new Ingredient(new Product("Kaese",Money.of(1.49, EURO)),q2, expDate2));
		testRecipe.setIngredients(inList);
		assertEquals("List has different size",3,testRecipe.getIngredients().size());


		
	}
	
	@Test
	public void kitchenManagerTests() {
		
		assertNotNull("Meal Repo is null", mealRepo);
		assertNotNull("Recipe Repo is null", recipeRepo);


		KitchenManager manager = new KitchenManager(mealRepo, recipeRepo);
		
		assertThat(manager.findMealsByName("Spaghetti"), is(iterableWithSize(0)));
	
		Meal m1 = manager.createMeal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR);	
		manager.saveMeal(m1);
				
		assertThat(manager.findMealsByName("Spaghetti"), is(iterableWithSize(1)));
		assertThat(manager.findMealsByMealType(MealType.REGULAR), is(iterableWithSize(2)));
		
		Meal m2 = manager.createMeal("Pizza", Money.of(6.50, EURO),MealType.REGULAR);	
		manager.saveMeal(m2);
		Optional<Meal> result = manager.findMealByIdentifier(m2.getIdentifier());
		
		assertTrue("meal doesnt exists", result.isPresent());
		
		
		
		
		//error throwing when meal isnt in repo (for testing uncomment it)		
		//Meal fakeMeal = manager.createMeal("Gibts gar nicht", Money.of(4.50, EURO),MealType.REGULAR);
		//Recipe r2 = manager.createRecipe("Pizza machen", inList, fakeMeal.getIdentifier());
		

		assertThat(manager.findAllRecipes(), is(iterableWithSize(1)));
		
		
		Meal m3 = manager.createMeal("Tofu Speise", Money.of(3.50, EURO),MealType.SPECIAL);	
		manager.saveMeal(m3);
		
		Product p1 = new Product("Tofu",Money.of(3.79, EURO));
		Quantity q1 = Quantity.of(1);
		LocalDateTime expDate1 = LocalDateTime.of(2015, 12, 24, 0, 0);
		
		Product p2 = new Product("Salat",Money.of(1.49, EURO));
		Quantity q2 = Quantity.of(1);
		LocalDateTime expDate2 = LocalDateTime.of(2015, 11, 5, 0, 0);
		
		productRepo.save(p1);
		productRepo.save(p2);

		
				
		Ingredient in1 = new Ingredient(p1,q1, expDate1);
		Ingredient in2 = new Ingredient(p2,q2, expDate2);
		
		inventoryRepo.save(in1);
		inventoryRepo.save(in2);

		
		List<Ingredient> inList = new LinkedList<Ingredient>();
		inList.add(in1);
		inList.add(in2);
		
		
		Recipe r1 = manager.createRecipe("Kalt servieren...", inList);
		manager.saveRecipe(r1);
		
		assertThat(manager.findAllRecipes(), is(iterableWithSize(2)));
		Iterable<Recipe> tofuSpeise = manager.findAllRecipes();
		
		
		//List<Ingredient> ingredients = tofuSpeise.get().getIngredients();
		
		//assertTrue(!tofuSpeise.isEmpty());
		
		assertEquals("Ingredients are false or null", tofuSpeise.iterator().next());

		

	}
	
	

}

