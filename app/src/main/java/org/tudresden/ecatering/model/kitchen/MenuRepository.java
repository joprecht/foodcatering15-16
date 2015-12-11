package org.tudresden.ecatering.model.kitchen;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface MenuRepository extends CrudRepository<Menu,Long> {

	
	Optional<Menu> findByCalendarWeek(int calendarWeek);
	
}
