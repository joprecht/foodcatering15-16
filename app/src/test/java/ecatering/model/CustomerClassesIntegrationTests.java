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
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.business.BusinessRepository;
import org.tudresden.ecatering.model.business.BusinessType;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.customer.CustomerRepository;

import ecatering.AbstractIntegrationTests;

public class CustomerClassesIntegrationTests extends AbstractIntegrationTests {
	
	@Autowired UserAccountManager userAccountManager;
	@Autowired CustomerRepository customerRepo;
	@Autowired BusinessRepository businessRepo;
	
//	BusinessManager businessManager = new BusinessManager(businessRepo);
//	CustomerManager customerManager = new CustomerManager(customerRepo,userAccountManager,businessManager);
	
	
	@Test
	public void CustomerTests() {
		
		UserAccount customerAccount = userAccountManager.create("customer1", "123", new Role("ROLE_CUSTOMER"));
		Customer testCustomer = CustomerManager.createCustomer(customerAccount,"1234-4321");
		
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
		
		
		assertNotNull("businessRepo is null",businessRepo);
		assertNotNull("customerRepo is null",customerRepo);

		BusinessManager businessManager = new BusinessManager(businessRepo);
		CustomerManager customerManager = new CustomerManager(customerRepo,userAccountManager,businessManager);
		
		assertNotNull("businessManager is null", businessManager);	
		assertNotNull("customerManager is null", customerManager);	
		
		Address deliveryAddress = new Address("Max","Muster","Kantenstrasse",29,"91127","Teststadt","Testland");

		Business business = BusinessManager.createChildcareBusiness("Kita Kunterschwarz", deliveryAddress, "1234", "5678");
		businessManager.saveBusiness(business);
		
		UserAccount customerAccount = userAccountManager.create("customer1", "123", new Role("ROLE_CUSTOMER"));
		userAccountManager.save(customerAccount);
		
		
		Customer testCustomer = CustomerManager.createCustomer(customerAccount,"5678");
		customerManager.saveCustomer(testCustomer);
		
		assertThat(customerManager.findAllCustomers(), is(iterableWithSize(1)));
		assertThat(customerManager.findCustomersByBusinessCode("5678"), is(iterableWithSize(1)));
		assertThat(customerManager.findExpiredCustomers(), is(iterableWithSize(0)));
		assertTrue("customer for useraccount not found", customerManager.findCustomerByUserAccount(customerAccount).isPresent());

		//lets check, if the userAccount "customerAccount" is a 
		//customer in a childcare business and is able to perform group orders
		
		Optional<Customer> resultCustomer = customerManager.findCustomerByUserAccount(customerAccount);
		assertTrue("customer for useraccount not found", resultCustomer.isPresent());
		
		String businessCode = resultCustomer.get().getBusinessCode();

		assertTrue("business for customer not found", businessManager.findBusinessByCode(businessCode).isPresent());
		
		//no fails -> now lets check the business
		Business customersBusiness = businessManager.findBusinessByCode(businessCode).get();
		
		assertEquals("customer is in a company",BusinessType.CHILDCARE ,customersBusiness.getBusinessType());
		
		//customer is in a childcare 
			
		assertEquals("customer is not a chief of the childcare",customersBusiness.getInstitutionCode(), resultCustomer.get().getBusinessCode());

		//customer is a chief of a childcare -> is able to perform group orders 
		
	}

}
