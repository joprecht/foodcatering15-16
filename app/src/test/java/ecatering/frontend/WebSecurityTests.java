package ecatering.frontend;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;

import ecatering.AbstractWebIntegrationTests;


public class WebSecurityTests extends AbstractWebIntegrationTests {


	
	//Tests if all views are protected from Non users
	@Test
	public void checkForPermissionTests() throws Exception {

		mvc.perform(get("/showPlan")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/cart")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/changeExpirationDate")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/UserAccount")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/UserAccount")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/createRecipe")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/kitchenReport")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/createGrocery")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/addStock")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/inventory")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/createBusiness")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/createMeal")). 
				andExpect(status().isFound()). 
				andExpect(header().string("Location", endsWith("/login")));
		
		mvc.perform(get("/createPlan")). 
		andExpect(status().isFound()). 
		andExpect(header().string("Location", endsWith("/login")));
		
		
	}


	@Test
	public void accountingControllerPermissionTests() throws Exception {

		mvc.perform(get("/retrieveVacantPositions").with(user("boss").roles("ACCOUNTING"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("retrieveVacantPositions")). 
				andExpect(model().attributeExists("allVacantPostions"));
		
		mvc.perform(get("/createBusiness").with(user("boss").roles("ACCOUNTING"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("createBusiness")). 
				andExpect(model().attributeExists("allBusinesses"));
		
		mvc.perform(get("/createMeal").with(user("boss").roles("ACCOUNTING"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("createMeal")). 
				andExpect(model().attributeExists("allVacantPostions"));
	}
	
	@Test
	public void customerControllerTests() throws Exception{
		
		mvc.perform(get("/changeExpirationDate").with(user("kunde").roles("CUSTOMER"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("changeExpirationDate"));
		
		mvc.perform(get("/UserAccount").with(user("kunde").roles("CUSTOMER"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("userAccount")). 
				andExpect(model().attributeExists("user"));
	}
	
	@Test
	public void cartControllerTests() throws Exception{
		
		mvc.perform(get("/cart").with(user("kunde").roles("CUSTOMER"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("cart")). 
				andExpect(model().attributeExists("address"));
		
		mvc.perform(get("/showPlan").with(user("kunde").roles("CUSTOMER"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("showPlan")). 
				andExpect(model().attributeExists("currentWeek"));
	}
	
	@Test
	public void kitchenControllerTests() throws Exception{
		
		mvc.perform(get("/kitchen").with(user("koch").roles("KITCHEN"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("kitchen"));
		
		mvc.perform(get("/createRecipe").with(user("koch").roles("KITCHEN"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("createRecipe")). 
				andExpect(model().attributeExists("allRecipes")). 
				andExpect(model().attributeExists("allGroceries"));
		
		mvc.perform(get("/kitchenReport").with(user("koch").roles("KITCHEN"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("kitchenReport")). 
				andExpect(model().attributeExists("kitchenReport"));
	}
	
	@Test
	public void stockControllerTests() throws Exception {
		
		mvc.perform(get("/createGrocery").with(user("lager").roles("STOCK"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("createGrocery")). 
				andExpect(model().attributeExists("allGroceries"));
		
		mvc.perform(get("/addStock").with(user("lager").roles("STOCK"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("addStock"));
		
		mvc.perform(get("/inventory").with(user("lager").roles("STOCK"))). 
				andExpect(status().isOk()). 
				andExpect(view().name("inventory")). 
				andExpect(model().attributeExists("requiredStockItems")). 
				andExpect(model().attributeExists("allStockItems"));
	}
}