package org.tudresden.ecatering.model.stock;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.salespointframework.order.OrderManager;
import org.tudresden.ecatering.model.Report;
import org.tudresden.ecatering.model.ReportGenerator;
import org.tudresden.ecatering.model.accountancy.MealOrder;

public class StockReportGenerator implements ReportGenerator {
	
	private final OrderManager<MealOrder> orderManager;

	public StockReportGenerator(OrderManager<MealOrder> orderManager)
	{
		this.orderManager = orderManager;
	}
	
	
	
	@Override
	public Report generateReport() {
			
		//new report each monday of week 
		
		Map<DayOfWeek,Integer> dayToInt = new HashMap<DayOfWeek,Integer>();		
		dayToInt.put(DayOfWeek.MONDAY, 0);
		dayToInt.put(DayOfWeek.TUESDAY, 1);
		dayToInt.put(DayOfWeek.WEDNESDAY, 2);
		dayToInt.put(DayOfWeek.THURSDAY, 3);
		dayToInt.put(DayOfWeek.FRIDAY, 4);
		dayToInt.put(DayOfWeek.SATURDAY, 5);
		dayToInt.put(DayOfWeek.SUNDAY, 6);
		
		LocalDate reportDate = LocalDate.now().minusDays(dayToInt.get(LocalDate.now().getDayOfWeek()));
		
		
		//get all ordered Meals 
		

		
		return null;
	}
	

}
