package org.tudresden.ecatering;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDateTime;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.tudresden.ecatering.kitchen.Ingredient;
import org.tudresden.ecatering.kitchen.IngredientRepository;
import org.tudresden.ecatering.kitchen.KitchenManager;
import org.tudresden.ecatering.kitchen.Meal;
import org.tudresden.ecatering.kitchen.Meal.MealType;
import org.tudresden.ecatering.kitchen.MealRepository;
import org.tudresden.ecatering.kitchen.StockManager;


@Component
public class ECateringDataInitializer implements DataInitializer {

	private final UserAccountManager userAccountManager;
	private final StockManager stockManager;
	private final KitchenManager kitchenManager;

	@Autowired
	public ECateringDataInitializer(IngredientRepository inventory, MealRepository mealRepo,
			UserAccountManager userAccountManager) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");


		this.userAccountManager = userAccountManager;
		this.stockManager = new StockManager(inventory);
		this.kitchenManager = new KitchenManager(mealRepo);
	}
	
	@Override
	public void initialize() {

		initializeUsers(this.userAccountManager);
		initializeInventory(this.stockManager);
		initializeMealRepo(this.kitchenManager);

	}
	
	private void initializeMealRepo(KitchenManager kitchenManager) {
		
		Meal m1 = kitchenManager.createMeal("Spaghetti", Money.of(4.50, EURO), MealType.REGULAR);
		Meal m2 = kitchenManager.createMeal("Milchreis", Money.of(2.50, EURO), MealType.DIET);
		
		kitchenManager.saveMeal(m1);
		kitchenManager.saveMeal(m2);

	}

	private void initializeInventory(StockManager stockManager) {

		Product product1 = new Product("Quark",Money.of(0.79, EURO));
		Product product2 = new Product("Jagdwurst",Money.of(1.45, EURO));
		Quantity menge = Quantity.of(2);
		
		Ingredient in1 = stockManager.createIngredient(product1,menge,LocalDateTime.of(2015, 12, 24, 0, 0));
		stockManager.saveIngredient(in1);
		Ingredient in2 = stockManager.createIngredient(product2,menge,LocalDateTime.of(2015, 11, 30, 0, 0));
		stockManager.saveIngredient(in2);
	}

	private void initializeUsers(UserAccountManager userAccountManager) {

		
		if (userAccountManager.findByUsername("koch").isPresent()) {
			return;
		}

		UserAccount ua1 = userAccountManager.create("koch", "123", new Role("ROLE_KITCHEN"));
		userAccountManager.save(ua1);

	}
}
