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
import org.tudresden.ecatering.model.business.BusinessRepository;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.customer.CustomerRepository;

@Controller
public class MainController {
	
	private final UserAccountManager userAccountManager;
	private final CustomerManager customerManager;
	private final BusinessManager businessManager;
	
	@Autowired
	  public MainController(UserAccountManager userAccountManager, CustomerRepository customerRepository, BusinessRepository businessRepository) {
		this.userAccountManager = userAccountManager;
	    this.businessManager = new BusinessManager(businessRepository);
	    this.customerManager = new CustomerManager(customerRepository, userAccountManager, businessManager);
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
	public String registerUser(@RequestParam("username") String username, @RequestParam("password") String password,@RequestParam("referal") String referal){
		
		UserAccount user = userAccountManager.create(username, password, new Role("ROLE_CUSTOMER"));
		
		userAccountManager.save(user);
		
		Customer cust = CustomerManager.createCustomer(user, referal);
		
		customerManager.saveCustomer(cust);
		
		
		return "index";
	}
	
	//TODO @RequestMapping für emailänderung, vornameänderung und nachnameänderung
}
