package org.tudresden.ecatering.frontend;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.stock.IngredientRepository;
import org.tudresden.ecatering.model.stock.StockManager;


@Controller
@PreAuthorize("hasRole('ROLE_STOCK')||hasRole('ROLE_ACCOUNTING')")
class StockController {

	private final StockManager stockManager;

	@Autowired
	public StockController(IngredientRepository inventory) {

		this.stockManager = new StockManager(inventory);
	}


	@RequestMapping("/stock")
	public String stockMethodsForMap(ModelMap modelMap) {

		modelMap.addAttribute("allIngredients", stockManager.findAllIngredients());

		return "stock";
	}
	
	@RequestMapping("/expirationReport")
	public String expirationReport(ModelMap modelMap){
		
		modelMap.addAttribute("expiredIngredients", stockManager.findExpiredIngredients());
		
		return "expirationReport";
	}
	
	@RequestMapping(value = "/removeExpiredIngredient", method = RequestMethod.POST)
	public String removeExpiredIngredient(@RequestParam("identifier") String identifier){
		
		//TODO Needs method for deleting ingredients
		
		return "redirect:/expirationReport";
	}

}
