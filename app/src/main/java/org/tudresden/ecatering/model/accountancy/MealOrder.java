package org.tudresden.ecatering.model.accountancy;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;

@Entity
public class MealOrder extends Order {

	
	private static final long serialVersionUID = -2060482524899570502L;
	
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL) private Address invoiceAddress;
	
	
	@SuppressWarnings({ "deprecation", "unused" })
	private MealOrder() {}
	
	public MealOrder(UserAccount userAccount, PaymentMethod paymentMethod, Address invoiceAddress) {
		
		super(userAccount,paymentMethod);
		this.invoiceAddress = invoiceAddress;
	}
	
	public Address getInvoiceAddress() {
		return invoiceAddress;
	}
	
	public void setInvoiceAddress(Address invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}
	

	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(171, 259).
	       append(super.hashCode()).
	       append(invoiceAddress).
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
