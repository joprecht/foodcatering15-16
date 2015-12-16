package org.tudresden.ecatering.model.kitchen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;



@Entity
public class DailyMenu implements Serializable {


	private static final long serialVersionUID = -5246668037552907066L;
	
	@Id 
	@GeneratedValue
	@Column(name = "DAILYMENU_ID", insertable = false, updatable = false)
	private long id;
	
	
	private Day day;
	private Helping helping;
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<MenuItem> dailyMeals = new ArrayList<MenuItem>();
	
	@SuppressWarnings("unused")
	private DailyMenu(){}
	

	//Methoden
	protected DailyMenu(List<MenuItem> dailyMeals)
	{		
		
		
		if(dailyMeals==null)
			throw new IllegalArgumentException("dailyMeals is null!");
		
		if(dailyMeals.size()!=3)
			throw new IllegalArgumentException("dailyMeals must have 3 meals!");
		
		boolean 
		hasRegularType=false,
		hasDietType=false,
		hasSpecialType = false;
		
		helping = dailyMeals.get(0).getHelping();
		day = dailyMeals.get(0).getDay();
		
		for(int i = 0; i<dailyMeals.size(); i++)
		{
			if(dailyMeals.get(i).getMeal().getMealType().equals(MealType.REGULAR))
				hasRegularType = true;
			
			if(dailyMeals.get(i).getMeal().getMealType().equals(MealType.DIET))
				hasDietType = true;
			
			if(dailyMeals.get(i).getMeal().getMealType().equals(MealType.SPECIAL))
				hasSpecialType = true;
			
			if(!dailyMeals.get(i).getHelping().equals(helping))
				throw new IllegalArgumentException("MenuItems does not have identic helpings!");
			
			if(!dailyMeals.get(i).getDay().equals(day))
				throw new IllegalArgumentException("MenuItems does not have identic days!");

		}
		
		
		
		
   
			if(!(hasRegularType&&hasDietType&&hasSpecialType))
					throw new IllegalArgumentException ( "DailyMenu has multiple MealTypes!" ) ;
			
			
		this.dailyMeals = dailyMeals;

		
	}
	
	public Day getDay()
	{
		return day;
	}
	
	public List<MenuItem> getDailyMeals()
	{
		
		return dailyMeals;
	}
	
	public long getID() {
		return id;
	}
	
	public Helping getHelping() {
		return helping;
	}
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(113, 73).
	       append(super.hashCode()).
	       append(day).
	       append(dailyMeals).
	       append(id).
	       append(helping).
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
	
}
