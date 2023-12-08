/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.service;

import java.io.File;
import java.util.Map;

import org.squashtest.tm.api.report.criteria.Criteria;


/**
 * The Interface ReportGeneratorService.
 */
public interface ReportGeneratorService {
	
	/**
	 * Generate report.
	 *
	 * @param criterias the criterias
	 * @return the report file
	 */
	File generateReport(Map<String, Criteria> criterias);
}
