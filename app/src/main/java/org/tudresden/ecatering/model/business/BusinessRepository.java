package org.tudresden.ecatering.model.business;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface BusinessRepository extends CrudRepository<Business,Long> {
	
	Iterable<Business> findByName(String name);
	Iterable<Business> findByType(BusinessType type);
	Optional<Business> findByInstitutionCode(String institutionCode);
	Optional<Business> findByMemberCode(String memberCode);

}
