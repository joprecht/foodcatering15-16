package org.tudresden.ecatering.model;

import java.time.LocalDate;

public interface ReportGenerator {
	
	Report generateReport(LocalDate date);

}
