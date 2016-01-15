package org.tudresden.ecatering.frontend;




import java.time.LocalDate;
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

	/**
	 * Controller to create a new Recipe out of Groceries
	 * Also displays all current Recipes
	 * 
	 * @param modelMap Required for Thymeleaf
	 * @return createRecipe.html
	 */
	@RequestMapping("/createRecipe")
	public String createRecipe(ModelMap modelMap){
		//List all Groceries Available that the cook can use to create a Recipe
		modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
		modelMap.addAttribute("allRecipes", kitchenManager.findAllRecipes());
		return "createRecipe";
	}
	
	/**
	 * Controller to save the recipe from /createRecipe
	 * 
	 * @param name Name of the new Recipe
	 * @param desc Description of the new Recipe
	 * @param ing ArrayList of Ingredients for the new Recipe
	 * @param quan ArrayList for the quantity of the previous Ingredients
	 * @return createRecipe.html
	 */
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
	
	/**
	 * Controller to list all current Recipes
	 * Absorbed into /createRecipe
	 * 
	 * @param modelMap Needed for Thymeleaf
	 * @return listRecipes.html
	 */
	@RequestMapping("/listRecipes")
	public String listRecipes(ModelMap modelMap) {
		modelMap.addAttribute("allRecipes", kitchenManager.findAllRecipes());
		return "listRecipes";
	}
	
	/**
	 * Displays a daily report on what needs to be cooked
	 * 
	 * @param modelMap Reuqired for Thymeleaf
	 * @return
	 */
	@RequestMapping("/kitchenReport")
	public String kitchenReport(ModelMap modelMap){
		
		LocalDate date = LocalDate.now();
		modelMap.addAttribute("kitchenReport", kitchenManager.getKitchenReportForDate(date));
		
		return "kitchenReport";
	}

}
