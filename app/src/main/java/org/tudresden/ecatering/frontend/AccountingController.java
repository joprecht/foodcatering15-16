package org.tudresden.ecatering.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.tudresden.ecatering.model.accountancy.MealOrder;
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
	
	private final OrderManager<MealOrder> orderManager;
	private final BusinessManager businessManager;
	private final KitchenManager kitchenManager;
	
	@Autowired
	public AccountingController(OrderManager<MealOrder> orderManager, BusinessManager businessManager, KitchenManager kitchenManager){
		this.orderManager = orderManager;
		this.businessManager = businessManager;
		this.kitchenManager = kitchenManager;
	}
	
	/**
	 * Controller that shows the Accounting User all open orders
	 * 
	 * @param modelMap Required for Thymeleaf to display the data
	 * @return retrieveVacantPositions.html
	 */
	@RequestMapping("/retrieveVacantPositions")
	public String retrieveVacantPositions(ModelMap modelMap){
		OrderStatus o1 = OrderStatus.OPEN;
		OrderStatus o2 = OrderStatus.PAID;
			modelMap.addAttribute("allCompletedPostions",orderManager.findBy(o2));
			modelMap.addAttribute("allVacantPostions",orderManager.findBy(o1));
			System.out.println(orderManager.findBy(o1));
		return "retrieveVacantPositions";
	}
	
	/**
	 * Controller responsible for completing the open Orders from
	 * the retrieveVacantPositions mapping
	 * @see retrieveVacantPositions 
	 * @param orderId The Id of the Order that is to be completed
	 * @return retrieveVacantPositions.html
	 */
	@RequestMapping(value = "/completeOrder", method = RequestMethod.POST)
	public String completeOrder(@RequestParam("OrderId") OrderIdentifier orderId){
		Optional<MealOrder> o1 = orderManager.get(orderId);
		orderManager.payOrder(o1.get());
		//orderManager.completeOrder(o1.get());
		return "redirect:/retrieveVacantPositions";
	}
	
	/**
	 * Controller to create new Businesses as well as showing all existing ones
	 * 
	 * @param modelMap Required for tymeleaf
	 * @return createBusiness.html
	 */
	@RequestMapping("/createBusiness")
	public String createBusiness(ModelMap modelMap){
		modelMap.addAttribute("allBusinesses", businessManager.findAllBusinesses());
		return "createBusiness";
	}
	
	/**
	 * Controller to actually save the new Businesses
	 * that can be entered in /createBusiness
	 * @param name The name of the new Business
	 * @param type The name of the new Business (Social or Company)
	 * @param firstname The first name of the Business Owner
	 * @param lastname The last name of the new Business Owner
	 * @param streetname The Streetname of the new Business
	 * @param streetnumber The Streetnumber of the new Business
	 * @param zip The Zip code of the new Business
	 * @param city The City of the new Business
	 * @param country The Country of the new Business
	 * @param referal The referalcode of the new Business
	 * @param institutioncode The Institution code of the new Business (only if type=social)
	 * @see createBusiness
	 * @return createBusiness.html
	 */
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
		
		
		if(type.equals("SOCIAL")){
			System.out.println("Social");
			Business child = businessManager.createChildcareBusiness(name,deliveryAddress,referal,institutioncode);
			businessManager.saveBusiness(child);
			
		}else if(type.equals("COMPANY")){
			System.out.println("Company");
			Business comp = businessManager.createCompanyBusiness(name,deliveryAddress,referal);
			businessManager.saveBusiness(comp);
		}
		
		
		return "redirect:/createBusiness";
	}
	
	/**
	 * Controller to show all Recipes that have not yet been given a Meal
	 * 
	 * @param modelMap Required for Thymeleaf
	 * @return createMeal.html
	 */
	@RequestMapping("/createMeal")
	public String createMeal(ModelMap modelMap){
		modelMap.addAttribute("allVacantPostions",kitchenManager.findUnusedRecipes());
		return "createMeal";
	}
	
	/**
	 * Controller to save the new meals created in /createMeal
	 * 
	 * @param name Name of the Recipe that will be turned into a meal
	 * @param mult Cost Multiplier So that we can profit from the food
	 * @param type MealType (Regular/Diet/Special)
	 * @return createMeal.html
	 */
	@RequestMapping(value = "/addMeal", method = RequestMethod.POST)
	public String addMeal(@RequestParam("name") String name,
						  @RequestParam("multiplier") Double mult,
						  @RequestParam("type") String type){

				//Goes through the MealType enumeration to match the right MealType
				//Contains is case-sensitive so send REGULAR, DIET or SPECIAL
				for(MealType m : MealType.values()){
			      if(m.name().contains(type)){
			    	  kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName(name).get(), m, mult)); 
			      }
			     }
				
				return "redirect:/createMeal";
	}
	
	/**
	 * Controller to create a Plan for the following Weeks
	 * Can specify any week as long as it hasn't been created before
	 * 
	 * @param modelMap Required for Thymelaf
	 * @return createPlan.html
	 */
	@RequestMapping("/createPlan")
	public String createPlan(ModelMap modelMap){
		modelMap.addAttribute("allMeals",kitchenManager.findAllMeals());
		//Not sure whats easier to display in HTML
		modelMap.addAttribute("allMealsRegular",kitchenManager.findMealsByMealType(MealType.REGULAR));
		modelMap.addAttribute("allMealsSpecial",kitchenManager.findMealsByMealType(MealType.SPECIAL));
		modelMap.addAttribute("allMealsDiet",kitchenManager.findMealsByMealType(MealType.DIET));
		return "createPlan";
	}
	
	/**
	 * Controller to save the Plan created in /createPlan
	 * 
	 * @param meal Array of Meals to be saved for each day
	 * @param week The Calendar Week for the Plan
	 * @param helping The size for the plan, both small and Regular have to be saved independently
	 * @return createPlan
	 */
	@RequestMapping(value = "/savePlan", method = RequestMethod.POST)
	public String savePlan(@RequestParam("meal") ArrayList<String> meal,
						   @RequestParam("week") Integer week,
						   @RequestParam("helping") String helping){
		
		Helping size = null;
		
		if(helping.equals("regular")){
			size = Helping.REGULAR;
		} else if(helping.equals("small")){
			size = Helping.SMALL;
		}else{
			//If wrong helping size send User back to site
			return "redirect:/createPlan";
		}
		
		List<MenuItem> mondayMeals = new ArrayList<MenuItem>();
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(0)).get(),size,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(5)).get(),size,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(10)).get(),size,Day.MONDAY));

		List<MenuItem> tuesdayMeals = new ArrayList<MenuItem>();
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(1)).get(),size,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(6)).get(),size,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(11)).get(),size,Day.TUESDAY));
		
		List<MenuItem> wednesdayMeals = new ArrayList<MenuItem>();
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(2)).get(),size,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(7)).get(),size,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(12)).get(),size,Day.WEDNESDAY));

		List<MenuItem> thursdayMeals = new ArrayList<MenuItem>();
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(3)).get(),size,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(8)).get(),size,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(13)).get(),size,Day.THURSDAY));
		
		List<MenuItem> fridayMeals = new ArrayList<MenuItem>();
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(4)).get(),size,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(9)).get(),size,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(meal.get(14)).get(),size,Day.FRIDAY));
			
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
