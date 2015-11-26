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
import java.util.stream.StreamSupport;

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
	@Autowired KitchenManager manager;

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
	public void createMealTest() {
		
		Meal testMeal = new Meal("Spaghetti", Money.of(4.50, EURO),MealType.REGULAR,null);
		
		testMeal = mealRepo.save(testMeal);
		
		assertNotNull("Meal is null",testMeal);
		assertNotNull("Meal identifier is null", testMeal.getIdentifier());
		
		assertEquals("Meal Name wrong or null","Spaghetti",testMeal.getName());
		assertEquals("Money wrong or null",Money.of(4.50, EURO), testMeal.getPrice());
		assertEquals("MealType wrong or null",MealType.REGULAR, testMeal.getMealType());
		assertEquals("Helping wrong or null",Helping.REGULAR,testMeal.getHelping());
		
		testMeal.setHelping(Helping.SMALL);
		assertEquals("Helping wrong or null",Helping.SMALL,testMeal.getHelping());
		
		
	}
	
	@Test
	public void createIngredientTest() {
		
		Product p1 = new Product("Quark",Money.of(0.79, EURO));
		Quantity q1 = Quantity.of(1);
		LocalDateTime expirationDate = LocalDateTime.of(2015, 12, 24, 0, 0);
		
		Ingredient testIngredient = new Ingredient(p1,q1, expirationDate);
		
		testIngredient = inventoryRepo.save(testIngredient);
		
		assertNotNull("Ingredient is null",testIngredient);
		assertNotNull("Ingredient identifier is null", testIngredient.getIdentifier());
		
		assertEquals("Product wrong or null",p1,testIngredient.getProduct());
		assertEquals("Quantity wrong or null",q1,testIngredient.getQuantity());
		assertEquals("Expiration date wrong or null",expirationDate,testIngredient.getExpirationDate());
	}
	
	@Test
	public void createRecipeTest() {
		
		Product p1 = new Product("Quark",Money.of(0.79, EURO));
		Product p2 = new Product("Wurst",Money.of(2.49, EURO));
		
		Quantity q1 = Quantity.of(1);
		LocalDateTime expDate1 = LocalDateTime.of(2015, 12, 24, 0, 0);
		
		Quantity q2 = Quantity.of(3);
		LocalDateTime expDate2 = LocalDateTime.of(2015, 11, 5, 0, 0);
		
		Ingredient in1 = new Ingredient(p1,q1, expDate1);
		Ingredient in2 = new Ingredient(p2,q2, expDate2);
		
		inventoryRepo.save(in1);
		inventoryRepo.save(in2);
		
		List<Ingredient> inList = new LinkedList<>();
		inList.add(in1);
		inList.add(in2);
		
		Recipe testRecipe = new Recipe("Mit dem Loeffel ruehren", inList);
		testRecipe = recipeRepo.save(testRecipe);
		
		assertNotNull("Recipe is null",testRecipe);
		assertNotNull("Recipe identifer is null", testRecipe.getID());
		
		assertEquals("Description wrong or null","Mit dem Loeffel ruehren",testRecipe.getDescription());
		assertEquals("Ingredients wrong or null",inList,testRecipe.getIngredients());
	}
	
	@Test
	public void kitchenManagerTests() {
		
		Meal m2 = manager.createMeal("Pizza", Money.of(6.50, EURO),MealType.REGULAR);	
		manager.saveMeal(m2);
		
		Optional<Meal> result = manager.findMealByIdentifier(m2.getIdentifier());
		assertTrue(result.isPresent());
		
		// one recipe from DataInitilizer
		assertThat(manager.findAllRecipes(), is(iterableWithSize(1)));
		
		Meal m3 = manager.createMeal("Tofu Speise", Money.of(3.50, EURO),MealType.SPECIAL);	
		manager.saveMeal(m3);
		
		Product p1 = new Product("Tofu",Money.of(3.79, EURO));
		Quantity q1 = Quantity.of(1);
		LocalDateTime expDate1 = LocalDateTime.of(2015, 12, 24, 0, 0);
		
		Product p2 = new Product("Salat",Money.of(1.49, EURO));
		Quantity q2 = Quantity.of(1);
		LocalDateTime expDate2 = LocalDateTime.of(2015, 11, 5, 0, 0);
		
		Ingredient in1 = new Ingredient(p1,q1, expDate1);
		Ingredient in2 = new Ingredient(p2,q2, expDate2);
		
		inventoryRepo.save(in1);
		inventoryRepo.save(in2);

		
		List<Ingredient> inList = new LinkedList<Ingredient>();
		inList.add(in1);
		inList.add(in2);
		
		
		Recipe r1 = manager.createRecipe("Kalt servieren...", inList);
		manager.saveRecipe(r1);
		
		// one recipe from DataInititilizer and one from above
		assertThat(manager.findAllRecipes(), is(iterableWithSize(2)));
		
		Optional<Recipe> optR1 = StreamSupport
		.stream(manager.findAllRecipes().spliterator(), true)
		.filter(recipe -> recipe.equals(r1))
		.findAny();
		
		assertTrue(optR1.isPresent());
		
		List<Ingredient> ingredients = optR1.get().getIngredients();
		assertTrue(ingredients.isEmpty() == false);
		
		assertEquals("Ingredients are false or null", 2, ingredients.size());
	}
	
	

}

