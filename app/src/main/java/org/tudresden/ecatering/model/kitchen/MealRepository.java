package org.tudresden.ecatering.model.kitchen;

import org.salespointframework.catalog.Catalog;

public interface MealRepository extends Catalog<Meal> {

	/**
	 * Returns all {@link Disc}s by type.
	 * 
	 * @param type must not be {@literal null}.
	 * @return
	 */
	Iterable<Meal> findByType(MealType type);
}

