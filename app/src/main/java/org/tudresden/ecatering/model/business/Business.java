package org.tudresden.ecatering.model.business;

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
import org.tudresden.ecatering.model.accountancy.Address;



@Entity
public class Business implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue 
	@Column(name = "BUSINESS_ID", insertable = false, updatable = false)
	private long id;
	
	
	private String name, memberCode, institutionCode;
	private BusinessType type;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL) 
	private Address deliveryAddress;
	
	
	@SuppressWarnings("unused")
	private Business(){}
	
	protected Business(String name, Address deliveryAddress, String memberCode) {
		
		this(name,deliveryAddress,memberCode,null);
		this.type = BusinessType.COMPANY;
	}
	
	protected Business(String name, Address deliveryAddress, String memberCode, String institutionCode) {
		this.name = name;
		this.deliveryAddress = deliveryAddress;
		this.memberCode = memberCode;
		this.institutionCode = institutionCode;
		this.type = BusinessType.CHILDCARE;
	}


	public String getName() {
		return name;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	public String getMemberCode() {
		return memberCode;
	}
	
	public String getInstitutionCode() {
		return institutionCode;
	}
	
	public BusinessType getBusinessType() {
		return type;
	}
	
	public long getID() {
		return this.id;
	}


	public void setName(String name) {
		this.name = name;
	}

	public void setDeliveryAddress(Address deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	
	public void setInstitutionCode(String institutionCode) {
		
		if(this.getBusinessType().equals(BusinessType.COMPANY))
			throw new IllegalAccessError("can not set an institution code to a company!");
		
		this.institutionCode = institutionCode;
	}
	
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(313, 117).
	       append(super.hashCode()).
	       append(deliveryAddress).
	       append(name).
	       append(type).
	       append(memberCode).
	       append(institutionCode).
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