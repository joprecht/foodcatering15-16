package org.tudresden.ecatering.model.customer;

import java.util.Iterator;
import java.util.Optional;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.tudresden.ecatering.model.business.BusinessManager;

public class CustomerManager {
	
	private CustomerRepository customerRepo;
	private UserAccountManager userAccountManager;
	private BusinessManager businessManager;
	
	public CustomerManager(CustomerRepository customers, UserAccountManager userAccountManager, BusinessManager businessManager) {
		this.customerRepo = customers;
		this.userAccountManager = userAccountManager;
		this.businessManager = businessManager;
	}
	
	public Iterable<Customer> findAllCustomers() {
		
		return customerRepo.findAll();
	}
	
	
	public Iterable<Customer> findExpiredCustomers() {
		Iterable<Customer> customersResult = this.findAllCustomers();
		Iterator<Customer> iter = customersResult.iterator();
		
		while(iter.hasNext())
		{
			if(!iter.next().isExpired())
				iter.remove();
		}
		
		return customersResult;
	}
	
	public Optional<Customer> findCustomerByUserAccount(UserAccount userAccount) {
		return customerRepo.findByUserAccount(userAccount);
	}
	
	public Iterable<Customer> findCustomersByBusinessCode(String businessCode) {
		return customerRepo.findByBusinessCode(businessCode);
	}
	
	public static Customer createCustomer(UserAccount userAccount, String businessIdentifier)
	{
		return new Customer(userAccount,businessIdentifier);
	}
	
	public Customer saveCustomer(Customer customer) {
		
		if(!this.userAccountManager.get(customer.getUserAccount().getIdentifier()).isPresent())
			throw new IllegalArgumentException("UserAccount for customer does not exist!");
		
		if(!this.businessManager.findBusinessByCode(customer.getBusinessCode()).isPresent())
			throw new IllegalArgumentException("BusinessCode for customer does not exist!");
	
		return customerRepo.save(customer);
	}

}
