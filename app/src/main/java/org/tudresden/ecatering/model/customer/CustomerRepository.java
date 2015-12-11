package org.tudresden.ecatering.model.customer;


import java.util.Optional;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer,Long> {

	
	Iterable<Customer> findByBusinessCode(String businessCode);
	Optional<Customer> findByUserAccount(UserAccount userAccount);
	
}
