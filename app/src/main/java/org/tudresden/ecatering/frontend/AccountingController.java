package org.tudresden.ecatering.frontend;

import java.util.Optional;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.business.BusinessRepository;
import org.tudresden.ecatering.model.customer.CustomerRepository;

@Controller
public class AccountingController {
	
	private final OrderManager<Order> orderManager;
	private final BusinessManager businessManager;
	
	@Autowired
	public AccountingController(OrderManager<Order> orderManager, BusinessRepository businessRepository){
		this.orderManager = orderManager;
		this.businessManager = new BusinessManager(businessRepository);
	}
	
	@RequestMapping("/retrieveVacantPositions")
	public String retrieveVacantPositions(ModelMap modelMap){
			//public static final OrderStatus orderStatus;
			OrderStatus o1 = OrderStatus.OPEN;
			modelMap.addAttribute("allVacantPostions",orderManager.find(o1));
		return "retrieveVacantPositions";
	}
	
	@RequestMapping(value = "/completeOrder", method = RequestMethod.POST)
	public String completeOrder(@RequestParam("OrderId") OrderIdentifier orderId){
		Optional<Order> o1 = orderManager.get(orderId);
		orderManager.completeOrder(o1.get());
		return "redirect:/retrieveVacantPositions";
	}
			
	@RequestMapping("/createBusiness")
	public String createBusiness(){
		return "createBusiness";
	}
	
	@RequestMapping(value = "/addBusiness", method = RequestMethod.POST)
	public String addBusiness(@RequestParam("name") String name,@RequestParam("type") String type){
		
		Address deliveryAddress = new Address("Frank","Zappa","Marienstrasse",21,"01307","Dresden","Deutschland");
		
		
		if(type=="social"){
			Business child = BusinessManager.createChildcareBusiness("Kita Kunterschwarz",deliveryAddress,"1234-5678","1234-5678");
			businessManager.saveBusiness(child);
			
		}else{
			Business comp = BusinessManager.createCompanyBusiness("Stahlwerk Sonnenschein",deliveryAddress,"1234-5678");
			businessManager.saveBusiness(comp);
		}
		
		
		return "redirect:/createBusiness";
	}
}
