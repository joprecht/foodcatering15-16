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


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.javamoney.moneta.Money;
import static org.salespointframework.core.Currencies.*;
import org.salespointframework.quantity.Metric;


@Entity
public class Grocery implements Serializable {

	

	private static final long serialVersionUID = 8953308785426506433L;

	
	@Id 
	@GeneratedValue 
	@Column(name = "GROCERY_ID", insertable = false, updatable = false)
	private long id;
	
	
	private String name;
	private double price;
	private Metric metric;


	
	

	
//need default constructor	
	@SuppressWarnings( "unused" )
	private Grocery() {}
	

	protected Grocery(String name, Metric metric, Money price) {

		if(name==null || name.trim().equals(""))
			throw new IllegalArgumentException("Name is null or empty!");
		if(metric==null)
			throw new IllegalArgumentException("Metric is null!");
		if(price==null || price.isNegativeOrZero())
			throw new IllegalArgumentException("Price is null or <= 0!");

		
		this.name = name;
		this.metric = metric;
		this.price = price.getNumber().doubleValue();
	}
	
	
	

	public String getName() {
		return name;
	}
	
	public Metric getMetric() {
		return metric;
	}
	
	public Money getPrice() {
		return Money.of(price, EURO);
	}
	
	public long getID() {
		return id;
	}
	
	
	public void setPrice(Money price) {
		this.price = price.getNumber().doubleValue();
	}
	
	@Override
	public int hashCode() {	
		
		
	     return new HashCodeBuilder(19, 913).
	       append(super.hashCode()).
	       append(name).
	       append(metric).
	       append(price).
	       append(id).
	       toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		   return ToStringBuilder.reflectionToString(this);
	}	
	
	//pre persist for optional
}
