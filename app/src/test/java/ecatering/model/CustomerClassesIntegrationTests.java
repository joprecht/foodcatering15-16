package ecatering.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.accountancy.Discount;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.business.BusinessType;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;

import ecatering.AbstractIntegrationTests;

public class CustomerClassesIntegrationTests extends AbstractIntegrationTests {
	
	@Autowired UserAccountManager userAccountManager;
	@Autowired CustomerManager customerManager;
	@Autowired BusinessManager businessManager;
	
//	BusinessManager businessManager = new BusinessManager(businessRepo);
//	CustomerManager customerManager = new CustomerManager(customerRepo,userAccountManager,businessManager);
	
	
	@Test
	public void CustomerTests() {
		
		Address deliveryAddress = new Address("Max","Muster","Kantenstrasse","29","91127","Teststadt","Testland");

		Business business = businessManager.createChildcareBusiness("TestBusiness", deliveryAddress, "3333", "4444");
		businessManager.saveBusiness(business);
		
		UserAccount customerAccount = userAccountManager.create("customer1", "123",  Role.of("ROLE_CUSTOMER"));
		userAccountManager.save(customerAccount);
		Customer testCustomer = customerManager.createCustomer(customerAccount,"3333");
		
		assertNotNull("Customer is null",testCustomer);
		assertNull("ExpirationDate not null",testCustomer.getExpirationDate());
		assertEquals("CustomerAccount wrong or null",customerAccount,testCustomer.getUserAccount());
		assertTrue("customer is expired",!testCustomer.isExpired());
		
		try {
		testCustomer.setExpirationDate(LocalDate.of(2015, Month.NOVEMBER, 30));
		}
		catch(IllegalArgumentException e) {
			System.out.print(e);
		}
		
		assertTrue("customer is expired",!testCustomer.isExpired());
	}
	
	@Test
	public void CustomerManagerTests() {
		
		
		assertNotNull("businessRepo is null",businessManager);
		assertNotNull("customerManager is null",customerManager);

		
		assertNotNull("businessManager is null", businessManager);	
		assertNotNull("customerManager is null", customerManager);	
		
		Address deliveryAddress = new Address("Max","Muster","Kantenstrasse","29","91127","Teststadt","Testland");

		Business business = businessManager.createChildcareBusiness("Kita Kunterschwarz", deliveryAddress, "1234", "5678");
		businessManager.saveBusiness(business);
		
		UserAccount customerAccount = userAccountManager.create("customer2", "123",  Role.of("ROLE_CUSTOMER"));
		userAccountManager.save(customerAccount);
		
		
		Customer testCustomer = customerManager.createCustomer(customerAccount,"5678");
		customerManager.saveCustomer(testCustomer);
		
		assertThat(customerManager.findAllCustomers(), is(iterableWithSize(2)));
		assertThat(customerManager.findCustomersByBusiness(business), is(iterableWithSize(1)));
		assertThat(customerManager.findExpiredCustomers(), is(iterableWithSize(0)));
		assertTrue("customer for useraccount not found", customerManager.findCustomerByUserAccount(customerAccount).isPresent());

		//lets check, if the userAccount "customerAccount" is a 
		//customer in a childcare business and is able to perform group orders
		
		Optional<Customer> resultCustomer = customerManager.findCustomerByUserAccount(customerAccount);
		assertTrue("customer for useraccount not found", resultCustomer.isPresent());
		
		business = resultCustomer.get().getBusiness();

		assertTrue("business for customer not found", businessManager.findBusinessByIdentifier(business.getID()).isPresent());
		
		//no fails -> now lets check the business
		
		assertEquals("customer is in a company",BusinessType.CHILDCARE ,resultCustomer.get().getBusiness().getBusinessType());
		
		//customer is in a childcare 
			
		assertEquals("customer is not a chief of the childcare",Discount.CHILDCARE,resultCustomer.get().getDiscount());

		//customer is a chief of a childcare -> is able to perform group orders 
		
	}

}
