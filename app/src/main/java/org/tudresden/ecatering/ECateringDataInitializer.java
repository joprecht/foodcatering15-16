package org.tudresden.ecatering;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.Meal;
import org.tudresden.ecatering.model.kitchen.MealRepository;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.MenuRepository;
import org.tudresden.ecatering.model.kitchen.Recipe;
import org.tudresden.ecatering.model.kitchen.RecipeRepository;
import org.tudresden.ecatering.model.stock.Ingredient;
import org.tudresden.ecatering.model.stock.IngredientRepository;
import org.tudresden.ecatering.model.stock.StockManager;


@Component
public class ECateringDataInitializer implements DataInitializer {

	private final UserAccountManager userAccountManager;
	private final StockManager stockManager;
	private final KitchenManager kitchenManager;

	@Autowired
	public ECateringDataInitializer(IngredientRepository inventory, MealRepository mealRepo, RecipeRepository recipeRepo, MenuRepository menuRepo,
			UserAccountManager userAccountManager) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");


		this.userAccountManager = userAccountManager;
		this.stockManager = new StockManager(inventory);
		this.kitchenManager = new KitchenManager(mealRepo, recipeRepo, menuRepo);
	}
	
	@Override
	public void initialize() {

		initializeUsers();
		initializeInventory();
		initializeMealRepo();
		initializeRecipeRepo();

	}
	
private void initializeUsers() {

		
		if (userAccountManager.findByUsername("koch").isPresent()) {
			return;
		}

		UserAccount ua1 = userAccountManager.create("koch", "123", new Role("ROLE_KITCHEN"));
		userAccountManager.save(ua1);
		
		UserAccount ua2 = userAccountManager.create("lager", "123", new Role("ROLE_STOCK"));
		userAccountManager.save(ua2);

	}
	
	private void initializeInventory() {
		
		//Inventar wird gefuellt mit Zutaten bestehend aus Produkt, Menge und Haltbarkeit
		Quantity menge1 = Quantity.of(0.500, Metric.LITER);
		Quantity menge2 = Quantity.of(1.500, Metric.KILOGRAM);
		
		
		Ingredient in1 = StockManager.createIngredient("Tomatensauce",Money.of(0.79, EURO),menge1,LocalDate.of(2015, Month.DECEMBER, 24));
		stockManager.saveIngredient(in1);
		//zweimal Jagdwurst mit unterschiedlicher Haltbarkeit
		Ingredient in2 = StockManager.createIngredient("Jagdwurst",Money.of(1.45, EURO),menge2,LocalDate.of(2015, Month.DECEMBER, 13));
		stockManager.saveIngredient(in2);
		Ingredient in3 = StockManager.createIngredient("Jagdwurst",Money.of(1.45, EURO),menge2,LocalDate.of(2016, Month.JANUARY, 13));
		stockManager.saveIngredient(in3);
	}
	
	private void initializeMealRepo() {
		
		Meal m1 = KitchenManager.createMeal("Pizza", Money.of(4.50, EURO), MealType.REGULAR);
		Meal m2 = KitchenManager.createMeal("Milchreis", Money.of(2.50, EURO), MealType.DIET);
		
		kitchenManager.saveMeal(m1);
		kitchenManager.saveMeal(m2);

	}
	
	private void initializeRecipeRepo() {
		
				
		
		Quantity q1 = Quantity.of(0.150, Metric.KILOGRAM);
		
		Quantity q2 = Quantity.of(0.025, Metric.LITER);
		
				
		Ingredient in1 = KitchenManager.createIngredient("Pizzateig", q1);
		Ingredient in2 = KitchenManager.createIngredient("Tomatensauce", q2);
		
		List<Ingredient> inList = new ArrayList<Ingredient>();
		inList.add(in1);
		inList.add(in2);
		
		//erstes Objekt aus der Liste von Meals mit dem Namen "Pizza"
		Iterable<Meal> pizzaMeals = kitchenManager.findMealsByName("Pizza");
						Meal meal = pizzaMeals.iterator().next();
						Recipe recipe = KitchenManager.createRecipe("Pizza machen", inList, meal.getIdentifier());
						kitchenManager.saveRecipe(recipe);
	}
	


	

}
