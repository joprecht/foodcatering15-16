package org.tudresden.ecatering.model.kitchen;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;



@Entity 
public class Menu implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7419706969705767363L;

	@Id
	@GeneratedValue
	@Column(name = "MENU_ID", insertable = false, updatable = false)
	private long id;
	
	//Attribute
	private int calendarWeek;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
	
	//Konstruktoren
	@SuppressWarnings("unused")
	private Menu(){}
	
	protected Menu(int calendarWeek, List<DailyMenu> dailyMenus)
	{		
		
		boolean hasMondayMeal = false;
		boolean hasTuesdayMeal = false;
		boolean hasWednesdayMeal = false;
		boolean hasThursdayMeal = false;
		boolean hasFridayMeal = false;

		
		for(int i=0;i<dailyMenus.size();i++)
		{
			if(dailyMenus.get(i).getDay().equals(Day.MONDAY))
				hasMondayMeal = true;
			
			if(dailyMenus.get(i).getDay().equals(Day.TUESDAY))
				hasTuesdayMeal = true;
			
			if(dailyMenus.get(i).getDay().equals(Day.WEDNESDAY))
				hasWednesdayMeal = true;
			
			if(dailyMenus.get(i).getDay().equals(Day.THURSDAY))
				hasThursdayMeal = true;
			
			if(dailyMenus.get(i).getDay().equals(Day.FRIDAY))
				hasFridayMeal = true;
		}
		
		if((calendarWeek>53)||(calendarWeek<=0))
			throw new IllegalArgumentException ( "Menu has wrong calendarWeek!" ) ;

		if(dailyMenus.size()!=5)
			throw new IllegalArgumentException ( "Menu does not consists of 5 DailyMenus!" ) ;
		
		if(!(hasMondayMeal&&hasTuesdayMeal&&hasWednesdayMeal&&hasThursdayMeal&&hasFridayMeal))
			throw new IllegalArgumentException ( "Menu has multiple days!" ) ;
		
		this.calendarWeek = calendarWeek;
		this.dailyMenus = dailyMenus;
	}

	//Methoden
	public int getCalendarWeek() {
		return calendarWeek;
	}

	public List<DailyMenu> getDailyMenus() {
		return dailyMenus;
	}
	
	public long getID() {
		return this.id;
	}

	@Override
	public int hashCode() {	
	     return new HashCodeBuilder(95, 13).
	       append(super.hashCode()).
	       append(calendarWeek).
	       append(dailyMenus).
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
