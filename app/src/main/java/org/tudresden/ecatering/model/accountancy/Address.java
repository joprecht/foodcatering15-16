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
	
	private String firstname, lastname, street, streetNumber,postalCode, city, country;
	
	
	@SuppressWarnings("unused")
	private Address() {}
	
	public Address(String firstname, String lastname, String street, String streetNumber, String postalCode, String city, String country) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.street = street;
		this.streetNumber = streetNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.country = country;
	}
	/**
	 * returns the Firstname
	 * @return Firstname
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * returns the Lastname
	 * @return Lastname
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * returns the Streetname
	 * @return Streetname
	 */
	public String getStreet() {
		return street;
	}
	/**
	 * returns the Streetnumber
	 * @return Streetnumber
	 */
	public String getStreetNumber() {
		return streetNumber;
	}
	/**
	 * returns the Postalcode
	 * @return Postalcode
	 */
	public String getPostalCode() {
		return postalCode;
	}
	/**
	 * returns the City
	 * @return city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * returns the Country
	 * @return Country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * returns the Identificationnumber
	 * @return Identificationnumber
	 */
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