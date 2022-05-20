/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import org.squashtest.tm.api.report.BasicDirectDownloadableReport;
import org.squashtest.tm.api.report.criteria.Criteria;
import org.squashtest.tm.plugin.custom.report.segur.service.ReportGeneratorService;

/**
 * The Class SegurExcelReport.
 */
public class SegurExcelReport extends BasicDirectDownloadableReport {

	@Inject
	ReportGeneratorService reportGeneratorService;

	@Override
	public File generateReport(Map<String, Criteria> criterias) {
		return reportGeneratorService.generateReport(criterias);
	}
}
