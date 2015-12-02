package ecatering.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.tudresden.ecatering.model.accountancy.Address;

import ecatering.AbstractIntegrationTests;

public class AccountancyClassesIntegrationTests extends AbstractIntegrationTests {
	
	
	@Test
	public void addressTests() {
		
		Address testAddress = new Address("Frank","Zappa","Marienstrasse",21,"01307","Dresden","Deutschland");
		
		assertNotNull("Address is null", testAddress);
		assertEquals("wrong or null firstname", "Frank", testAddress.getFirstname());
		assertEquals("wrong or null lastname", "Zappa", testAddress.getLastname());
		assertEquals("wrong or null streetname", "Marienstrasse", testAddress.getStreet());
		assertEquals("wrong or null streetnumber", 21, testAddress.getStreetNumber());
		assertEquals("wrong or null postalcode", "01307", testAddress.getPostalCode());
		assertEquals("wrong or null city", "Dresden", testAddress.getCity());
		assertEquals("wrong or null country", "Deutschland", testAddress.getCountry());
		
	}

}
