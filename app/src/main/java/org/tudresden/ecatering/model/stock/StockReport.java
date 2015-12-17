package org.tudresden.ecatering.model.stock;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.tudresden.ecatering.model.Report;
import org.tudresden.ecatering.model.kitchen.Ingredient;

@Entity
public class StockReport implements Report, Serializable {
	
	
	@Id 
	@GeneratedValue
	@Column(name = "STOCKREPORT_ID", insertable = false, updatable = false)
	private long id;
	
	private static final long serialVersionUID = 7176204267708077310L;
	
	private LocalDate date;
	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	private List<Ingredient> ingredients = new ArrayList<Ingredient>();
	
	
	@SuppressWarnings("unused")
	private StockReport() {
		
	}
	
	protected StockReport(LocalDate date, List<Ingredient> ingredients)
	{
		this.date = date;
		this.ingredients = ingredients;
	}
	
	
	public List<Ingredient> getIngredients() {
		
		return ingredients;
	}
	
	public long getID() {
		return id;
	}

	@Override
	public LocalDate getReportDate() {
		return date;
	}
	
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(123, 701).
	       append(super.hashCode()).
	       append(date).
	       append(ingredients).
	       append(id).
	       toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		   return ToStringBuilder.reflectionToString(this);
	}	
	

}
