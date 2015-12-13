package ecatering.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;


import java.time.LocalDateTime;

import org.junit.Test;
import org.salespointframework.order.OrderManager;
import org.salespointframework.time.Interval;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.accountancy.MealOrder;

import org.tudresden.ecatering.model.accountancy.Transfer;

import ecatering.AbstractIntegrationTests;





public class AccountancyClassesIntegrationTests extends AbstractIntegrationTests {
	
	@Autowired UserAccountManager userManager;
	@Autowired OrderManager<MealOrder> orderManager;
	
	@Test
	public void addressTests() {
		
		Address testAddress = new Address("Frank","Zappa","Marienstrasse","21","01307","Dresden","Deutschland");
		
		assertNotNull("Address is null", testAddress);
		assertEquals("wrong or null firstname", "Frank", testAddress.getFirstname());
		assertEquals("wrong or null lastname", "Zappa", testAddress.getLastname());
		assertEquals("wrong or null streetname", "Marienstrasse", testAddress.getStreet());
		assertEquals("wrong or null streetnumber", "21", testAddress.getStreetNumber());
		assertEquals("wrong or null postalcode", "01307", testAddress.getPostalCode());
		assertEquals("wrong or null city", "Dresden", testAddress.getCity());
		assertEquals("wrong or null country", "Deutschland", testAddress.getCountry());
		
	}
	
	@Test
	public void mealOrderTests() {
		
		UserAccount testAccount = userManager.create("customer", "123",  Role.of("ROLE_CUSTOMER"));
		userManager.save(testAccount);
		Address testAddress = new Address("Frank","Zappa","Marienstrasse","21","01307","Dresden","Deutschland");				
		MealOrder mealOrder = new MealOrder(testAccount, new Transfer("blablabla"), testAddress);
		
		
		//just some tests to see , how order works
		assertNotNull("MealOrder is null", mealOrder);
		assertTrue("mealOrder is not new",mealOrder.isNew());
		assertTrue("mealOrder is canceled",!mealOrder.isCanceled());
		assertTrue("mealOrder is completed", !mealOrder.isCompleted());
		assertTrue("mealOrder is not open", mealOrder.isOpen());
		assertTrue("mealOrder is paid", !mealOrder.isPaid());
		assertEquals("wrong or null address", testAddress, mealOrder.getInvoiceAddress());
		

		orderManager.save(mealOrder);
		
		assertTrue("Mealorder with id not found",orderManager.get(mealOrder.getIdentifier()).isPresent());
		

		mealOrder = orderManager.get(mealOrder.getIdentifier()).get();
		
		
		assertNotNull("MealOrder is null", mealOrder);
		assertTrue("mealOrder is new",!mealOrder.isNew());
		assertTrue("mealOrder is canceled",!mealOrder.isCanceled());
		assertTrue("mealOrder is completed", !mealOrder.isCompleted());
		assertTrue("mealOrder is not open", mealOrder.isOpen());
		assertTrue("mealOrder is paid", !mealOrder.isPaid());
		assertEquals("wrong or null address", testAddress, mealOrder.getInvoiceAddress());
		
		assertThat(orderManager.findBy(Interval.from(LocalDateTime.now().minusMinutes(12)).to(LocalDateTime.now().minusMinutes(1))), is(iterableWithSize(0)));
		
		//check for paymentMethod
		
		assertTrue(mealOrder.getPaymentMethod() instanceof Transfer);
		
		
	}

}
