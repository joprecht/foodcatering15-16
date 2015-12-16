package org.tudresden.ecatering.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.kitchen.DailyMenu;
import org.tudresden.ecatering.model.kitchen.Day;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.MenuItem;

@Controller
@PreAuthorize("hasRole('ROLE_ACCOUNTING')")
public class AccountingController {
	
	private final OrderManager<Order> orderManager;
	private final BusinessManager businessManager;
	private final KitchenManager kitchenManager;
	
	@Autowired
	public AccountingController(OrderManager<Order> orderManager, BusinessManager businessManager, KitchenManager kitchenManager){
		this.orderManager = orderManager;
		this.businessManager = businessManager;
		this.kitchenManager = kitchenManager;
	}
	
	@RequestMapping("/retrieveVacantPositions")
	public String retrieveVacantPositions(ModelMap modelMap){
			//public static final OrderStatus orderStatus;
			OrderStatus o1 = OrderStatus.OPEN;
			modelMap.addAttribute("allVacantPostions",orderManager.findBy(o1));
		return "retrieveVacantPositions";
	}
	
	@RequestMapping(value = "/completeOrder", method = RequestMethod.POST)
	public String completeOrder(@RequestParam("OrderId") OrderIdentifier orderId){
		Optional<Order> o1 = orderManager.get(orderId);
		orderManager.completeOrder(o1.get());
		return "redirect:/retrieveVacantPositions";
	}
	
	//TODO New HTML Page
	@RequestMapping("/createBusiness")
	public String createBusiness(ModelMap modelMap){
		modelMap.addAttribute("allBusinesses", businessManager.findAllBusinesses());
		return "createBusiness";
	}
	
	@RequestMapping(value = "/addBusiness", method = RequestMethod.POST)
	public String addBusiness(@RequestParam("name") String name,
							  @RequestParam("type") String type,
							  @RequestParam("firstname") String firstname,
							  @RequestParam("lastname") String lastname,
							  @RequestParam("streetname") String streetname,
							  @RequestParam("streetnumber") String streetnumber,
							  @RequestParam("zip") String zip,
							  @RequestParam("city") String city,
							  @RequestParam("country") String country,
							  @RequestParam("referalcode") String referal,
							  @RequestParam(value = "institutioncode", required = false) String institutioncode){
		
		//country = "Germany";
		Address deliveryAddress = new Address(firstname,lastname,streetname,streetnumber,zip,city,country);
		
		
		if(type=="SOCIAL"){
			Business child = businessManager.createChildcareBusiness(name,deliveryAddress,referal,institutioncode);
			businessManager.saveBusiness(child);
			
		}else if(type=="COMPANY"){
			Business comp = businessManager.createCompanyBusiness(name,deliveryAddress,referal);
			businessManager.saveBusiness(comp);
		}
		
		
		return "redirect:/createBusiness";
	}
	
	//TODO new HTML needed
	@RequestMapping("/addMeal")
	public String addMeal(ModelMap modelMap){
		
		modelMap.addAttribute("allVacantPostions",kitchenManager.findUnusedRecipes());
		return "addMeal";
	}
	
	@RequestMapping(value = "/createMeal", method = RequestMethod.POST)
	public String createMeal(@RequestParam("name") String name,
							 @RequestParam("type") String type,
							 @RequestParam("multiplier") Double mult){
		
		for(MealType m : MealType.values())
	    {
	      if(m.name().contains(type))
	      {
	    	  kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName(name).get(), m, mult)); 
	      }
	     }

		return "createMeal";
	}
	
	@RequestMapping("/createPlan")
	public String createPlan(ModelMap modelMap){
		modelMap.addAttribute("allMeals",kitchenManager.findAllMeals());
		//Not sure whats easier to display in HTML
		modelMap.addAttribute("allMealsRegular",kitchenManager.findMealsByMealType(MealType.REGULAR));
		modelMap.addAttribute("allMealsSpecial",kitchenManager.findMealsByMealType(MealType.SPECIAL));
		modelMap.addAttribute("allMealsDiet",kitchenManager.findMealsByMealType(MealType.DIET));
		return "createPlan";
	}
	
	@RequestMapping(value = "/savePlan", method = RequestMethod.POST)
	public String savePlan(@RequestParam("meal") ArrayList<String> meal,
						   @RequestParam("week") Integer week){
		
		List<MenuItem> mondayMeals = new ArrayList<MenuItem>();
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(0)).get(),Helping.REGULAR,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(1)).get(),Helping.REGULAR,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(2)).get(),Helping.REGULAR,Day.MONDAY));

		List<MenuItem> tuesdayMeals = new ArrayList<MenuItem>();
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(3)).get(),Helping.REGULAR,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(4)).get(),Helping.REGULAR,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(5)).get(),Helping.REGULAR,Day.TUESDAY));
		
		List<MenuItem> wednesdayMeals = new ArrayList<MenuItem>();
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(6)).get(),Helping.REGULAR,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(8)).get(),Helping.REGULAR,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(9)).get(),Helping.REGULAR,Day.WEDNESDAY));

		List<MenuItem> thursdayMeals = new ArrayList<MenuItem>();
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(10)).get(),Helping.REGULAR,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(11)).get(),Helping.REGULAR,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(12)).get(),Helping.REGULAR,Day.THURSDAY));
		
		List<MenuItem> fridayMeals = new ArrayList<MenuItem>();
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(13)).get(),Helping.REGULAR,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(14)).get(),Helping.REGULAR,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(15)).get(),Helping.REGULAR,Day.FRIDAY));
			
		List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
		dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

		kitchenManager.saveMenu(kitchenManager.createMenu(week, dailyMenus));
		
		return "redirect:/createPlan";
	}
}
