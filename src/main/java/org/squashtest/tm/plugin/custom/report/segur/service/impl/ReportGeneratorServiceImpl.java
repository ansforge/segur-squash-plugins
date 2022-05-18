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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	public static final String TEMPLATE_NAME = "template-segur-requirement-export.xlsx";
	public static final String TEMPLATE_PREPUB_NAME = "template-segur-requirement-export-avec-colonnes-prepub.xlsx";
	// nom du fichier Excel
	public static final String REM = "REM";
	public static final String PREPUB = "prepub";
	public static final String NAME_SEPARATOR = "_";
	public static final String EXTENSION = ".xlsx";
	
	@Override
	public File generateReport(Map<String, Criteria> criterias) {
		Traceur traceur = new Traceur();

		ExcelWriter excel = new ExcelWriter(traceur);

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
		// chargement du template Excel:
		XSSFWorkbook workbook;
		if (perimeterData.isPrePublication()) {
			workbook = excel.loadWorkbookTemplate(TEMPLATE_PREPUB_NAME);
		} else {
			workbook = excel.loadWorkbookTemplate(TEMPLATE_NAME);
		}
		LOGGER.info(" Récupération du template Excel");

		// ecriture du workbook
		excel.putDatasInWorkbook(perimeterData.isPrePublication(), workbook, data);

		String fileName = createOutputFileName(perimeterData.isPrePublication(),
				getProjectTrigram(perimeterData.getProjectName()), perimeterData.getMilestoneName());
		File report = null;
		try {
			report = excel.flushToTemporaryFile(workbook, fileName);
		} catch (IOException e) {
			LOGGER.error("Error when writing temp file", e);
		}
		return report;
	}

// ***************************************************************************************************	

	private String createOutputFileName(boolean prepub, String trigrammeProjet, String versionOuJalon) {
		
		// publication: REM_[trigramme projet]_version.xls => REM_HOP-RI_V1.3.xls
		// prépublication: prepub_[datedujourJJMMAAAA]_REM_[trigramme
		// projet]_[version].xls
		// avec versiopn= nom du Jalon courant
	
		StringBuilder sFileName = new StringBuilder();
		if (prepub) {
			DateTimeFormatter pattern = DateTimeFormatter.ofPattern("ddMMyyyy");
			LocalDateTime nowDate = LocalDateTime.now();
			sFileName.append(PREPUB).append(NAME_SEPARATOR).append(nowDate.format(pattern)).append(NAME_SEPARATOR);
		}
	
		sFileName.append(REM).append(NAME_SEPARATOR).append(trigrammeProjet).append(NAME_SEPARATOR).append(versionOuJalon)
				.append(EXTENSION);
		return sFileName.toString();
	}

	private String getProjectTrigram(String projectName) {
		
		String trigram = "";
		// CH_ANN_xxxxx SC_ANN_xxx ou prefix_ANN_xxxxxxxxxxx
		String[] frags = projectName.split(NAME_SEPARATOR);
		if ((frags.length < 3) || (frags[1].length() != 3)) {
			LOGGER.warn("project name format not as expected : " + projectName);
			// TODO ecrire une trace pour 3ème onglet si format non respecté ...
			trigram = projectName;
		} else {
			trigram = frags[1];
		}
		return trigram;
	}

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
}
