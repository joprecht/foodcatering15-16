package org.tudresden.ecatering.frontend;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tudresden.ecatering.stock.IngredientRepository;
import org.tudresden.ecatering.stock.StockManager;


@Controller
@PreAuthorize("hasRole('ROLE_STOCK')")
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

}
