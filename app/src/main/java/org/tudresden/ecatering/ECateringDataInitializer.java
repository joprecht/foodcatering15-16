package org.tudresden.ecatering;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Metric;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.accountancy.MealOrder;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.kitchen.Ingredient;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.stock.Grocery;
import org.tudresden.ecatering.model.stock.StockManager;


@Component
public class ECateringDataInitializer implements DataInitializer {

	private final UserAccountManager userAccountManager;
	private final StockManager stockManager;
	private final KitchenManager kitchenManager;
	private final CustomerManager customerManager;
	private final BusinessManager businessManager;



	@Autowired
	public ECateringDataInitializer(StockManager stockManager, KitchenManager kitchenManager,
			UserAccountManager userAccountManager, CustomerManager customerManager, BusinessManager businessManager, OrderManager<MealOrder> orderManager) {

		Assert.notNull(stockManager, "StockManager must not be null!");
		Assert.notNull(kitchenManager, "KitchenManager must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
		Assert.notNull(customerManager, "CustomerManager must not be null!");
		Assert.notNull(businessManager, "BusinessManager must not be null!");


		this.userAccountManager = userAccountManager;
		this.stockManager = stockManager;
		this.kitchenManager = kitchenManager;
		this.businessManager = businessManager;
		this.customerManager = customerManager;
	}
	
	@Override
	public void initialize() {

		initializeBusiness();
		initializeUsers();
		initializeStock();
		initializeKitchen();

	}
	
private void initializeBusiness() {
	
	Address address = new Address("Max","Mustermann","Musterstrasse","12","01307","Dresden","Deutschland");
	
	businessManager.saveBusiness(businessManager.createChildcareBusiness("Kindergarten Marienhof", address, "1111", "2222"));
	
}
	
private void initializeUsers() {

		
		if (userAccountManager.findByUsername("koch").isPresent()) {
			return;
		}

		UserAccount ua0 = userAccountManager.create("boss", "123", Role.of("ROLE_ACCOUNTING"));
		userAccountManager.save(ua0);
		
		UserAccount ua1 = userAccountManager.create("koch", "123", Role.of("ROLE_KITCHEN"));
		userAccountManager.save(ua1);
		
		UserAccount ua2 = userAccountManager.create("lager", "123", Role.of("ROLE_STOCK"));
		userAccountManager.save(ua2);
		
		
		//customer
		UserAccount ua3 = userAccountManager.create("kunde", "123", Role.of("ROLE_CUSTOMER"));
		ua3.setFirstname("Max");
		ua3.setLastname("Mustermann");
		ua3.setEmail("max@mustermann.de");
		userAccountManager.save(ua3);
		customerManager.saveCustomer(customerManager.createCustomer(ua3, "2222"));

	}
	
private void initializeStock() {
		
		//Lebensmittel werden hinzugefuegt
		Grocery sahne = stockManager.saveGrocery(stockManager.createGrocery("Sahne", Metric.LITER, Money.of(0.79, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Schweinefleisch", Metric.KILOGRAM, Money.of(1.50, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Fisch", Metric.KILOGRAM, Money.of(4.30, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Bandnudeln", Metric.KILOGRAM, Money.of(0.50, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Kartoffeln", Metric.KILOGRAM, Money.of(0.80, EURO)));

		
		//Sahne im Stock verfügbar
		stockManager.saveStockItem(stockManager.createStockItem(sahne, 0.525, LocalDate.of(2016, 12, 30)));
		stockManager.saveStockItem(stockManager.createStockItem(sahne, 1.325, LocalDate.of(2016, 12, 24)));

		
	}
	
private void initializeKitchen() {
		
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Sahne").get(), 0.200));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Bandnudeln").get(), 0.100));

	
		kitchenManager.saveRecipe(kitchenManager.createRecipe("Nudeln special", "...", ingredients));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Schweinefleisch").get(), 0.200));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Bandnudeln").get(), 0.100));
		
		kitchenManager.saveRecipe(kitchenManager.createRecipe("Schweinefleisch mit Nudeln", "...", ingredients));

		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Kartoffeln ohne allem", "...", ingredients));
		
		
		//meals für rezepte
		
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Nudeln special").get(), MealType.SPECIAL, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Schweinefleisch mit Nudeln").get(), MealType.REGULAR, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Kartoffeln ohne allem").get(), MealType.DIET, 1.3));
		
		

		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("A", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("B", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("C", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("D", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("E", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("F", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("G", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("H", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("I", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("J", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("K", "...", ingredients));
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("L", "...", ingredients));
		
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("A").get(), MealType.REGULAR, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("B").get(), MealType.REGULAR, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("C").get(), MealType.REGULAR, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("D").get(), MealType.REGULAR, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("E").get(), MealType.DIET, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("F").get(), MealType.DIET, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("G").get(), MealType.DIET, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("H").get(), MealType.DIET, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("I").get(), MealType.SPECIAL, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("J").get(), MealType.SPECIAL, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("K").get(), MealType.SPECIAL, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("L").get(), MealType.SPECIAL, 1.3));
		

		
		

	}
	

	


	

}

