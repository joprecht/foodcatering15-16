package org.tudresden.ecatering.model.stock;

import java.time.LocalDate;
import java.util.List;

import org.tudresden.ecatering.model.Report;

public class StockReport implements Report{
	
	private LocalDate date;
	private List<Grocery> ingredients;
	
	protected StockReport(LocalDate date, List<Grocery> ingredients)
	{
		this.date = date;
		this.ingredients = ingredients;
	}
	
	
	public List<Grocery> getIngredients() {
		
		return ingredients;
	}

	@Override
	public LocalDate getReportDate() {
		return date;
	}

}
