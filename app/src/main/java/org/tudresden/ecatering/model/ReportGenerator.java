package org.tudresden.ecatering.model;

import java.time.LocalDate;

public interface ReportGenerator<T> {
	
	T generateReport(LocalDate date);

}
