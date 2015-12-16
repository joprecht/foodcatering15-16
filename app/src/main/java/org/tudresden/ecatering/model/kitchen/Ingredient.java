package org.tudresden.ecatering.model.kitchen;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.tudresden.ecatering.model.stock.Grocery;

@Entity
public class Ingredient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4041533978291162531L;
	
	@Id 
	@GeneratedValue 
	@Column(name = "INGREDIENT_ID", insertable = false, updatable = false)
	private long id;
	
	private double quantity;
	
	@OneToOne
	private Grocery grocery;
	
	@SuppressWarnings("unused")
	private Ingredient(){}
	
	protected Ingredient(Grocery grocery, double quantity) {
		
		if(grocery==null)
			throw new IllegalArgumentException("Grocery is null!");
		if(quantity<=0)
			throw new IllegalArgumentException("Quantity is <=0!");
		this.grocery = grocery;
		this.quantity = quantity;
	}
	

	public Grocery getGrocery() {
		return grocery;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public float getID() {
		return id;
	}
	
	public void setQuantity(double quantity) {
		this.quantity=quantity;
	}
	
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(735, 59).
	       append(super.hashCode()).
	       append(grocery).
	       append(quantity).
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
