package org.tudresden.ecatering.model.customer;

import java.util.Iterator;
import java.util.Optional;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudresden.ecatering.model.accountancy.Discount;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.business.BusinessType;

@Component
public class CustomerManager {
	
	@Autowired private CustomerRepository customerRepo;
	@Autowired private UserAccountManager userAccountManager;
	@Autowired private BusinessManager businessManager;
	
	public CustomerManager() {
		
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
	
	public Iterable<Customer> findCustomersByBusiness(Business business) {
		return customerRepo.findByBusiness(business);
	}
	
	public Customer createCustomer(UserAccount userAccount, String businessCode)
	{
		if(!this.userAccountManager.get(userAccount.getIdentifier()).isPresent())
			throw new IllegalArgumentException("UserAccount for customer does not exist!");
		
		if(!this.businessManager.findBusinessByCode(businessCode).isPresent())
			throw new IllegalArgumentException("BusinessCode for customer does not exist!");
		
		Business business = this.businessManager.findBusinessByCode(businessCode).get();
		
		if(business.getBusinessType().equals(BusinessType.CHILDCARE) && businessCode.equals(business.getInstitutionCode()))
			return new Customer(userAccount,business,Discount.CHILDCARE);
		
		return new Customer(userAccount,business,Discount.NONE);				
	}
	
	public Customer saveCustomer(Customer customer) {
		
		
	
		return customerRepo.save(customer);
	}

}
