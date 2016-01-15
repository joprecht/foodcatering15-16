package org.tudresden.ecatering;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.tudresden.ecatering.model.kitchen.DailyMenu;
import org.tudresden.ecatering.model.kitchen.Day;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.Ingredient;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.MenuItem;
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
	
	//Company Business
	Address address2 = new Address("Erika","Musterfrau","Musterstrasse","23","21343","Musterstadt","Deutschland");
	
	businessManager.saveBusiness(businessManager.createCompanyBusiness("Musterfirma", address2, "comp"));

	
	//Childcare Business
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
		
		
		//customer for childcare
		UserAccount ua3 = userAccountManager.create("kunde", "123", Role.of("ROLE_CUSTOMER"));
		ua3.setFirstname("Max");
		ua3.setLastname("Mustermann");
		ua3.setEmail("max@mustermann.de");
		userAccountManager.save(ua3);
		customerManager.saveCustomer(customerManager.createCustomer(ua3, "2222"));
		
		//Customer for Company
		UserAccount ua4 = userAccountManager.create("kunde2", "123", Role.of("ROLE_CUSTOMER"));
		ua4.setFirstname("Erika");
		ua4.setLastname("Musterfrau");
		ua4.setEmail("erika@musterfrau.de");
		userAccountManager.save(ua4);
		customerManager.saveCustomer(customerManager.createCustomer(ua4, "comp"));

	}
	
