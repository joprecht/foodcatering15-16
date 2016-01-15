package org.tudresden.ecatering.frontend;


import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.stock.Grocery;
import org.tudresden.ecatering.model.stock.StockItem;
import org.tudresden.ecatering.model.stock.StockManager;


@Controller
@PreAuthorize("hasRole('ROLE_STOCK')||hasRole('ROLE_ACCOUNTING')")
class StockController {

	private final StockManager stockManager;

	@Autowired
	public StockController(StockManager stockManager) {

		this.stockManager = stockManager;
	}

	
//	/**
//	 * Controller to display all expired Stock Items
//	 * 
//	 * @param modelMap Required for Thymeleaf
//	 * @return expirationReport.html
//	 */
//	@RequestMapping("/expirationReport")
//	public String expirationReport(ModelMap modelMap){
//		
//		modelMap.addAttribute("expiredIngredients", stockManager.findExpiredStockItems());
//		
//		return "expirationReport";
//	}
	
	/**
	 * Controller to remove all Expired StockItems
	 * 
	 * @return inventory
	 */
	@RequestMapping("/removeExpiredStock")
	public String removeExpiredStock(){
		
		//get all expired Stock items
		Iterable<StockItem> expired = stockManager.findExpiredStockItems();
		Iterator<StockItem> iter = expired.iterator();
		
		//Delete all expired Stock items
		while(iter.hasNext())
		{
				stockManager.deleteStockItem(iter.next());
		}
		
		return "redirect:/inventory";
	}
	
	/**
	 * Controller to create a new Grocery
	 * as well as listing all existing ones
	 * 
	 * @param modelMap Required for Thymeleaf
	 * @return createGrocery.html
	 */
	@RequestMapping("/createGrocery")
	public String createGrocery(ModelMap modelMap){
		modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
		return "createGrocery";
	}
	
	/**
	 * Controller to actually save the Grocery created in /createGrocery
	 * 
	 * @param name Name of the new Grocery
	 * @param metric metric the grocery is measured in (Unit/Liter/Kilo)
	 * @param price Price of the Grocery per full metric
	 * @return
	 */
	@RequestMapping(value = "/saveGrocery", method = RequestMethod.POST)
	public String saveGrocery(@RequestParam("name") String name,
							  @RequestParam("metric") String metric,
							  @RequestParam("price") Double price){
		
		//Go through all metrics to find the matching one
		for(Metric m : Metric.values())
	    {
	      if(m.name().contains(metric))
	      {
	    	  //Save the Grocery with the right metric 
	    	  stockManager.saveGrocery(stockManager.createGrocery(name, m, Money.of(price, EURO)));
	      }
	    }
		
		//Show the page to create another grocery
		return "redirect:/createGrocery";
	}
	
	
//	@RequestMapping("/listGrocery")
//	public String listGrocery(ModelMap modelMap){
//		modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
//		return "listGrocery";
//	}
	
	
	
//		@RequestMapping("/addStock")
//		public String addStock(){
//			return "addStock";
//		}
	
		/**
		 * Controller to save new StockItems
		 * 
		 * @param name Name of the Grocery that is being stocked
		 * @param quantity Quantity added
		 * @param year Expiration Year
		 * @param month Expiration Month
		 * @param day Expiration Day
		 * @return inventory.html
		 */
		@RequestMapping(value = "/newStock", method = RequestMethod.POST)
		public String newStock(@RequestParam("name") String name,
							 @RequestParam("quantity") Double quantity,
							 @RequestParam("YYYY") Integer year,
							 @RequestParam("MM") Integer month,
							 @RequestParam("DD") Integer day){
		
		
		//Get the correct Grocery
		Optional<Grocery> gro = stockManager.findGroceryByName(name);
		Grocery gro2 = gro.get();
		
		//Save the new amount with the corresponding expiration Date
		stockManager.saveStockItem(stockManager.createStockItem(gro2, quantity, LocalDate.of(year, month, day)));
		
		return "redirect:/inventory";
	}
	
		/**
		 * Controller to display all interesting numbers of the Stock
		 * The Weekly Report
		 * All StockItems and groceries
		 * All expired StockItems
		 * All non expired StockItems
		 * 
		 * @param modelMap Required for Thymeleaf
		 * @return inventory.html
		 */
		@RequestMapping("/inventory")
		public String orderReport(ModelMap modelMap){
			//create modelMap and fill with required Groceries based on Orders
			LocalDate date = LocalDate.now();
			modelMap.addAttribute("requiredStockItems", stockManager.getStockReportForDate(date));
			modelMap.addAttribute("allStockItems", stockManager.findAllStockItems());
			modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
			modelMap.addAttribute("stockItems", stockManager.findNonExpiredStockItems());
			modelMap.addAttribute("expiredIngredients", stockManager.findExpiredStockItems());
			

			return "inventory";
		}
		
		/**
		 * Controller to change the Price of a grocery
		 * 
		 * @param grocery The grocery to be updated
		 * @param price The new Price
		 * @return inventory.html
		 */
		@RequestMapping(value = "/setGroceryPrice", method = RequestMethod.POST)
		public String setGroceryPrice(@RequestParam("grocery") String name,
									@RequestParam("price") String price)
								  {
			
			try{
				Double priceValue = Double.valueOf(price);
				Grocery grocery = stockManager.findGroceryByName(name).get();
				grocery.setPrice(Money.of(priceValue, EURO));
				stockManager.saveGrocery(grocery);
			}catch(Exception e)
			{
				System.out.println(e+"\n");
				return "redirect:/inventory";
			}
			
			return "redirect:/inventory";
		}

}
