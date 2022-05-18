/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.service;


import java.io.File;
import java.util.Map;

import org.squashtest.tm.api.report.criteria.Criteria;


public interface ReportGeneratorService {
    File generateReport(Map<String, Criteria> criterias);
}
