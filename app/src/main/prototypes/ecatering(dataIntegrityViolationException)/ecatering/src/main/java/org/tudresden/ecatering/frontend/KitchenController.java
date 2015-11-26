package org.tudresden.ecatering.frontend;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tudresden.ecatering.kitchen.KitchenManager;
import org.tudresden.ecatering.kitchen.MealRepository;
import org.tudresden.ecatering.kitchen.RecipeRepository;


@Controller
@PreAuthorize("hasRole('ROLE_KITCHEN')")
class KitchenController {

	private final KitchenManager kitchenManager;

	@Autowired
	public KitchenController(KitchenManager kitchenManager) {

		this.kitchenManager = kitchenManager;
	}


	@RequestMapping("/kitchen")
	public String kitchenMethodsForMap(ModelMap modelMap) {

		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());

		return "kitchen";
	}

}
