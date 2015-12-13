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
	
	private Helping helping;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
	
	//Konstruktoren
	@SuppressWarnings("unused")
	private Menu(){}
	
	protected Menu(int calendarWeek, List<DailyMenu> dailyMenus)
	{		
		
		if(dailyMenus==null || dailyMenus.isEmpty())
			throw new IllegalArgumentException("dailyMenus null or empty!");
		
		boolean hasMondayMeal = false;
		boolean hasTuesdayMeal = false;
		boolean hasWednesdayMeal = false;
		boolean hasThursdayMeal = false;
		boolean hasFridayMeal = false;
		
		helping = dailyMenus.get(0).getHelping();
		
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
			
			if(!dailyMenus.get(i).getHelping().equals(helping))
				throw new IllegalArgumentException("DailyMenus does not have identic helpings!");
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
	
	public Helping getHelping() {
		return helping;
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
