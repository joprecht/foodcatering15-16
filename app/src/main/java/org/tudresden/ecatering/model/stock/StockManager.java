package org.tudresden.ecatering.model.stock;



import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javamoney.moneta.Money;

import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudresden.ecatering.model.ReportGenerator;
import org.tudresden.ecatering.model.kitchen.Ingredient;

@Component
public class StockManager {
	
	
@Autowired private GroceryRepository groceryRepo;
@Autowired private StockItemRepository stockRepo;
@Autowired private ReportGenerator<StockReport> reportGenerator;
@Autowired private StockReportRepository reportRepo;

	
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
			
		return this.sortStockItemsByExpirationDate(this.stockRepo.findAll());		
	}
	
	public Iterable<StockItem> findStockItemsByGrocery(Grocery grocery) {
		
		return this.sortStockItemsByExpirationDate(this.stockRepo.findByGrocery(grocery));		
	}
	
	public Iterable<StockItem> findExpiredStockItems() {
		
		Iterable<StockItem> result = this.findAllStockItems();
		Iterator<StockItem> iter = result.iterator();
		
		while(iter.hasNext())
		{
			if(!iter.next().getExpirationDate().isBefore(LocalDate.now()))
			 iter.remove();
		}
		
		return this.sortStockItemsByExpirationDate(result);
	}
	
public Iterable<StockItem> findNonExpiredStockItems() {
		
		Iterable<StockItem> result = this.findAllStockItems();
		Iterator<StockItem> iter = result.iterator();
		
		while(iter.hasNext())
		{
			if(iter.next().getExpirationDate().isBefore(LocalDate.now()))
			 iter.remove();
		}
		
		return this.sortStockItemsByExpirationDate(result);
	}
	
	public StockReport getStockReportForDate(LocalDate date) {
		
		//new report each monday of week 
		
				Map<DayOfWeek,Integer> dayToInt = new HashMap<DayOfWeek,Integer>();		
				dayToInt.put(DayOfWeek.MONDAY, 0);
				dayToInt.put(DayOfWeek.TUESDAY, 1);
				dayToInt.put(DayOfWeek.WEDNESDAY, 2);
				dayToInt.put(DayOfWeek.THURSDAY, 3);
				dayToInt.put(DayOfWeek.FRIDAY, 4);
				dayToInt.put(DayOfWeek.SATURDAY, 5);
				dayToInt.put(DayOfWeek.SUNDAY, 6);
				
				LocalDate reportDate = date.minusDays(dayToInt.get(date.getDayOfWeek()));		
				
		
		if(!reportRepo.findByDate(reportDate).isPresent())
		//save temp
			reportRepo.save(reportGenerator.generateReport(reportDate));
		
		
		if(reportRepo.findByDate(reportDate.minusWeeks(1)).isPresent())
		{
			//put all ordered groceries from last week to kitchen once by FIFO
			StockReport report =reportRepo.findByDate(reportDate.minusWeeks(1)).get();
			this.removeItemsFromStockByStockReport(report);
			reportRepo.delete(report);
		}
		
		
		StockReport report = reportRepo.findByDate(reportDate).get();

		
		//calculate , how much groceries have to be ordered

		Iterator<Ingredient> iter1 = report.getIngredients().iterator();
		while(iter1.hasNext())
		{
			Ingredient ingredient = iter1.next();
			Iterable<StockItem> stockItems = this.findStockItemsByGrocery(ingredient.getGrocery());
			Iterator<StockItem> iter2 = stockItems.iterator();
			while(iter2.hasNext())
			{
				StockItem stockItem = iter2.next();
				if(stockItem.getQuantity()>=ingredient.getQuantity())
				{
					iter1.remove();
				}
				else
				{
					ingredient.setQuantity(BigDecimal.valueOf(ingredient.getQuantity()).subtract(BigDecimal.valueOf(stockItem.getQuantity())).doubleValue());
				}
			}
		}
		
		System.out.println("Es muss bestellt werden:\n");
		for(Ingredient in : report.getIngredients())
		{
			System.out.println(in.getGrocery().getName()+": "+in.getQuantity()+"\n");

		}
		
		return report;
		
	}
	
private Iterable<StockItem> sortStockItemsByExpirationDate(Iterable<StockItem> stockItems)
{
	List<StockItem> sortedStockItems = new ArrayList<StockItem>();

	//sort stock items by date
	for(StockItem item : stockItems)
	{
		sortedStockItems.add(item);
	}
	
	Collections.sort(sortedStockItems, new Comparator<StockItem>(){
		public int compare(StockItem item1, StockItem item2)
		{
			
			return item1.getExpirationDate().isAfter(item2.getExpirationDate()) ? 1 : item1.getExpirationDate().isBefore(item2.getExpirationDate()) ? -1 : 0;
			
		}		
	});
	
	return sortedStockItems;
}
	
private void removeItemsFromStockByStockReport(StockReport report) {
	
	
	

		for(Ingredient ingredient : report.getIngredients())
		{
			for(StockItem item : this.findStockItemsByGrocery(ingredient.getGrocery()))
				if(item.getQuantity()<=ingredient.getQuantity())
				{
					ingredient.setQuantity(BigDecimal.valueOf(ingredient.getQuantity()).subtract(BigDecimal.valueOf(item.getQuantity())).doubleValue());
					this.deleteStockItem(item);
				}
				else if(ingredient.getQuantity()!=0) 
				{
					item.setQuantity(BigDecimal.valueOf(item.getQuantity()).subtract(BigDecimal.valueOf(ingredient.getQuantity())).doubleValue());
					this.saveStockItem(item);
					ingredient.setQuantity(0);
					}
				
		}
		
		System.out.println("Aktuelles Lager nach Entnahme aller Lebensmittel für diese Woche:\n");
		for(StockItem item : this.findAllStockItems())
		{
			
			System.out.println(item.getGrocery().getName()+", Ablaufdatum "+item.getExpirationDate()+" Menge:"+item.getQuantity()+"\n");

		}
	
	
	
	
	
	
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
