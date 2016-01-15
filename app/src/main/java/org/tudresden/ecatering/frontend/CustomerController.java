package org.tudresden.ecatering.frontend;


import java.time.LocalDate;
import java.util.Optional;


import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;


@Controller
@PreAuthorize("hasRole('ROLE_ACCOUNTING')||hasRole('ROLE_CUSTOMER')")
public class CustomerController {
	
	private final UserAccountManager userAccountManager;
	private final CustomerManager customerManager; 
	
	@Autowired
	public CustomerController(CustomerManager customerManager, UserAccountManager userAccountManager){
		this.userAccountManager = userAccountManager;
		this.customerManager = customerManager;
	}
	
	/**
	 * Controller to change the expiration date of a customer account
	 * 
	 * @return changeExpirationDate.html
	 */
	@RequestMapping("/changeExpirationDate")
	public String changeExpirationDate(){
		return "changeExpirationDate";
	}
	
	/**
	 * The actual action of setting the expiration date from /changeExpirationDate
	 * 
	 * @param userAccount Current user
	 * @param year Expiration Year
	 * @param month Expiration Month
	 * @param day Expiration Day
	 * @return changeExpirationDate.html
	 */
	@RequestMapping(value = "/setExpirationDate", method = RequestMethod.POST)
	public String setExpirationDate(@LoggedIn Optional<UserAccount> userAccount, 
									@RequestParam("YYYY") int year,
									@RequestParam("MM") int month,
									@RequestParam("DD") int day){
		
		Optional<Customer> cust = customerManager.findCustomerByUserAccount(userAccount.get());
		Customer cust2 = cust.get();
		
		cust2.setExpirationDate(LocalDate.of(year, month, day));
		customerManager.saveCustomer(cust2);
		
		return "setExpirationDate";
	}
		
		/**
		 * Controller to show the user all his info
		 * 
		 * @param modelMap Required for Thymeleaf
		 * @param userAccount Current userAccount
		 * @return userAccount.html
		 */
		@RequestMapping("/UserAccount")
		public String userAccount(ModelMap modelMap,
								  @LoggedIn Optional<UserAccount> userAccount){
			
			Customer cust = customerManager.findCustomerByUserAccount(userAccount.get()).get();
			if(cust.isExpired()){
				return "expiredUser";
			}
			
			modelMap.addAttribute("user", customerManager.findCustomerByUserAccount(userAccount.get()).get());
			
			return "userAccount";
		}
	
		/**
		 * Controller to change a name or email of a customer
		 * 
		 * @param username Current username
		 * @param email New or Current email
		 * @param firstname New or Current first name
		 * @param lastname New or Current last name
		 * @return
		 */
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
