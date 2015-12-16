package org.tudresden.ecatering.model.stock;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.DayOfWeek;
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
import org.tudresden.ecatering.model.Report;
import org.tudresden.ecatering.model.ReportGenerator;
import org.tudresden.ecatering.model.accountancy.MealOrder;
import org.tudresden.ecatering.model.kitchen.Ingredient;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MenuItem;
import org.tudresden.ecatering.model.kitchen.MenuItemRepository;
import org.tudresden.ecatering.model.kitchen.Recipe;

@Component
@Transactional
public class StockReportGenerator implements ReportGenerator {
	
	@Autowired private OrderManager<MealOrder> orderManager;
	@Autowired private KitchenManager kitchenManager;
	@Autowired private MenuItemRepository menuItemRepo;


	protected StockReportGenerator()
	{

	}
	
	
	
	@Override
	public Report generateReport(LocalDate date) {
			
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
		LocalDateTime ordersFrom = LocalDateTime.of(reportDate.minusWeeks(1).getYear(), reportDate.minusWeeks(1).getMonth(), reportDate.minusWeeks(1).getDayOfMonth(), 0, 0);
		LocalDateTime ordersTo = LocalDateTime.of(reportDate.minusDays(1).getYear(), reportDate.minusDays(1).getMonth(), reportDate.minusDays(1).getDayOfMonth(), 0, 0);

		//get all ordered Meals 
		Iterable<MealOrder> orders = orderManager.findBy(Interval.from(ordersFrom).to(ordersTo));
		
		//build a map with all recipes and a quantity
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
		
		assertThat(orders,is(iterableWithSize(3)));
		assertThat(groceryMap.keySet(),is(iterableWithSize(4)));
		

		Iterable<Grocery> groceries = groceryMap.keySet();
		Iterator<Grocery> iter4 = groceries.iterator();
		while(iter4.hasNext()) {
		Grocery grocery = iter4.next();	
		System.out.println(grocery.getName()+": "+groceryMap.get(grocery)+"\n");
		
		}
		
		
		
		return null;
		
	}
	

}
