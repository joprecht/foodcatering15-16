package org.tudresden.ecatering.model.kitchen;

import java.time.LocalDate;
import java.util.List;

import org.tudresden.ecatering.model.Report;

public class KitchenReport implements Report {
	
	private LocalDate date;
	private List<Recipe> recipes;
	
	protected KitchenReport(LocalDate date, List<Recipe> recipes)
	{
		this.date = date;
		this.recipes = recipes;
	}
	
	public List<Recipe> getRecipes() {
		return recipes;
	}

	@Override
	public LocalDate getReportDate() {
		return date;
	}

}
