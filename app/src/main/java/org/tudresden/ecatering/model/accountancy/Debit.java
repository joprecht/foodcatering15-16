package org.tudresden.ecatering.model.accountancy;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.salespointframework.payment.PaymentMethod;

public class Debit extends PaymentMethod {
	
	
	private static final long serialVersionUID = -974591388020652281L;
	
	
	
	private String iban;
	private String bic;
	public Debit(String description, String iban, String bic) {
		super(description);
		this.iban = iban;
		this.bic = bic;
	}
	public String getIban() {
		return iban;
	}
	public String getBic() {
		return bic;
	}
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(423, 197).
	       append(super.hashCode()).
	       append(iban).
	       append(bic).
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