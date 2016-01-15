/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tudresden.ecatering.frontend;



import java.time.LocalDate;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;

@Controller
public class MainController {
	
	private final UserAccountManager userAccountManager;
	private final CustomerManager customerManager;
	private final BusinessManager businessManager;
	
	@Autowired
	  public MainController(UserAccountManager userAccountManager, CustomerManager customerManager, BusinessManager businessManager) {
		this.userAccountManager = userAccountManager;
		this.businessManager = businessManager;
	    this.customerManager = customerManager;
	  }

	/**
	 * Basic Controller that show the index Page,
	 * welcoming the User to the Page and offering a login form.
	 *
	 * @return      index.html
	 */
	@RequestMapping({ "/", "/index" })
	public String index() {
		return "index";
	}
	
	
	/**
	 * Basic Controll to display the Register page
	 * 
	 * @return register.html
	 */
	@RequestMapping("/register")
	public String register(){
		return "register";
	}
	
	/**
	 * Controller used in the register form
	 * 
	 * @param username The username for the new account
	 * @param password The password for the new account
	 * @param referal The referalcode to match the user to a business
	 * @param firstname The firstname for the new customerAccount
	 * @param lastname The lastname for the new customerAccount
	 * @param email The email for the new customerAccount
	 * @return index.html
	 */
	@RequestMapping("/registerUser")
	public String registerUser(@RequestParam("username") String username,
							   @RequestParam("password") String password,
							   @RequestParam("referal") String referal,
							   @RequestParam("firstname") String firstname,
							   @RequestParam("lastname") String lastname,
							   @RequestParam("email") String email,
							   @RequestParam(value="year", required = false) Integer year,
							   @RequestParam(value="month", required = false) Integer month,
							   @RequestParam(value="day", required = false) Integer day){
		
		UserAccount user = userAccountManager.create(username, password, Role.of("ROLE_CUSTOMER"));
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setEmail(email);
		
		//Check if Business Code is legit
		if(businessManager.findBusinessByCode(referal).isPresent()){
			
			userAccountManager.save(user);
			Customer cust = customerManager.createCustomer(user, referal);
			
						try{
							cust.setExpirationDate(LocalDate.of(year, month, day));	
				 		}
				 		catch(Exception e)
				 		{
				 			//System.out.println(e+"\n");
				 		}
			customerManager.saveCustomer(cust);
			
		} else {
			return "redirect:/register";
		}
		
		
		//System.out.println("Customer saved");
		
		return "index";
	}
	
	/**
	 * Basic Controller to Map the about page
	 * @return about.html
	 */
	@RequestMapping("/about")
	public String about() {
		return "about";
	}
	
	/**
	 * Basic Controller to Map the contact page
	 * @return contact.html
	 */
	@RequestMapping("/contact")
	public String contact() {
		return "contact";
	}
	
	/**
	 * Basic Controller to Map the help page
	 * @return help.html
	 */
	@RequestMapping("/help")
	public String help() {
		return "help";
	}

	
}
