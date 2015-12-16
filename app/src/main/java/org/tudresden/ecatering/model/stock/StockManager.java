package org.tudresden.ecatering.model.stock;


import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudresden.ecatering.model.ReportGenerator;

@Component
public class StockManager {
	
	
@Autowired private GroceryRepository groceryRepo;
@Autowired private StockItemRepository stockRepo;
@Autowired private ReportGenerator reportGenerator;

//@Autowired	private OrderManager<MealOrder> orderManager;
	
	public StockManager() {
		
	//	assertNotNull("GroceryRepo is null",groceryRepo);
	//	assertNotNull("StockRepo is null",stockRepo);

	}
	
	public Iterable<Grocery> findAllGroceries() {
		
		return this.groceryRepo.findAll();
	}
	
	public Optional<Grocery> findGroceryByName(String name) {
		
		return this.groceryRepo.findByName(name);
	}
	
	public Optional<Grocery> findGroceryByID(long id) {
		Grocery grocery = this.groceryRepo.findOne(id);
			if(grocery!=null)
				return Optional.of(grocery);
		
		return Optional.empty();
	}
	
	public Iterable<Grocery> findGroceriesByMetric(Metric metric) {
		
		return this.groceryRepo.findByMetric(metric);
	}
	
	
	public Iterable<StockItem> findAllStockItems() {
			
		return this.stockRepo.findAll();		
	}
	
	public Iterable<StockItem> findStockItemsByGrocery(Grocery grocery) {
		
		return this.stockRepo.findByGrocery(grocery);		
	}
	
	public Iterable<StockItem> findExpiredStockItems() {
		
		Iterable<StockItem> result = this.findAllStockItems();
		Iterator<StockItem> iter = result.iterator();
		
		while(iter.hasNext())
		{
			if(!iter.next().getExpirationDate().isBefore(LocalDate.now()))
			 iter.remove();
		}
		
		return result;
	}
	
	public void getStockReportForDate(LocalDate date) {
		reportGenerator.generateReport(date);
	}
	
	public Grocery createGrocery(String name, Metric metric, Money price) {
		
		if(this.findGroceryByName(name).isPresent())
			throw new IllegalArgumentException("Another Grocery with this name already exists!");

		
		return new Grocery(name,metric,price);
	}
	
	public StockItem createStockItem(Grocery grocery,double quantity,LocalDate expirationDate) {
		
		return new StockItem(grocery,quantity,expirationDate);
	}
	
	public Grocery saveGrocery(Grocery grocery) {
				
		return this.groceryRepo.save(grocery);
	}
	
	public StockItem saveStockItem(StockItem stockItem) {
		
		if(!this.findGroceryByName(stockItem.getGrocery().getName()).isPresent())
			throw new IllegalArgumentException("Grocery does not exists!");
				
		return this.stockRepo.save(stockItem);
	
	}
	
	
	public void deleteStockItem(StockItem stockItem) {
		
		this.stockRepo.delete(stockItem);
		return;
	}
	
	

}
