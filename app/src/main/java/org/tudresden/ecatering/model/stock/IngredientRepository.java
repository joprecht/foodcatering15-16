/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tudresden.ecatering.model.stock;



import org.salespointframework.inventory.Inventory;

/**
 * An extension of {@link Catalog} to add video shop specific query methods.
 * 
 * @author Oliver Gierke
 */
public interface IngredientRepository extends Inventory<Ingredient> {

	/**
	 * Returns all {@link Ingredient}s by type.
	 * 
	 * @param type must not be {@literal null}.
	 * @return
	 */
}
