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


import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.business.BusinessManager;
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

	@RequestMapping({ "/", "/index" })
	public String index() {
		return "index";
	}
	
	@RequestMapping("/register")
	public String register(){
		return "register";
	}
	
	@RequestMapping("/registerUser")
	public String registerUser(@RequestParam("username") String username,
							   @RequestParam("password") String password,
							   @RequestParam("referal") String referal,
							   @RequestParam("firstname") String firstname,
							   @RequestParam("lastname") String lastname,
							   @RequestParam("email") String email){
		
		UserAccount user = userAccountManager.create(username, password, Role.of("ROLE_CUSTOMER"));
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setEmail(email);
		
		//Check if Business Code is legit
		if(businessManager.findBusinessByCode(referal).isPresent()){
			
			userAccountManager.save(user);
			customerManager.saveCustomer(customerManager.createCustomer(user, referal));
			
		} else {
			return "redirect:/register";
		}
		
		
		//System.out.println("Customer saved");
		
		return "index";
	}
	
	@RequestMapping("/about")
	public String about() {
		return "about";
	}
	
	@RequestMapping("/contact")
	public String contact() {
		return "contact";
	}
	
	@RequestMapping("/help")
	public String help() {
		return "help";
	}
}
