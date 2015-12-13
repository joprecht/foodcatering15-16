package org.tudresden.ecatering.model.accountancy;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.salespointframework.payment.PaymentMethod;

public class Transfer extends PaymentMethod {

	private static final long serialVersionUID = 1L;
	

	public Transfer(String description) {
		super(description);
	}
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(263, 855).
	       append(super.hashCode()).
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