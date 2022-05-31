/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.squashtest.tm.api.report.criteria.Criteria;
import org.squashtest.tm.plugin.custom.report.segur.Traceur;
import org.squashtest.tm.plugin.custom.report.segur.model.PerimeterData;
import org.squashtest.tm.plugin.custom.report.segur.repository.RequirementsCollector;
import org.squashtest.tm.plugin.custom.report.segur.service.ReportGeneratorService;


/**
 * The Class ReportGeneratorServiceImpl.
 */
@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);
	
	/** The Constant TEMPLATE_NAME. */
	public static final String TEMPLATE_NAME = "template-segur-requirement-export.xlsx";
	
	/** The Constant TEMPLATE_PREPUB_NAME. */
	public static final String TEMPLATE_PREPUB_NAME = "template-segur-requirement-export-avec-colonnes-prepub.xlsx";
	
	/** The Constant REM. */
	// nom du fichier Excel
	public static final String REM = "REM";
	
	/** The Constant PREPUB. */
	public static final String PREPUB = "prepub";
	
	/** The Constant NAME_SEPARATOR. */
	public static final String NAME_SEPARATOR = "_";
	
	/** The Constant EXTENSION. */
	public static final String EXTENSION = ".xlsx";

	@Inject
	RequirementsCollector reqCollector;

	@Override
	public File generateReport(Map<String, Criteria> criterias) {
		Traceur traceur = new Traceur();

		ExcelWriter writer = new ExcelWriter(traceur);

		LOGGER.info(" SquashTm-segur plugin report ");
		// lecture des critères
		Long selectedProjectId = getProjectId(criterias);
		Long selectedMilestonesId = getMilestone(criterias);
		LOGGER.info(" selectedMilestonesId: " + selectedMilestonesId + " selectedProjectId: " + selectedProjectId);

		// lecture du statut et du nom du jalon => mode publication ou prépublication
		// l'objet perimeterData est construit sur lecture du Milestone (name, status)
		PerimeterData perimeterData = getPerimeterData(selectedMilestonesId, selectedProjectId);
		DSRData data = new DSRData(traceur, reqCollector, perimeterData);
		// Chargement des données
		data.loadData();
		// chargement du template Excel:
		XSSFWorkbook workbook;
		if (perimeterData.isPrePublication()) {
			workbook = writer.loadWorkbookTemplate(TEMPLATE_PREPUB_NAME);
		} else {
			workbook = writer.loadWorkbookTemplate(TEMPLATE_NAME);
		}
		LOGGER.info(" Récupération du template Excel");

		// ecriture du workbook
		writer.putDatasInWorkbook(workbook, data);

		String fileName = createOutputFileName(perimeterData.isPrePublication(),
				getProjectTrigram(perimeterData.getProjectName()), perimeterData.getMilestoneName());
		File report = null;
		try {
			report = writer.flushToTemporaryFile(workbook, fileName);
		} catch (IOException e) {
			LOGGER.error("Error when writing temp file", e);
		}
		return report;
	}

// ***************************************************************************************************	

	/**
	 * Complete perimeter data.
	 *
	 * @param selectedMilestonesId the selected milestones id
	 * @param selectedProjectId the selected project id
	 * @return the perimeter data
	 */
	private PerimeterData getPerimeterData(Long selectedMilestonesId, Long selectedProjectId) {
		PerimeterData perimeterData = reqCollector.findMilestoneByMilestoneId(selectedMilestonesId);
		LOGGER.info(" lecture du nom et du statut du jalon en base: " + perimeterData.getMilestoneName() + " ; "
				+ perimeterData.getMilestoneStatus());
		perimeterData.setMilestoneId(String.valueOf(selectedMilestonesId));
		perimeterData.setProjectId(String.valueOf(selectedProjectId));

		perimeterData.setProjectName(reqCollector.findProjectNameByProjectId(selectedProjectId));
		perimeterData.setSquashBaseUrl(reqCollector.findSquashBaseUrl());
		return perimeterData;
	}
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

		sFileName.append(REM).append(NAME_SEPARATOR).append(trigrammeProjet).append(NAME_SEPARATOR)
				.append(versionOuJalon).append(EXTENSION);
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
