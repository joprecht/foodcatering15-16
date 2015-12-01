package org.tudresden.ecatering;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.tudresden.ecatering.kitchen.KitchenManager;
import org.tudresden.ecatering.kitchen.Meal;
import org.tudresden.ecatering.kitchen.MealType;
import org.tudresden.ecatering.kitchen.Recipe;
import org.tudresden.ecatering.kitchen.RecipeRepository;
import org.tudresden.ecatering.stock.Ingredient;
import org.tudresden.ecatering.stock.IngredientRepository;
import org.tudresden.ecatering.stock.StockManager;
import org.tudresden.ecatering.kitchen.MealRepository;

@Component
public class ECateringDataInitializer implements DataInitializer {

	private final UserAccountManager userAccountManager;
	private final StockManager stockManager;
	private final KitchenManager kitchenManager;

	private Ingredient in1;
	private Ingredient in2;

	@Autowired Catalog<Product> productRepo;

	@Autowired
	public ECateringDataInitializer(IngredientRepository inventory, MealRepository mealRepo, RecipeRepository recipeRepo,
			UserAccountManager userAccountManager, KitchenManager kitchenManager) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.userAccountManager = userAccountManager;
		this.stockManager = new StockManager(inventory);
		this.kitchenManager = kitchenManager;
	}

	@Override
	public void initialize() {

		initializeUsers();
		initializeInventory();
		initializeMealRepo();
		initializeRecipeRepo();

	}

	private void initializeRecipeRepo() {

		List<Ingredient> inList = new ArrayList<Ingredient>();
		inList.add(in1);
		inList.add(in2);

		// erstes Objekt aus der Liste von Meals mit dem Namen "Pizza"
		Iterable<Meal> pizzaMeals = kitchenManager.findMealsByName("Pizza");
		Meal meal = pizzaMeals.iterator().next();
		Recipe recipe = kitchenManager.createRecipe("Pizza machen", inList);
		kitchenManager.saveRecipe(recipe);
	}

	private void initializeMealRepo() {

		Meal m1 = kitchenManager.createMeal("Pizza", Money.of(4.50, EURO), MealType.REGULAR);
		Meal m2 = kitchenManager.createMeal("Milchreis", Money.of(2.50, EURO), MealType.DIET);

		kitchenManager.saveMeal(m1);
		kitchenManager.saveMeal(m2);

	}

	private void initializeInventory() {

		Product product1 = new Product("Quark", Money.of(0.79, EURO));
		Product product2 = new Product("Jagdwurst", Money.of(1.45, EURO));
		Quantity menge = Quantity.of(2);

		productRepo.save(product1);
		productRepo.save(product2);

		in1 = stockManager.createIngredient(product1, menge, LocalDateTime.of(2015, 12, 24, 0, 0));
		stockManager.saveIngredient(in1);
		in2 = stockManager.createIngredient(product2, menge, LocalDateTime.of(2015, 11, 30, 0, 0));
		stockManager.saveIngredient(in2);
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
}
