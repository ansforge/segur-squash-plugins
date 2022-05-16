/**
 * ====
 *         This file is part of the Squashtest platform.
 *         Copyright (C) 2010 - 2015 Henix, henix.fr
 *
 *         See the NOTICE file distributed with this work for additional
 *         information regarding copyright ownership.
 *
 *         This is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Lesser General Public License as published by
 *         the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         this software is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Lesser General Public License for more details.
 *
 *         You should have received a copy of the GNU Lesser General Public License
 *         along with this software.  If not, see <http://www.gnu.org/licenses/>.
 * ====
 *
 *     This file is part of the Squashtest platform.
 *     Copyright (C) 2010 - 2021 Henix, henix.fr
 *
 *     See the NOTICE file distributed with this work for additional
 *     information regarding copyright ownership.
 *
 *     This is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     this software is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.squashtest.tm.api.report.criteria.Criteria;
import org.squashtest.tm.plugin.custom.report.segur.Traceur;
import org.squashtest.tm.plugin.custom.report.segur.model.PerimeterData;
import org.squashtest.tm.plugin.custom.report.segur.service.ReportGeneratorService;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);

	@Override
	public File generateReport(Map<String, Criteria> criterias) {
		Traceur traceur = new Traceur();

		ExcelWriterUtil excel = new ExcelWriterUtil(traceur);

		LOGGER.info(" SquashTm-segur plugin report ");
		DSRData data = new DSRData(traceur);
		// lecture des critères

		Long selectedProjectId = getProjectId(criterias);
		Long selectedMilestonesId = getMilestone(criterias);
		LOGGER.info(" selectedMilestonesId: " + selectedMilestonesId + " selectedProjectId: " + selectedProjectId);

		// lecture du statut et du nom du jalon => mode publication ou prépublication
		// l'objet perimeterData est construit sur lecture du Milestone (name, status)
		PerimeterData perimeterData = data.completePerimeterData(selectedMilestonesId, selectedProjectId);
		//Chargement des données
		data.loadData(perimeterData);
		// chargement du template Excel
		XSSFWorkbook workbook = excel.loadWorkbookTemplate();
		LOGGER.info(" Récupération du template Excel");

		// ecriture du workbook
		excel.putDatasInWorkbook(perimeterData.isPrePublication(), workbook, data);

		String fileName = excel.createOutputFileName(perimeterData.isPrePublication(),
				ExcelWriterUtil.getProjectTrigram(perimeterData.getProjectName()), perimeterData.getMilestoneName());
		return writeInFile(workbook, fileName);
	}

// ***************************************************************************************************	



	@SuppressWarnings("unchecked")
	private Long getMilestone(Map<String, Criteria> criterias) {
		List<Integer> selectedMilestonesIds = (List<Integer>) criterias.get("milestones").getValue();
		Long selectedMilestonesId = Long.valueOf(selectedMilestonesIds.get(0));
		return selectedMilestonesId;
	}

	@SuppressWarnings("unchecked")
	private Long getProjectId(Map<String, Criteria> criterias) {
		List<String> selectedProjectIds = (List<String>) criterias.get("projects").getValue();
		Long selectedProjectId = Long.parseLong(selectedProjectIds.get(0));
		return selectedProjectId;
	}

	private File writeInFile(XSSFWorkbook workbook, String fileName) {
		File report = null;
		try {
			report = ExcelWriterUtil.flushToTemporaryFile(workbook, fileName);
		} catch (IOException e) {
			LOGGER.error("Error when writing temp file", e);
		}
		return report;
	}


}
