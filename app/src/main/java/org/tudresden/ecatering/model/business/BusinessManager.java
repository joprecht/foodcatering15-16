package org.tudresden.ecatering.model.business;

import java.util.Optional;

import org.tudresden.ecatering.model.accountancy.Address;

public class BusinessManager {
	
	private BusinessRepository businessRepo;
	
	public BusinessManager(BusinessRepository businesses) {
		
		this.businessRepo = businesses;
	}
	
	public Iterable<Business> findAllBusinesses() {
		return businessRepo.findAll();		
	}
	
	public Iterable<Business> findBusinessesByName(String name) {
		return businessRepo.findByName(name);
	}
	
	public Iterable<Business> findBusinessesByType(BusinessType type) {
		return businessRepo.findByType(type);
	}
		
	public Optional<Business> findBusinessByCode(String businessCode)	
	{
		if(businessCode == null)
			throw new IllegalArgumentException("businessCode must not be null");
		
		Optional<Business> business;
		business = businessRepo.findByInstitutionCode(businessCode);
		if(business.isPresent())
			return business;
		business = businessRepo.findByMemberCode(businessCode);
		
		return business;
	}
	
	public static Business createCompanyBusiness(String name, Address deliveryAddress, String memberCode)
	{
		
		return new Business(name,deliveryAddress,memberCode);
	}
	
	public static Business createChildcareBusiness(String name, Address deliveryAddress, String memberCode, String institutionCode)
	{
		
		return new Business(name,deliveryAddress,memberCode,institutionCode);

	}
	
	public Business saveBusiness(Business business) {
		
		if(business.getMemberCode()==null)
			throw new IllegalArgumentException("MemberCode must not be null!");
		if(this.findBusinessByCode(business.getMemberCode()).isPresent())
			throw new IllegalArgumentException("MemberCode already exists as a BusinessCode!");
		
		if(business.getBusinessType().equals(BusinessType.CHILDCARE))
		{
			if(business.getInstitutionCode()==null)
				throw new IllegalArgumentException("InstitutionCode must not be null!");
			if(this.findBusinessByCode(business.getInstitutionCode()).isPresent())
				throw new IllegalArgumentException("InstitutionCode already exists as a BusinessCode!");
			if(business.getMemberCode().equals(business.getInstitutionCode()))
				throw new IllegalArgumentException("InstitutionCode and MemberCode must not be equal!");
		}
		
		return businessRepo.save(business);
	}
}
