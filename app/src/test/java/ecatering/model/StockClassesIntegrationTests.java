package ecatering.model;

import org.javamoney.moneta.Money;
import org.junit.Test;
import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.stock.Grocery;
import org.tudresden.ecatering.model.stock.StockItem;
import org.tudresden.ecatering.model.stock.StockManager;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDate;

import ecatering.AbstractIntegrationTests;

public class StockClassesIntegrationTests extends AbstractIntegrationTests {
	

	
	@Autowired private StockManager stockManager;

	
	@Test
	public void groceryTests() {
				
		assertNotNull("StockManager is null",stockManager);

		Grocery testGrocery = stockManager.createGrocery("Zucker", Metric.KILOGRAM, Money.of(1.70, EURO));
		
		assertNotNull("testGrocery is null",testGrocery);
		assertEquals("Grocery Name wrong or null","Zucker",testGrocery.getName());
		assertEquals("Grocery Metric wrong or null",Metric.KILOGRAM,testGrocery.getMetric());
		assertEquals("Grocery Price wrong or null",Money.of(1.70, EURO),testGrocery.getPrice());
		
		testGrocery.setPrice(Money.of(2.70, EURO));
		
		assertNotNull("testGrocery is null",testGrocery);
		assertEquals("Grocery Name wrong or null","Zucker",testGrocery.getName());
		assertEquals("Grocery Metric wrong or null",Metric.KILOGRAM,testGrocery.getMetric());
		assertEquals("Grocery Price wrong or null",Money.of(2.70, EURO),testGrocery.getPrice());


		
	}
	
	@Test
	public void stockItemTests() {
		
		assertNotNull("StockManager is null",stockManager);
		
		Grocery grocery = stockManager.createGrocery("Zucker", Metric.KILOGRAM, Money.of(1.70, EURO));
		
		StockItem testStockItem;
		
		//wrong quantity and date check
		try{
			stockManager.createStockItem(grocery, 0, LocalDate.now());
		}
		catch(IllegalArgumentException e) {
			System.out.print(e+"\n");	
		}
		
		try{
			stockManager.createStockItem(grocery, -1.20f, LocalDate.now());
		}
		catch(IllegalArgumentException e) {
			System.out.print(e+"\n");	
		}
		
		try{
			stockManager.createStockItem(grocery, 10f, LocalDate.of(2015, 12, 2));
		}
		catch(IllegalArgumentException e) {
			System.out.print(e+"\n");	
		}
		
		testStockItem = stockManager.createStockItem(grocery, 5.80f, LocalDate.now());
		
		assertNotNull("StockItem is null",testStockItem);
		assertEquals("StockItem Grocery wrong or null",grocery,testStockItem.getGrocery());
		assertEquals(5.80,testStockItem.getQuantity(),0.1);
		assertEquals("StockItem Date wrong or null",LocalDate.now(),testStockItem.getExpirationDate());
		
		
		grocery = stockManager.createGrocery("Milch", Metric.LITER, Money.of(2.70, EURO));

		testStockItem = stockManager.createStockItem(grocery, 1.80f, LocalDate.of(2015,12,30));

		assertNotNull("StockItem is null",testStockItem);
		assertEquals("StockItem Grocery wrong or null",grocery,testStockItem.getGrocery());
		assertEquals(1.80,testStockItem.getQuantity(),0.1);
		assertEquals("StockItem Date wrong or null",LocalDate.of(2015,12,30),testStockItem.getExpirationDate());

		
	}
	
	@Test
	public void stockManagerTests() {
		
		assertNotNull("StockManager is null",stockManager);

	//Grocery save and find part
		
				
		Grocery grocery = stockManager.createGrocery("Rindfleisch", Metric.KILOGRAM, Money.of(1.70, EURO));
		stockManager.saveGrocery(grocery);
		
		assertThat(stockManager.findAllGroceries(), is(iterableWithSize(6)));
		assertThat(stockManager.findGroceriesByMetric(Metric.KILOGRAM), is(iterableWithSize(5)));
		assertThat(stockManager.findGroceriesByMetric(Metric.LITER), is(iterableWithSize(1)));
		assertTrue("Grocery Rindfleisch not found!",stockManager.findGroceryByName("Rindfleisch").isPresent());
		assertTrue("Grocery Lammfleisch found!",!stockManager.findGroceryByName("Lammfleisch").isPresent());

		grocery = stockManager.findGroceryByName("Rindfleisch").get();
		
		assertNotNull("testGrocery is null",grocery);
		assertEquals("Grocery Name wrong or null","Rindfleisch",grocery.getName());
		assertEquals("Grocery Metric wrong or null",Metric.KILOGRAM,grocery.getMetric());
		assertEquals("Grocery Price wrong or null",Money.of(1.70, EURO),grocery.getPrice());
		
		grocery = stockManager.createGrocery("Rotwein", Metric.LITER, Money.of(1.45, EURO));
		stockManager.saveGrocery(grocery);
			
			//try to save a grocery with already used name
		try {
				stockManager.saveGrocery(stockManager.createGrocery("Rindfleisch", Metric.KILOGRAM, Money.of(1.70, EURO)));		
		} catch(IllegalArgumentException e) {
			System.out.print(e+"\n");	

		}
		
	//StockItem save and find part
		
		grocery = stockManager.createGrocery("Spaghetti", Metric.KILOGRAM, Money.of(1.20, EURO));		
		
		StockItem stockItem = stockManager.createStockItem(grocery, 0.200f, LocalDate.now());
		
			//try to save a stockItem with an unknown grocery
		try {
		stockManager.saveStockItem(stockItem);
		} catch(IllegalArgumentException e) {
			System.out.print(e+"\n");
		}
	
		grocery = stockManager.findGroceryByName("Rindfleisch").get();
		stockItem = stockManager.createStockItem(grocery, 0.200f, LocalDate.now());
		stockManager.saveStockItem(stockItem);
		
		stockItem = stockManager.createStockItem(grocery, 1.50f, LocalDate.of(2015,12,20));
		stockManager.saveStockItem(stockItem);

		
		assertThat(stockManager.findAllStockItems(), is(iterableWithSize(2)));
		assertThat(stockManager.findExpiredStockItems(), is(iterableWithSize(0)));
		assertThat(stockManager.findStockItemsByGrocery(grocery), is(iterableWithSize(2)));
		
		stockItem = stockManager.findStockItemsByGrocery(grocery).iterator().next();

		assertNotNull("StockItem is null",stockItem);
		assertEquals("StockItem Grocery wrong or null",grocery,stockItem.getGrocery());
		assertEquals(0.20,stockItem.getQuantity(),0.1);
		assertEquals("StockItem Date wrong or null",LocalDate.now(),stockItem.getExpirationDate());

		
		stockManager.deleteStockItem(stockItem);
		
		assertThat(stockManager.findAllStockItems(), is(iterableWithSize(1)));
		
	



		
	}
	
	
}