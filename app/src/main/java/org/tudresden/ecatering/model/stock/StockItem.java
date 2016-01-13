package org.tudresden.ecatering.model.stock;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
public class StockItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6399772827694738278L;
	
	
	@Id 
	@GeneratedValue 
	@Column(name = "STOCKITEM_ID", insertable = false, updatable = false)
	private long id;
	
	@OneToOne(cascade=CascadeType.DETACH)
	private Grocery grocery;
	
	private double quantity;
	
	private LocalDate expirationDate;
	
	@SuppressWarnings("unused")
	private StockItem(){}
	
	protected StockItem(Grocery grocery, double quantity, LocalDate expirationDate)
	{
		if(grocery==null)
			throw new IllegalArgumentException("Grocery is null!");
		
		if(expirationDate==null)
			throw new IllegalArgumentException("Date is null!");
		
	/*	if(expirationDate.isBefore(LocalDate.now()))
			throw new IllegalArgumentException("wrong/expired expirationDate!");
	*/	
		if(quantity <= 0)
			throw new IllegalArgumentException("Quantity is less or equal 0");

		
		this.grocery = grocery;
		this.quantity = quantity;
		this.expirationDate = expirationDate;
		
	}
	
	public Grocery getGrocery() {
		return grocery;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public LocalDate getExpirationDate() {
		return expirationDate;
	}
	
	public long getID() {
		return id;
	}
	
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(41, 77).
	       append(super.hashCode()).
	       append(grocery).
	       append(quantity).
	       append(expirationDate).
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
