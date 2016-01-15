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


//	@RequestMapping("/stock")
//	public String stockMethodsForMap(ModelMap modelMap) {
//
//		modelMap.addAttribute("allIngredients", stockManager.findAllStockItems());
//
//		return "stock";
//	}
	
	//TODO Needs new HTML
	@RequestMapping("/expirationReport")
	public String expirationReport(ModelMap modelMap){
		
		modelMap.addAttribute("expiredIngredients", stockManager.findExpiredStockItems());
		
		return "expirationReport";
	}
	
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
		
		return "redirect:/expirationReport";
	}
	
	//TODO Needs new HTML
	@RequestMapping("/createGrocery")
	public String createGrocery(ModelMap modelMap){
		modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
		return "createGrocery";
	}
	
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
	
	//TODO Needs new HTML
	@RequestMapping("/listGrocery")
	public String listGrocery(ModelMap modelMap){
		modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
		return "listGrocery";
	}
	
	
	//TODO Needs new HTML
		@RequestMapping("/addStock")
		public String addStock(){
			return "addStock";
		}
	
	@RequestMapping(value = "/newStock", method = RequestMethod.POST)
	public String newStock(@RequestParam("name") String name,
							 @RequestParam("quantity") Double quantity,
							 @RequestParam("YYYY") Integer year,
							 @RequestParam("MM") Integer month,
							 @RequestParam("DD") Integer day){
		
		if(name.isEmpty()||quantity.isNaN()){
			
		}
		
		
		//Get the correct Grocery
		Optional<Grocery> gro = stockManager.findGroceryByName(name);
		Grocery gro2 = gro.get();
		
		//Save the new amount with the corresponding expiration Date
		stockManager.saveStockItem(stockManager.createStockItem(gro2, quantity, LocalDate.of(year, month, day)));
		
		return "redirect:/inventory";
	}
	
	//TODO order management required for further work
		@RequestMapping("/inventory")
		public String orderReport(ModelMap modelMap){
			//create modelMap and fill with required Groceries based on Orders
			LocalDate date = LocalDate.now();
			modelMap.addAttribute("requiredStockItems", stockManager.getStockReportForDate(date));
			modelMap.addAttribute("allStockItems", stockManager.findAllStockItems());
			modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
			modelMap.addAttribute("expiredIngredients", stockManager.findExpiredStockItems());

			return "inventory";
		}
		
		//changePrice
		@RequestMapping(value = "/changePrice",method= RequestMethod.POST)
		public String changePrice(@RequestParam("grocery") String grocery,
								  @RequestParam("price") Double price){
			
			Grocery gro = stockManager.findGroceryByName(grocery).get();
			gro.setPrice(Money.of(price, EURO));
			stockManager.saveGrocery(gro);
			
			return "redirect:/inventory";
		}

}
