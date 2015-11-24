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
package org.tudresden.ecatering.stock;


import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;


@Entity
public class Ingredient extends InventoryItem {

	private static final long serialVersionUID = 1602164805477720501L;
	

	


	private LocalDateTime expirationDate;

	
//need default constructor	
	@SuppressWarnings({ "unused", "deprecation" })
	private Ingredient() {}
	

	public Ingredient(Product product,Quantity quantity,LocalDateTime expirationDate) {

		super(product, quantity);

		this.expirationDate = expirationDate;
	}

	public LocalDateTime getExpirationDate() {
		return this.expirationDate;
	}

}
