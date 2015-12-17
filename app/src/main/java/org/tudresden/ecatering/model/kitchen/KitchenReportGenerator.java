package org.tudresden.ecatering.model.kitchen;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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


@Component
@Transactional
public class KitchenReportGenerator implements ReportGenerator<KitchenReport> {

	@Autowired private OrderManager<MealOrder> orderManager;
	@Autowired private MenuItemRepository menuItemRepo;

	
	private KitchenReportGenerator(){}
	
	@Override
	public KitchenReport generateReport(LocalDate date) {
		
		//get orders from 2 weeks ago
		
		
		Map<DayOfWeek,Integer> dayToInt = new HashMap<DayOfWeek,Integer>();		
		dayToInt.put(DayOfWeek.MONDAY, 0);
		dayToInt.put(DayOfWeek.TUESDAY, 1);
		dayToInt.put(DayOfWeek.WEDNESDAY, 2);
		dayToInt.put(DayOfWeek.THURSDAY, 3);
		dayToInt.put(DayOfWeek.FRIDAY, 4);
		dayToInt.put(DayOfWeek.SATURDAY, 5);
		dayToInt.put(DayOfWeek.SUNDAY, 6);
		
		LocalDate mondayDate = date.minusDays(dayToInt.get(date.getDayOfWeek()));
		
		LocalDateTime ordersFrom = LocalDateTime.of(mondayDate.minusWeeks(2).getYear(), mondayDate.minusWeeks(2).getMonth(), mondayDate.minusWeeks(2).getDayOfMonth(), 0, 0);
		LocalDateTime ordersTo = LocalDateTime.of(mondayDate.minusDays(2).getYear(), mondayDate.minusDays(2).getMonth(), mondayDate.minusDays(2).getDayOfMonth(), 0, 0);


		
		//build menuitems with quantities for all orders 
		Map<Recipe,BigDecimal> recipes = new HashMap<Recipe,BigDecimal>();
		
		for(MealOrder order : orderManager.findBy(Interval.from(ordersFrom).to(ordersTo)))
		{
			for(OrderLine line : order.getOrderLines())
			{
				MenuItem item = menuItemRepo.findOne(line.getProductIdentifier()).get();
				
				if(item.getDay().toString().equals(date.getDayOfWeek().toString()))
				{
					Recipe recipe = item.getMeal().getRecipe();
				if(!recipes.containsKey(recipe))
					recipes.put(recipe, line.getQuantity().getAmount().multiply(BigDecimal.valueOf(item.getHelping().getHelpingFactor())));
				else
					recipes.replace(recipe, recipes.get(recipe).add(line.getQuantity().getAmount().multiply(BigDecimal.valueOf(item.getHelping().getHelpingFactor()))));
				}
			
			}
		}
		
		List<Recipe> recipesList = new ArrayList<Recipe>();
		System.out.println("Mahlzeiten mit Menge für: "+date.getDayOfWeek()+", dem "+date);
		for(Recipe recipe : recipes.keySet())
		{
			System.out.println("Mahlzeit: "+recipe.getName()+": \n");
			for(Ingredient ingredient : recipe.getIngredients())
			{
				ingredient.setQuantity(BigDecimal.valueOf(ingredient.getQuantity()).multiply(recipes.get(recipe)).doubleValue());
				
				System.out.println("Zutat: "+ingredient.getGrocery().getName()+", Menge: "+ingredient.getQuantity()+"\n");

			}
			
			recipesList.add(recipe);
			
		}
		
		
		
		
		
		return new KitchenReport(date,recipesList);
	}
	



}
