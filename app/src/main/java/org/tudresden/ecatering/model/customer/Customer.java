package org.tudresden.ecatering.model.customer;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.salespointframework.useraccount.UserAccount;

@Entity
public class Customer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4526608006278837720L;
	
	@Id 
	@GeneratedValue 
	@Column(name = "CUSTOMER_ID", insertable = false, updatable = false)
	private long id;
	
	private String businessCode;
	
	private LocalDate expirationDate;
	
	@OneToOne private UserAccount userAccount;

	@SuppressWarnings("unused")
	private Customer(){}
	
	protected Customer(UserAccount userAccount, String businessCode) {
		
		this.userAccount = userAccount;
		this.businessCode = businessCode;
		this.expirationDate = null;
		
	}
	public String getBusinessCode() {
		return businessCode;
	}
	public LocalDate getExpirationDate() {
		return expirationDate;
	}
	
	public UserAccount getUserAccount() {
		return userAccount;
	}
	
	public long getID() {
		return this.id;
	}
	
	public void setExpirationDate(LocalDate expirationDate) {
		
		if(expirationDate!=null && expirationDate.isBefore(LocalDate.now()))
			throw new IllegalArgumentException("wrong/expired expirationDate entered");
			
		
		this.expirationDate = expirationDate;
		
	}
	
	public boolean isExpired(){
		
		if(this.expirationDate==null)
			return false;
		
		return expirationDate.isBefore(LocalDate.now());
	}
	
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(171, 259).
	       append(super.hashCode()).
	       append(businessCode).
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