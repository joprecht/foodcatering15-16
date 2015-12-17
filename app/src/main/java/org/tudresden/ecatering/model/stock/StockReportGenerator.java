package org.tudresden.ecatering.model.stock;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManager;
import org.salespointframework.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tudresden.ecatering.model.ReportGenerator;
import org.tudresden.ecatering.model.accountancy.MealOrder;
import org.tudresden.ecatering.model.kitchen.Ingredient;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MenuItem;
import org.tudresden.ecatering.model.kitchen.MenuItemRepository;
import org.tudresden.ecatering.model.kitchen.Recipe;

@Component
@Transactional
public class StockReportGenerator implements ReportGenerator<StockReport> {
	
	@Autowired private OrderManager<MealOrder> orderManager;
	@Autowired private KitchenManager kitchenManager;
	@Autowired private MenuItemRepository menuItemRepo;


	protected StockReportGenerator()
	{

	}
	
	
	
	@Override
	public StockReport generateReport(LocalDate date) {

		
		
		
		LocalDateTime ordersFrom = LocalDateTime.of(date.minusWeeks(1).getYear(), date.minusWeeks(1).getMonth(), date.minusWeeks(1).getDayOfMonth(), 0, 0);
		LocalDateTime ordersTo = LocalDateTime.of(date.minusDays(1).getYear(), date.minusDays(1).getMonth(), date.minusDays(1).getDayOfMonth(), 0, 0);

		//get all ordered Meals 
		Iterable<MealOrder> orders = orderManager.findBy(Interval.from(ordersFrom).to(ordersTo));
		
		//build a map with all groceries and quantities needed for next week
		Map<Grocery,BigDecimal> groceryMap = new HashMap<Grocery,BigDecimal>();
		
		
		Iterator<MealOrder> iter1 = orders.iterator();
		while(iter1.hasNext())
		{
			MealOrder order = iter1.next();
			Iterator<OrderLine> iter2 = order.getOrderLines().iterator();
			while(iter2.hasNext())
			{
				OrderLine orderLine = iter2.next();
				MenuItem item = menuItemRepo.findOne(orderLine.getProductIdentifier()).get();
				Recipe recipe = item.getMeal().getRecipe();
				Iterable<Ingredient> ingredients = recipe.getIngredients();
				Iterator<Ingredient> iter3 = ingredients.iterator();
				while(iter3.hasNext())
				{
					Ingredient ingredient = iter3.next();
					Grocery grocery = ingredient.getGrocery();
					BigDecimal count = groceryMap.containsKey(grocery) ? groceryMap.get(grocery) : BigDecimal.ZERO;
					groceryMap.put(grocery, count.add(BigDecimal.valueOf(ingredient.getQuantity()).multiply(orderLine.getQuantity().getAmount().multiply(BigDecimal.valueOf(item.getHelping().getHelpingFactor())))));

				}
				
				
			}
			
		}
		
		List<Ingredient> ingredients = new ArrayList<Ingredient>();

		Iterable<Grocery> groceries = groceryMap.keySet();
		Iterator<Grocery> iter4 = groceries.iterator();
		System.out.println("StockReport for all Orders "+date+"\n");
		while(iter4.hasNext()) {
		Grocery grocery = iter4.next();	
		System.out.println(grocery.getName()+": "+groceryMap.get(grocery)+"\n");
		
		ingredients.add(kitchenManager.createIngredient(grocery,groceryMap.get(grocery).doubleValue()));
		}
		
		
		
		return new StockReport(date,ingredients);
		
			
	}
	

}
