package org.tudresden.ecatering.model.kitchen;


import org.springframework.data.repository.CrudRepository;

public interface MenuRepository extends CrudRepository<Menu,Long> {

	
	Iterable<Menu> findByCalendarWeek(int calendarWeek);
	
}
