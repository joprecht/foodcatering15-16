package org.tudresden.ecatering.model.business;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudresden.ecatering.model.accountancy.Address;

@Component
public class BusinessManager {

	@Autowired	private BusinessRepository businessRepo;
	
	public BusinessManager() {
		
	}
	
	public Iterable<Business> findAllBusinesses() {
		return businessRepo.findAll();		
	}
	
	public Iterable<Business> findBusinessesByName(String name) {
		return businessRepo.findByName(name);
	}
	
	public Optional<Business> findBusinessByIdentifier(Long id) {
		if(id==null)
			throw new IllegalArgumentException("id is null");
		
		if(businessRepo.findOne(id)==null)
			return Optional.empty();
		
		
		return Optional.of(businessRepo.findOne(id));
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
	
	public Business createCompanyBusiness(String name, Address deliveryAddress, String memberCode)
	{
		
		return new Business(name,deliveryAddress,memberCode);
	}
	
	public Business createChildcareBusiness(String name, Address deliveryAddress, String memberCode, String institutionCode)
	{
		
		return new Business(name,deliveryAddress,memberCode,institutionCode);

	}
	
	public Business saveBusiness(Business business) {
		
		if(business.getMemberCode()==null || business.getMemberCode().trim().isEmpty())
			throw new IllegalArgumentException("MemberCode must not be null!");
		if(this.findBusinessByCode(business.getMemberCode()).isPresent())
			throw new IllegalArgumentException("MemberCode already exists as a BusinessCode!");
		if(business.getBusinessType().equals(BusinessType.CHILDCARE))
		{
			if(business.getInstitutionCode()==null || business.getMemberCode().trim().isEmpty())
				throw new IllegalArgumentException("InstitutionCode must not be null!");
			if(this.findBusinessByCode(business.getInstitutionCode()).isPresent())
				throw new IllegalArgumentException("InstitutionCode already exists as a BusinessCode!");
			if(business.getMemberCode().equals(business.getInstitutionCode()))
				throw new IllegalArgumentException("InstitutionCode and MemberCode must not be equal!");
		}
		
		return businessRepo.save(business);
	}
}
