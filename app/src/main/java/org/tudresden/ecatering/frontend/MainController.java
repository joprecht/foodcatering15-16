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

@Controller
public class MainController {
	
	private final UserAccountManager userAccountManager;
	
	@Autowired
	public MainController(UserAccountManager userAccountManager) {

		this.userAccountManager = userAccountManager;
	}

	@RequestMapping({ "/", "/index" })
	public String index() {
		return "index";
	}
	
	@RequestMapping("/kitchen")
	public String kitchen() {
		return "kitchen";
	}
	
	@RequestMapping("/register")
	public String register(){
		return "register";
	}
	
	@RequestMapping("/registerUser")
	public String registerUser(@RequestParam("username") String username, @RequestParam("password") String password){
		
		UserAccount user = userAccountManager.create(username, password, new Role("ROLE_KITCHEN"));
		
		userAccountManager.save(user);
		
		
		return "index";
	}
}
