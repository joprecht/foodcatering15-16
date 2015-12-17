package org.tudresden.ecatering.model.stock;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface StockReportRepository extends CrudRepository<StockReport,Long> {
	
	Optional<StockReport> findByDate(LocalDate date);

}
