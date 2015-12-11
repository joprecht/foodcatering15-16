package org.tudresden.ecatering.model.accountancy;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
public class Address implements Serializable {
	
	@Id 
	@GeneratedValue 
	private long id;
	
	private static final long serialVersionUID = 5719453362219325234L;
	
	private String firstname, lastname, street, postalCode, city, country;
	private int streetNumber;
	
	
	@SuppressWarnings("unused")
	private Address() {}
	
	public Address(String firstname, String lastname, String street, int streetNumber, String postalCode, String city, String country) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.street = street;
		this.streetNumber = streetNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.country = country;
	}
	public String getFirstname() {
		return firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public String getStreet() {
		return street;
	}
	public int getStreetNumber() {
		return streetNumber;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public String getCity() {
		return city;
	}
	public String getCountry() {
		return country;
	}
	
	public long getID() {
		return id;
	}
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(333, 239).
	       append(super.hashCode()).
	       append(firstname).
	       append(lastname).
	       append(streetNumber).
	       append(street).
	       append(postalCode).
	       append(city).
	       append(country).
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