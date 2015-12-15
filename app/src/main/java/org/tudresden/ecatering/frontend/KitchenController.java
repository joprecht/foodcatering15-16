package org.tudresden.ecatering.frontend;




import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.kitchen.Ingredient;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.stock.StockManager;


@Controller
@PreAuthorize("hasRole('ROLE_KITCHEN')||hasRole('ROLE_ACCOUNTING')")
class KitchenController {

	private final StockManager stockManager;
	private final KitchenManager kitchenManager;

	@Autowired
	public KitchenController(KitchenManager kitchenManager, StockManager stockManager) {

		this.stockManager = stockManager;
		this.kitchenManager = kitchenManager;
	}
	
	
	@RequestMapping("/kitchen")
	public String kitchen() {
		return "kitchen";
	}

//	@RequestMapping("/listIngredients")
//	public String kitchenData(ModelMap modelMap) {
//
//		modelMap.addAttribute("allIngredients", stockManager.findAllStockItems());
//
//		return "listIngredients";
//	}
	
//	@RequestMapping("/createIngredient")
//	public String createIngredient() {
//		return "createIngredient";
//	}
	
	
//	@RequestMapping(value = "/addIngredient", method = RequestMethod.POST)
//	public String addIngredient(@RequestParam("name") String name, @RequestParam("quantity") double quantity,@RequestParam("price") double price, @RequestParam("metric") String metric,@RequestParam("DD") String dayInput,@RequestParam("MM") String monthInput,@RequestParam("YYYY") String yearInput) {
//		//Actually create the Ingredient
//		//Quantity menge = Quantity.of(quantity);
//		
//		int month = Integer.parseInt(monthInput);
//		int year = Integer.parseInt(yearInput);
//		int day = Integer.parseInt(dayInput);
//		
//		for(Metric m : Metric.values())
//	    {
//	      //System.out.println(m.name());
//	      if(m.name().contains(metric))
//	      {
//	        System.out.println("Metric: "+m.name());
//	        Quantity menge = Quantity.of(quantity, m);
//	        
//	        if(day==0){
//	        	
//	        	Ingredient ingredient = StockManager.createIngredient(name,Money.of(price, EURO),menge);
//	        	stockManager.saveIngredient(ingredient);
//	        	System.out.println("Ohne Datum");
//	        }else{
//	        int i=0;
//	        for(Month mo : Month.values())
//	        {
//	        	i++;
//	          //System.out.println(mo.name());
//	          if(i==month)
//	          {
//	            System.out.println("Monat: "+mo.name());
//	            Month m1 = mo;
//	            
//	            Ingredient ingredient = StockManager.createIngredient(name,Money.of(price, EURO),menge,LocalDate.of(year, m1, day));
//				//Need to find out how we can save the date right
//				stockManager.saveIngredient(ingredient);
//	          	}
//	          }
//	        }
//	      }
//	    }
//		
//		
//		
//		return "createIngredient";
//	}
//	
//	@RequestMapping("/createMeal")
//	public String createMenue(ModelMap modelMap) {
//		modelMap.addAttribute("allIngredients", stockManager.findAllStockItems());
//		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());
//		return "createMeal";
//	}
	
//	@RequestMapping(value = "/addMeal", method = RequestMethod.POST)
//	public String addMenue(@RequestParam("name") String name, @RequestParam("price") double price, @RequestParam("type") String mealType){
//		
//		Meal m1 = KitchenManager.createMeal(name, Money.of(price, EURO), MealType.REGULAR);
//		
//		if(mealType.equals("Diet")){
//			m1 = KitchenManager.createMeal(name, Money.of(price, EURO), MealType.DIET);
//		}else if(mealType.equals("Special")){
//			m1 = KitchenManager.createMeal(name, Money.of(price, EURO), MealType.SPECIAL);
//		}
//		
//		kitchenManager.saveMeal(m1);
//		
//		return "redirect:/createMeal";
//	}
//	
//	@RequestMapping("/listMeals")
//	public String listMeals(ModelMap modelMap) {
//		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());
//		return "listMeals";
//	}
	
	@RequestMapping("/createRecipe")
	public String createRecipe(ModelMap modelMap){
		//List all Groceries Available that the cook can use to create a Recipe
		modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
		modelMap.addAttribute("allRecipes", kitchenManager.findAllRecipes());
		return "createRecipe";
	}
	
	@RequestMapping(value = "/saveRecipe", method = RequestMethod.POST)
	public String saveRecipe(@RequestParam("name") String name, 
							 @RequestParam("description") String desc, 
							 @RequestParam("ing") ArrayList<String> ing, 
							 @RequestParam("quan") ArrayList<Double> quan) {
		
		if(ing.size()!=quan.size()){
			System.out.println("Error Size differernt");
			return "redirect:/createRecipe";
		}
		
		//Create a list of Ingredients
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		System.out.println("Max Ingredients"+ing.size()+"Max Quantity"+quan.size());
		
		//Go through the Array from the HTML form
		for(int i=0; i < ing.size(); i++){

	        System.out.println("Ingredient "+ing.get(i)+" Quantity "+quan.get(i));
	        //Add the ingredients to the ArrayList
	        ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName(ing.get(i)).get(), quan.get(i)));
	
		}
		//Save the recipe
		kitchenManager.saveRecipe(kitchenManager.createRecipe(name, desc, ingredients));

		return "redirect:/createRecipe";
	}
	
	@RequestMapping("/listRecipes")
	public String listRecipes(ModelMap modelMap) {
		modelMap.addAttribute("allRecipes", kitchenManager.findAllRecipes());
		return "listRecipes";
	}
	

}