private void initializeStock() {
		
		//Lebensmittel werden hinzugefuegt
		Grocery sahne = stockManager.saveGrocery(stockManager.createGrocery("Sahne", Metric.LITER, Money.of(3.59, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Schweinefleisch", Metric.KILOGRAM, Money.of(5.98, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Fisch", Metric.KILOGRAM, Money.of(20.00, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Bandnudeln", Metric.KILOGRAM, Money.of(2.58, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Kartoffeln", Metric.KILOGRAM, Money.of(1.59, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Rollmops", Metric.UNIT, Money.of(0.60, EURO)));

		
		//Sahne im Stock verfügbar
		stockManager.saveStockItem(stockManager.createStockItem(sahne, 0.525, LocalDate.of(2016, 12, 30)));
		stockManager.saveStockItem(stockManager.createStockItem(sahne, 1.325, LocalDate.of(2016, 12, 24)));
		stockManager.saveStockItem(stockManager.createStockItem(sahne, 1.325, LocalDate.of(2015, 12, 29)));

		
	}
	
private void initializeKitchen() {
		
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Sahne").get(), 0.100));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Bandnudeln").get(), 0.250));

	
		kitchenManager.saveRecipe(kitchenManager.createRecipe("Nudeln Special", "Nudeln kochen, abtropfen lassen. Sahne kochen und die beiden Zutaten mischen", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Nudeln Special").get(), MealType.SPECIAL, 1.2));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Schweinefleisch").get(), 0.300));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Bandnudeln").get(), 0.250));
	
		
		kitchenManager.saveRecipe(kitchenManager.createRecipe("Schweinefleisch mit Nudeln", "Schwein anbraten, würzen. Nudeln kochen, abtopfen. Servieren", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Schweinefleisch mit Nudeln").get(), MealType.REGULAR, 1.5));

		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.500));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Kartoffeln ohne allem", "Kartoffeln servieren", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Kartoffeln ohne allem").get(), MealType.DIET, 1.1));
		
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Sahne").get(), 0.500));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Sahnekartoffeln", "kartoffeln schälen und in Sahne überbacken", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Sahnekartoffeln").get(), MealType.REGULAR, 1.7));
		

		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Schweinefleisch").get(), 0.500));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Entrecote", "Fleisch sous-vide zubereiten, Kartoffeln kochen. Anrichten", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Entrecote").get(), MealType.REGULAR, 1.9));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.120));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Pellkartoffeln", "Kartoffeln pellen und kochen.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Pellkartoffeln").get(), MealType.REGULAR, 1.5));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.200));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Bratkartoffeln", "Kartoffeln schälen, in Scheiben schneiden und anbraten.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Bratkartoffeln").get(), MealType.REGULAR, 1.3));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.500));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Backkartoffel", "Kartoffel in Alufolie wickeln und backen.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Backkartoffel").get(), MealType.DIET, 1.2));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.250));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Pommes", "Kartoffeln schälen, in Stifte scheiden und fritieren.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Pommes").get(), MealType.DIET, 1.3));
		
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.245));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Herzoginnenkartoffeln", "Kartoffelbrei herstellen, backen", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Herzoginnenkartoffeln").get(), MealType.DIET, 1.5));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Kartoffelwedges", "Kartoffeln schälen, in Wedges schneiden und fritieren.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Kartoffelwedges").get(), MealType.DIET, 1.9));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 1.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Kartoffelbrei", "Kartoffeln kochen und stampfen.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Kartoffelbrei").get(), MealType.SPECIAL, 1.1));
		
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.700));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Röstis", "Kartoffeln grob reiben, zu Röstis formen, backen.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Röstis").get(), MealType.SPECIAL, 1.3));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.500));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Kartoffelpuffer", "Kartoffeln grob reiben, zu Röstis formen, anbraten.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Kartoffelpuffer").get(), MealType.SPECIAL, 1.5));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.200));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Chips", "Kartoffeln dünn schneiden und friterien.", ingredients));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Chips").get(), MealType.SPECIAL, 1.4));
		
		//Create Regular Menu for secondNextWeek
		
		ArrayList<MenuItem> mondayMeals = new ArrayList<MenuItem>();
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.MONDAY));

		ArrayList<MenuItem> tuesdayMeals = new ArrayList<MenuItem>();
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.TUESDAY));
		
		ArrayList<MenuItem> wednesdayMeals = new ArrayList<MenuItem>();
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));

		ArrayList<MenuItem> thursdayMeals = new ArrayList<MenuItem>();
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.THURSDAY));
		
		ArrayList<MenuItem> fridayMeals = new ArrayList<MenuItem>();
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.FRIDAY));
			
		ArrayList<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
		dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

		
		LocalDate date = LocalDate.now(); 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));	
		calendar.setMinimalDaysInFirstWeek(4);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		
		
		int i = calendar.get(Calendar.WEEK_OF_YEAR);
		System.out.println(i);
		kitchenManager.saveMenu(kitchenManager.createMenu(i+2, dailyMenus));
		
		
		//Create small menu for secondNextWeek
		//Commented out for Presentation purposes
        ArrayList<MenuItem> mondayMeals1 = new ArrayList<MenuItem>();
		mondayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.MONDAY));
		mondayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.MONDAY));
		mondayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.MONDAY));

		ArrayList<MenuItem> tuesdayMeals1 = new ArrayList<MenuItem>();
		tuesdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.TUESDAY));
		tuesdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.TUESDAY));
		tuesdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.TUESDAY));
		
		ArrayList<MenuItem> wednesdayMeals1 = new ArrayList<MenuItem>();
		wednesdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.WEDNESDAY));
		wednesdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.WEDNESDAY));
		wednesdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.WEDNESDAY));

		ArrayList<MenuItem> thursdayMeals1 = new ArrayList<MenuItem>();
		thursdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.THURSDAY));
		thursdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.THURSDAY));
		thursdayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.THURSDAY));
		
		ArrayList<MenuItem> fridayMeals1 = new ArrayList<MenuItem>();
		fridayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.FRIDAY));
		fridayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.FRIDAY));
		fridayMeals1.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.FRIDAY));
			
		ArrayList<DailyMenu> dailyMenus1 = new ArrayList<DailyMenu>();
		dailyMenus1.add(kitchenManager.createDailyMenu(mondayMeals1));
		dailyMenus1.add(kitchenManager.createDailyMenu(tuesdayMeals1));
		dailyMenus1.add(kitchenManager.createDailyMenu(wednesdayMeals1));
		dailyMenus1.add(kitchenManager.createDailyMenu(thursdayMeals1));
		dailyMenus1.add(kitchenManager.createDailyMenu(fridayMeals1));

		
		kitchenManager.saveMenu(kitchenManager.createMenu(i+2, dailyMenus1)); 
	}
	

	


	

}

