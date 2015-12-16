package org.tudresden.ecatering.frontend;


import java.time.LocalDate;
import java.util.Optional;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.customer.CustomerRepository;

@Controller
@PreAuthorize("hasRole('ROLE_ACCOUNTING')||hasRole('ROLE_CUSTOMER')")
public class CustomerController {
	
	private final OrderManager<Order> orderManager;
	private final CustomerRepository customerRepository;
	private final UserAccountManager userAccountManager;
	private final CustomerManager customerManager; 
	
	@Autowired
	public CustomerController(OrderManager<Order> orderManager, CustomerRepository customerRepository, CustomerManager customerManager, UserAccountManager userAccountManager){
		this.orderManager = orderManager;
		this.customerRepository = customerRepository;
		this.userAccountManager = userAccountManager;
		this.customerManager = customerManager;
	}
	
//	@RequestMapping(value="/deleteOrder", method = RequestMethod.POST)
//	public String deleteOrder(@RequestParam("OrderId") OrderIdentifier orderId){
//		Optional <Order> o1 = orderManager.get(orderId);
//		orderManager.cancelOrder(o1.get());
//		return "deleteOrder";
//	}
	
	//TODO HTML eventuell benötigt
//	@RequestMapping("/createOrder")
//	public String createOrder(){
//		//modelMap.addAttribute(attributeValue)
//		return "createOrder";
//	}
	
	
//	@RequestMapping(value = "/myOrders", method = RequestMethod.POST)
//	public String myOrders(@RequestParam("user") UserAccount userAccount, ModelMap modelMap){
//		//modelMap.addAttribute("orders",orderManager.find(userAccount));
//		return "myOrders";
//	}
	
	@RequestMapping("/changeExpirationDate")
	public String changeExpirationDate(){
		return "changeExpirationDate";
	}
	
	//TODO HTML vonnöten
	@RequestMapping(value = "/setExpirationDate", method = RequestMethod.POST)
	public String setExpirationDate(@LoggedIn Optional<UserAccount> userAccount, 
									@RequestParam("YYYY") int year,
									@RequestParam("MM") int month,
									@RequestParam("DD") int day){
		
		Optional<Customer> cust = customerManager.findCustomerByUserAccount(userAccount.get());
		Customer cust2 = cust.get();
		
		cust2.setExpirationDate(LocalDate.of(year, month, day));
		customerManager.saveCustomer(cust2);
		
		//customer.setExpirationDate(expirationDate);
		return "setExpirationDate";
	}
	
		//TODO Needs HTML, as well as the missing functions
		//TBD after we update the Customer Account
		@RequestMapping(value = "/change", method = RequestMethod.POST)
		public String change(@RequestParam("username") String username,
							 @RequestParam("email") String email,
							 @RequestParam("firstname") String firstname, 
							 @RequestParam("lastname") String lastname){
			
			Optional<UserAccount> user = userAccountManager.findByUsername(username);
			UserAccount user2 = user.get();
//			Optional<Customer> customer = customerManager.findCustomerByUserAccount(user2);
//			Customer customer2 = customer.get();
			//Now we have the right customer account and we can change parameters
			user2.setFirstname(firstname);
			user2.setLastname(lastname);
			user2.setEmail(email);
			userAccountManager.save(user2);
			
			return "change";
		}
		
		
}
