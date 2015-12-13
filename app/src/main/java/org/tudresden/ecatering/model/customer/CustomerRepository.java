package org.tudresden.ecatering.model.customer;


import java.util.Optional;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.tudresden.ecatering.model.business.Business;

public interface CustomerRepository extends CrudRepository<Customer,Long> {

	
	Iterable<Customer> findByBusiness(Business business);
	Optional<Customer> findByUserAccount(UserAccount userAccount);
	
}
