package org.tudresden.ecatering.frontend;

import java.util.Optional;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MealType;

@Controller
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
	public String createBusiness(){
		return "createBusiness";
	}
	
	@RequestMapping(value = "/addBusiness", method = RequestMethod.POST)
	public String addBusiness(@RequestParam("name") String name,
							  @RequestParam("type") String type,
							  @RequestParam("firstname") String firstname,
							  @RequestParam("lastname") String lastname,
							  @RequestParam("streetname") String streetname,
							  @RequestParam("streetnumber") String streetnumber,
							  @RequestParam("plz") String plz,
							  @RequestParam("city") String city,
							  @RequestParam("country") String country,
							  @RequestParam("referalcode") String referal,
							  @RequestParam(value = "institutioncode", required = false) String institutioncode){
		
		Address deliveryAddress = new Address(firstname,lastname,streetname,streetnumber,plz,city,country);
		
		
		if(type=="social"){
			Business child = businessManager.createChildcareBusiness(name,deliveryAddress,referal,institutioncode);
			businessManager.saveBusiness(child);
			
		}else{
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
}
