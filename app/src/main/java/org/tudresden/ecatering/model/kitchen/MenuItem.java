package org.tudresden.ecatering.model.kitchen;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.javamoney.moneta.Money;
import static org.salespointframework.core.Currencies.*;

@Entity
public class MenuItem implements Serializable {

	
	private static final long serialVersionUID = -3839280035059479617L;
	
	@Id 
	@GeneratedValue
	@Column(name = "MENUITEM_ID", insertable = false, updatable = false)
	private long id;
	
	
	private double price;
	@OneToOne(fetch=FetchType.EAGER,cascade=CascadeType.DETACH)
	private Meal meal;
	
	@SuppressWarnings("unused")
	private MenuItem() {}
	
	protected MenuItem(Meal meal) {
		this.meal = meal;
		this.price = meal.getMealPriceForHelping(Helping.REGULAR).getNumber().doubleValue();
	}
	
	public Meal getMeal() {
		return meal;
	}
	
	public Money getMenuPriceForHelping(Helping helping) {
		return Money.of(price*helping.getHelpingFactor(), EURO);
	}
	
	public long getID() {
		return id;
	}
	
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(611, 99).
	       append(super.hashCode()).
	       append(meal).
	       append(price).
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
