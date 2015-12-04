package org.tudresden.ecatering.frontend;

import java.util.Optional;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.customer.CustomerRepository;

public class CustomerController {
	
	private final OrderManager<Order> orderManager;
	private final CustomerRepository customerRepository;
	private final UserAccount userAccount;
	@Autowired
	public CustomerController(OrderManager<Order> orderManager, CustomerRepository customerRepository, UserAccount userAccount){
		this.orderManager = orderManager;
		this.customerRepository = customerRepository;
		this.userAccount = userAccount;
	}
	
	@RequestMapping(value="/deleteOrder", method = RequestMethod.POST)
	public String deleteOrder(@RequestParam("OrderId") OrderIdentifier orderId){
		Optional <Order> o1 = orderManager.get(orderId);
		orderManager.cancelOrder(o1.get());
		return "deleteOrder";
	}
	
	@RequestMapping("/createOrder")
	public String createOrder(){
		
		return "createOrder";
	}
	
//TODO vielleicht Button
//	@RequestMapping("/mealPlan")
//	public String mealPlan(){
//		return "mealPlan";
//	}
	
	@RequestMapping(value = "/myOrders", method = RequestMethod.POST)
	public String myOrders(@RequestParam("user") UserAccount userAccount){
		orderManager.find(userAccount);
		return "myOrders";
	}
	
	@RequestMapping("/setExpirationDate")
	public String setExpirationDate(){
		
		return "setExpirationDate";
	}
	

}
