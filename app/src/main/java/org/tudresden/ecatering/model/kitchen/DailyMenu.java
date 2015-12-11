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
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Meal> dailyMeals = new ArrayList<Meal>();
	
	@SuppressWarnings("unused")
	private DailyMenu(){}
	

	//Methoden
	protected DailyMenu(Day day, List<Meal> dailyMeals)
	{		
		
		boolean hasRegularType = false;
		boolean hasDietType = false;
		boolean hasSpecialType = false;
		
		for(int i=0;i<dailyMeals.size();i++)
		{
			if(dailyMeals.get(i).getMealType().equals(MealType.REGULAR))
				hasRegularType = true;
			
			if(dailyMeals.get(i).getMealType().equals(MealType.DIET))
				hasDietType = true;
			
			if(dailyMeals.get(i).getMealType().equals(MealType.SPECIAL))
				hasSpecialType = true;
		}
		
		
			if(dailyMeals.size()!=3)
				throw new IllegalArgumentException( "DailyMenu does not consists of 3 Meals!" ) ;
       
		
		
			if(!(hasRegularType&&hasDietType&&hasSpecialType))
					throw new IllegalArgumentException ( "DailyMenu has multiple MealTypes!" ) ;
			
			this.day = day;
			this.dailyMeals = dailyMeals;

		
	}
	
	public Day getDay()
	{
		return day;
	}
	
	public List<Meal> getDailyMeals()
	{
		
		return dailyMeals;
	}
	
	public long getID() {
		return this.id;
	}
	
	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(113, 73).
	       append(super.hashCode()).
	       append(day).
	       append(dailyMeals).
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
	
}
