package org.tudresden.ecatering.model.form;

import java.time.LocalDate;

import org.salespointframework.useraccount.UserAccount;
import org.tudresden.ecatering.model.accountancy.Address;

public class CustomerEditForm {
	private long Id;
	private String businessCode;
	private LocalDate expirationDate;
	private UserAccount userAccount;
	private Address address;
	
	public CustomerEditForm(){
		
	}
	
	public long getId() {
		return Id;
	}
	public void setId(long id) {
		Id = id;
	}
	public String getBusinessCode() {
		return businessCode;
	}
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	public LocalDate getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}
	public UserAccount getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}
	
	public Address getAddress(){
		return address;
	}
	
	public void setAddress(Address address){
		this.address = address;
	}
	
	public String getFirstname(){
		return userAccount.getFirstname();
	}
	
	public void setFirstname(String firstname){
		userAccount.setFirstname(firstname);
	}

	public String getLastname() {
		return userAccount.getLastname();
	}

	public void setLastname(String lastname) {
		userAccount.setLastname(lastname);
	}
	
	

}
