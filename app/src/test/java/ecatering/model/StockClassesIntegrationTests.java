package ecatering.model;

import org.javamoney.moneta.Money;
import org.junit.Test;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.stock.Ingredient;
import org.tudresden.ecatering.model.stock.IngredientRepository;
import org.tudresden.ecatering.model.stock.StockManager;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.*;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDate;

import ecatering.AbstractIntegrationTests;

public class StockClassesIntegrationTests extends AbstractIntegrationTests {
	
	
	@Autowired private IngredientRepository ingredientRepo;


	
	@Test
	public void ingredientTests() {
			
		Ingredient testIngredient = StockManager.createIngredient("Zucker", Money.of(1.20, EURO), Quantity.of(0.500, Metric.KILOGRAM));
		
		assertNotNull("Ingredient is null",testIngredient);
		assertEquals("Name wrong or null","Zucker",testIngredient.getProduct().getName());
		assertEquals("Quantity wrong or null",Quantity.of(0.500, Metric.KILOGRAM),testIngredient.getQuantity());
		assertEquals("Money wrong or null",Money.of(1.20, EURO),testIngredient.getProduct().getPrice());
		assertEquals("Expiration date is not null",null,testIngredient.getExpirationDate());
		
		testIngredient = StockManager.createIngredient("Quark", Money.of(0.39, EURO), Quantity.of(0.250, Metric.KILOGRAM), LocalDate.of(2015, 12, 28));
		assertNotNull("Ingredient is null",testIngredient);
		assertEquals("Name wrong or null","Quark",testIngredient.getProduct().getName());
		assertEquals("Quantity wrong or null",Quantity.of(0.250, Metric.KILOGRAM),testIngredient.getQuantity());
		assertEquals("Money wrong or null",Money.of(0.39, EURO),testIngredient.getProduct().getPrice());
		assertEquals("Expiration date is wrong or null",LocalDate.of(2015, 12, 28),testIngredient.getExpirationDate());
	}
	
	
	
	@Test
	public void stockManagerTests() {
		
		assertNotNull("Ingredient Repo is null", ingredientRepo);

		StockManager manager = new StockManager(ingredientRepo);
		
		assertNotNull("StockManager is null", manager);
		
		
		//with ingredients from dataInitializer
		assertThat(manager.findAllIngredients(), is(iterableWithSize(3)));		
		assertThat(manager.findExpiredIngredients(), is(iterableWithSize(0)));
		assertThat(manager.findIngredientsByName("Tomatensauce"), is(iterableWithSize(1)));

		
		Ingredient testIngredient = StockManager.createIngredient("Salz", Money.of(0.90, EURO), Quantity.of(0.250, Metric.KILOGRAM));
		assertNotNull("Ingredient is null",testIngredient);
		assertEquals("Name wrong or null","Salz",testIngredient.getProduct().getName());
		assertEquals("Quantity wrong or null",Quantity.of(0.250, Metric.KILOGRAM),testIngredient.getQuantity());
		assertEquals("Money wrong or null",Money.of(0.90, EURO),testIngredient.getProduct().getPrice());
		assertEquals("Expiration date is not null",null,testIngredient.getExpirationDate());

		testIngredient = StockManager.createIngredient("Schokopudding", Money.of(1.20, EURO), Quantity.of(0.150, Metric.KILOGRAM), LocalDate.now());
		assertNotNull("Ingredient is null",testIngredient);
		assertEquals("Name wrong or null","Schokopudding",testIngredient.getProduct().getName());
		assertEquals("Quantity wrong or null",Quantity.of(0.150, Metric.KILOGRAM),testIngredient.getQuantity());
		assertEquals("Money wrong or null",Money.of(1.20, EURO),testIngredient.getProduct().getPrice());
		assertEquals("Expiration wrong or null",LocalDate.now(),testIngredient.getExpirationDate());
		
		manager.saveIngredient(testIngredient);
		
		assertTrue("Ingredient with ID not found",manager.findIngredientByIdentifier(testIngredient.getIdentifier()).isPresent());
		assertThat(manager.findIngredientsByName("Schokopudding"), is(iterableWithSize(1)));

	}

}