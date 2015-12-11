package org.tudresden.ecatering.model.kitchen;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe,Long> {
	
	
	
	
	Optional<Recipe> findByName(String name);


}
