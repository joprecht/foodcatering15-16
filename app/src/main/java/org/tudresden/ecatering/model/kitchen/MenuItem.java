package org.tudresden.ecatering.model.kitchen;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.quantity.Metric;


@Entity
public class MenuItem extends Product {

	
	private static final long serialVersionUID = -3839280035059479617L;
	
	
	
	
	@OneToOne(fetch=FetchType.EAGER,cascade=CascadeType.DETACH)
	private Meal meal;
	
	private Helping helping;
	private Day day;
	
	@SuppressWarnings({ "unused", "deprecation" })
	private MenuItem() {}
	
	protected MenuItem(Meal meal, Money price, Helping helping,Day day) {
		super(meal.getName(),price,Metric.UNIT);
		this.meal = meal;
		this.helping = helping;
		this.day = day;
	}
	
	public Meal getMeal() {
		return meal;
	}
	
	public Day getDay() {
		return day;
	}
	
	public Helping getHelping() {
		return helping;
	}
	
	
		
	

}
