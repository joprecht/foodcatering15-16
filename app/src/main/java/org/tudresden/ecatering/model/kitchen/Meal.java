package org.tudresden.ecatering.model.kitchen;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

@Entity
public class Meal extends Product {

	
	private static final long serialVersionUID = 8652543642342345647L;
	

	private MealType type;
	private Helping helping;
	
	@SuppressWarnings({ "unused", "deprecation" })
	private Meal() {}
	
	protected Meal(String name, Money price,MealType type) {
		
		super(name,price);
		this.type = type;
		this.helping = Helping.REGULAR;
		
	}
	
	//getter
	public MealType getMealType() {
		return this.type;
	}
	
	public Money getPrice() {
		return super.getPrice().multiply(this.helping.getHelpingFactor());
		
	}
	
	public Helping getHelping() {
		return this.helping;
	}
	
	//setter
	public void setHelping(Helping helping) {
		this.helping = helping;
	}
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(11, 39).
	       append(super.hashCode()).
	       append(type).
	       append(helping).
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
