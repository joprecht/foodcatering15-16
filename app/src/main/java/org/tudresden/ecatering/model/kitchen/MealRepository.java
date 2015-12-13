package org.tudresden.ecatering.model.kitchen;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface MealRepository extends CrudRepository<Meal,Long> {

	/**
	 * Returns all {@link Disc}s by type.
	 * 
	 * @param type must not be {@literal null}.
	 * @return
	 */
	Iterable<Meal> findByType(MealType type);
	Optional<Meal> findByName(String name);
	Optional<Meal> findMealByRecipe(Recipe recipe);

}

