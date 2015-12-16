package org.tudresden.ecatering.model.accountancy;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.tudresden.ecatering.model.customer.Customer;

@Entity
public class MealOrder extends Order {

	
	private static final long serialVersionUID = -2060482524899570502L;
	
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL) private Address invoiceAddress;
	private Discount discount;

	
	@SuppressWarnings({ "deprecation", "unused" })
	private MealOrder() {}
	
	public MealOrder(Customer customer, PaymentMethod paymentMethod, Address invoiceAddress) {
		
		super(customer.getUserAccount(),paymentMethod);
		this.invoiceAddress = invoiceAddress;
		this.discount = customer.getDiscount();
	}
	
	public Address getInvoiceAddress() {
		return invoiceAddress;
	}
	
	public Discount getDiscount() {
		return discount;
	}
	
	public void setInvoiceAddress(Address invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}
	
	@Override
	public Money getTotalPrice() {
		
		return super.getTotalPrice().multiply(discount.getDiscountFactor());
	}
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(171, 259).
	       append(super.hashCode()).
	       append(invoiceAddress).
	       append(discount).
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
