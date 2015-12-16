package org.tudresden.ecatering.model.stock;

import java.time.LocalDate;
import java.util.List;

import org.tudresden.ecatering.model.Report;
import org.tudresden.ecatering.model.kitchen.Ingredient;

public class StockReport implements Report{
	
	private LocalDate date;
	private List<Ingredient> ingredients;
	
	protected StockReport(LocalDate date, List<Ingredient> ingredients)
	{
		this.date = date;
		this.ingredients = ingredients;
	}
	
	
	public List<Ingredient> getIngredients() {
		
		return ingredients;
	}

	@Override
	public LocalDate getReportDate() {
		return date;
	}

}
