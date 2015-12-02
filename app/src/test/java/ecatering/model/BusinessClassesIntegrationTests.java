package ecatering.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.business.BusinessRepository;
import org.tudresden.ecatering.model.business.BusinessType;

import ecatering.AbstractIntegrationTests;

public class BusinessClassesIntegrationTests extends AbstractIntegrationTests {
	
	@Autowired BusinessRepository businessRepo;
	
	
	@Test
	public void businessTests() {
		
	//company business section
		Address deliveryAddress = new Address("Frank","Zappa","Marienstrasse",21,"01307","Dresden","Deutschland");
		
		Business testBusiness = BusinessManager.createCompanyBusiness("Stahlwerk Sonnenschein",deliveryAddress,"1234-5678");
		
		assertNotNull("Business is null", testBusiness);
		assertEquals("name wrong or null", "Stahlwerk Sonnenschein",testBusiness.getName());
		assertEquals("deliveryAddress wrong or null", deliveryAddress,testBusiness.getDeliveryAddress());
		assertEquals("memberCode wrong or null", "1234-5678",testBusiness.getMemberCode());
		assertNull("institutionCode not null", testBusiness.getInstitutionCode());
		assertEquals("businessType wrong or null", BusinessType.COMPANY,testBusiness.getBusinessType());

		Address deliveryAddress2 = new Address("Max","Muster","Kantenstrasse",29,"91127","Teststadt","Testland");
		
		testBusiness.setDeliveryAddress(deliveryAddress2);
		testBusiness.setMemberCode("9876-5432");
		testBusiness.setName("Druckerei Farbenblind");
		try{
		testBusiness.setInstitutionCode("000-not possible-000");
		}
		catch(IllegalAccessError e) {
			System.out.print(e);
		}
		
		assertEquals("name wrong or null", "Druckerei Farbenblind",testBusiness.getName());
		assertEquals("deliveryAddress wrong or null", deliveryAddress2,testBusiness.getDeliveryAddress());
		assertEquals("memberCode wrong or null", "9876-5432",testBusiness.getMemberCode());
		assertNull("institutionCode not null", testBusiness.getInstitutionCode());
		assertEquals("businessType wrong or null", BusinessType.COMPANY,testBusiness.getBusinessType());
		
		
	//childcare business section

		testBusiness = BusinessManager.createChildcareBusiness("Kita Kunterschwarz",deliveryAddress,"1234-5678","1234-5678");

		assertNotNull("Business is null", testBusiness);
		assertEquals("name wrong or null", "Kita Kunterschwarz",testBusiness.getName());
		assertEquals("deliveryAddress wrong or null", deliveryAddress,testBusiness.getDeliveryAddress());
		assertEquals("memberCode wrong or null", "1234-5678",testBusiness.getMemberCode());
		assertEquals("institutionCode wrong or null","1234-5678", testBusiness.getInstitutionCode());
		assertEquals("businessType wrong or null", BusinessType.CHILDCARE,testBusiness.getBusinessType());
		
		testBusiness.setDeliveryAddress(deliveryAddress2);
		testBusiness.setMemberCode("9876-5432");
		testBusiness.setName("Kita Kittchen");
		try{
		testBusiness.setInstitutionCode("000-now possible-000");
		}
		catch(IllegalAccessError e) {
			System.out.print(e);
		}
		
		
		assertEquals("name wrong or null", "Kita Kittchen",testBusiness.getName());
		assertEquals("deliveryAddress wrong or null", deliveryAddress2,testBusiness.getDeliveryAddress());
		assertEquals("memberCode wrong or null", "9876-5432",testBusiness.getMemberCode());
		assertEquals("institutionCode wrong or null","000-now possible-000", testBusiness.getInstitutionCode());
		assertEquals("businessType wrong or null", BusinessType.CHILDCARE,testBusiness.getBusinessType());
		
	}
	
	@Test
	public void businessManagerTests() {
		
		assertNotNull("businessRepo is null",businessRepo);

		BusinessManager manager = new BusinessManager(businessRepo);
		
		assertNotNull("businessManager is null",manager);
		assertThat(manager.findAllBusinesses(), is(iterableWithSize(0)));
		
		Address deliveryAddress = new Address("Frank","Zappa","Marienstrasse",21,"01307","Dresden","Deutschland");	
		Business testBusiness = BusinessManager.createChildcareBusiness("Kita Kunterschwarz",deliveryAddress,"1234-5678","4321-8765");
		
		assertNotNull("Business is null", testBusiness);
		assertEquals("name wrong or null", "Kita Kunterschwarz",testBusiness.getName());
		assertEquals("deliveryAddress wrong or null", deliveryAddress,testBusiness.getDeliveryAddress());
		assertEquals("memberCode wrong or null", "1234-5678",testBusiness.getMemberCode());
		assertEquals("institutionCode wrong or null","4321-8765", testBusiness.getInstitutionCode());
		assertEquals("businessType wrong or null", BusinessType.CHILDCARE,testBusiness.getBusinessType());
		
		manager.saveBusiness(testBusiness);
		
		Address deliveryAddress2 = new Address("Heinrich","Mueller","Stahlstrasse",88,"09990","Valhalla","Fantasia");	
		testBusiness = BusinessManager.createCompanyBusiness("Stahlwerk Sonnenschein",deliveryAddress2,"1234-4321");
		
		assertNotNull("Business is null", testBusiness);
		assertEquals("name wrong or null", "Stahlwerk Sonnenschein",testBusiness.getName());
		assertEquals("deliveryAddress wrong or null", deliveryAddress2,testBusiness.getDeliveryAddress());
		assertEquals("memberCode wrong or null", "1234-4321",testBusiness.getMemberCode());
		assertNull("institutionCode not null", testBusiness.getInstitutionCode());
		assertEquals("businessType wrong or null", BusinessType.COMPANY,testBusiness.getBusinessType());
		
		manager.saveBusiness(testBusiness);
		
		assertThat(manager.findAllBusinesses(), is(iterableWithSize(2)));
		assertThat(manager.findBusinessesByType(BusinessType.CHILDCARE), is(iterableWithSize(1)));
		assertThat(manager.findBusinessesByName("Stahlwerk Sonnenschein"), is(iterableWithSize(1)));
		assertTrue("business not found",manager.findBusinessByCode("1234-4321").isPresent());
		assertTrue("business not found",manager.findBusinessByCode("4321-8765").isPresent());
		assertTrue("fake business found",!manager.findBusinessByCode("4321-8745").isPresent());

		testBusiness = manager.findBusinessByCode("4321-8765").get();
		
		assertNotNull("Business is null", testBusiness);
		assertEquals("name wrong or null", "Kita Kunterschwarz",testBusiness.getName());
		assertEquals("deliveryAddress wrong or null", deliveryAddress,testBusiness.getDeliveryAddress());
		assertEquals("memberCode wrong or null", "1234-5678",testBusiness.getMemberCode());
		assertEquals("institutionCode wrong or null","4321-8765", testBusiness.getInstitutionCode());
		assertEquals("businessType wrong or null", BusinessType.CHILDCARE,testBusiness.getBusinessType());




	}

}
