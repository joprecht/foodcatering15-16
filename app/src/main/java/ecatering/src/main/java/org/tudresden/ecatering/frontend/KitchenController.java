package org.tudresden.ecatering.frontend;

import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;
import org.tudresden.ecatering.stock.Ingredient;
import org.tudresden.ecatering.stock.IngredientRepository;
import org.tudresden.ecatering.kitchen.KitchenManager;
import org.tudresden.ecatering.kitchen.Meal;
import org.tudresden.ecatering.kitchen.MealRepository;
import org.tudresden.ecatering.stock.StockManager;
import org.tudresden.ecatering.kitchen.MealType;
import org.tudresden.ecatering.kitchen.Recipe;
import org.tudresden.ecatering.kitchen.RecipeRepository;


@Controller
@PreAuthorize("hasRole('ROLE_KITCHEN')")
class KitchenController {

	private final StockManager stockManager;
	private final KitchenManager kitchenManager;

	@Autowired
	public KitchenController(IngredientRepository inventory, MealRepository mealRepo,RecipeRepository recipes) {

		this.stockManager = new StockManager(inventory);
		this.kitchenManager = new KitchenManager(mealRepo, recipes);
	}


	@RequestMapping("/listIngredients")
	public String kitchenData(ModelMap modelMap) {

		modelMap.addAttribute("allIngredients", stockManager.findAllIngredients());

		return "listIngredients";
	}
	
	@RequestMapping("/createIngredient")
	public String createIngredient() {
		return "createIngredient";
	}
	
	@RequestMapping(value = "/addIngredient", method = RequestMethod.POST)
	public String addIngredient(@RequestParam("name") String name, @RequestParam("quantity") int quantity,@RequestParam("price") double price,@RequestParam("DD") int day,@RequestParam("MM") int month,@RequestParam("YYYY") int year) {
		//Actually create the Ingredient
		Product product = new Product(name,Money.of(price, EURO));
		Quantity menge = Quantity.of(quantity);
		Ingredient ingredient = stockManager.createIngredient(product,menge,LocalDateTime.of(year, month, day, 0, 0));
		//Need to find out how we can save the date right
		stockManager.saveIngredient(ingredient);
		return "createIngredient";
	}
	
	@RequestMapping("/createMeal")
	public String createMenue(ModelMap modelMap) {
		modelMap.addAttribute("allIngredients", stockManager.findAllIngredients());
		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());
		return "createMeal";
	}
	
	@RequestMapping(value = "/addMeal", method = RequestMethod.POST)
	public String addMenue(@RequestParam("name") String name, @RequestParam("price") double price, @RequestParam("type") String mealType){
		
		Meal m1 = kitchenManager.createMeal(name, Money.of(price, EURO), MealType.REGULAR);
		
		if(mealType.equals("Diet")){
			m1 = kitchenManager.createMeal(name, Money.of(price, EURO), MealType.DIET);
		}else if(mealType.equals("Special")){
			m1 = kitchenManager.createMeal(name, Money.of(price, EURO), MealType.SPECIAL);
		}
		
		kitchenManager.saveMeal(m1);
		
		return "redirect:/createMeal";
	}
	
	@RequestMapping("/listMeals")
	public String listMeals(ModelMap modelMap) {
		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());
		return "listMeals";
	}
	
	@RequestMapping("/listRecipes")
	public String listRecipes(ModelMap modelMap) {
		modelMap.addAttribute("allRecipes", kitchenManager.findAllRecipes());
		return "listRecipes";
	}
	
	@RequestMapping("/createRecipe")
	public String createRecipe(ModelMap modelMap){
		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());
		return "createRecipe";
	}
	
	@RequestMapping(value = "/saveRecipe", method = RequestMethod.POST)
	public String saveRecipe(@RequestParam("name") String name, @RequestParam("meal") String meal, @RequestParam("ing") ArrayList<String> ing, @RequestParam("quan") ArrayList<Integer> quan) {
		//Check if I can send arrays to java if the namefields are called array[]
		
//		if(ing.size()!=quan.size()){
//			
//		}
		
		List<Ingredient> inList = new ArrayList<Ingredient>();
		System.out.println("Max Ing"+ing.size()+"Max Quan"+quan.size());
		for(int i=0; i < ing.size(); i++){
			
		System.out.println("Ingreidents"+ing.get(i)+" Quantity"+quan.get(i));

		
//		Product p1 = new Product(ing.get(i),Money.of(0.00, EURO));
//		Quantity q1 = Quantity.of(quan.get(i));
//		LocalDateTime expDate1 = LocalDateTime.of(2015, 12, 24, 0, 0);
		
		//Ingredient in1 = new Ingredient(p1,q1,expDate1);
		
		inList.add(new Ingredient(new Product(ing.get(i),Money.of(0.00, EURO)),Quantity.of(quan.get(i)),LocalDateTime.of(2015, 12, 24, 0, 0)));

		}
		
	
				Iterable<Meal> pizzaMeals = kitchenManager.findMealsByName(meal);
				Meal meals = pizzaMeals.iterator().next();
				Recipe recipe = kitchenManager.createRecipe(name, inList, meals.getIdentifier());
				kitchenManager.saveRecipe(recipe);
		

		
						
		
		
		return "redirect:/createRecipe";
	}

}
