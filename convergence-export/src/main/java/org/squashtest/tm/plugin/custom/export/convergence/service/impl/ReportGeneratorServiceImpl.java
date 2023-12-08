/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.squashtest.tm.api.report.criteria.Criteria;
import org.squashtest.tm.plugin.custom.export.convergence.Traceur;
import org.squashtest.tm.plugin.custom.export.convergence.model.PerimeterData;
import org.squashtest.tm.plugin.custom.export.convergence.repository.RequirementsCollector;
import org.squashtest.tm.plugin.custom.export.convergence.service.ReportGeneratorService;


/**
 * The Class ReportGeneratorServiceImpl.
 */
@Service("convergence.generatorService")
public class ReportGeneratorServiceImpl implements ReportGeneratorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);
	
	
	/** The name of the excel convergence export template */
	//public static final String TEMPLATE_PREPUB_NAME = "template-segur-requirement-export-avec-colonnes-prepub.xlsx";
	public static final String TEMPLATE_EXPORT_CONVERGENCE = "template-export-convergence.xlsx";

	//POur construction du nom du fichier excel
	/** Début du fichier */
	// nom du fichier Excel
	public static final String FILE_PREFIX = "EXPORT_CONVERGENCE";
	/** The Constant NAME_SEPARATOR. */
	public static final String NAME_SEPARATOR = "_";
	/** The Constant EXTENSION. */
	public static final String EXTENSION = ".xlsx";

	@Inject
	@Qualifier("convergenceReportRepository")
	RequirementsCollector reqCollector;

	@Override
	public File generateReport(Map<String, Criteria> criterias) {
		Traceur traceur = new Traceur();

		ExcelWriter writer = new ExcelWriter(traceur);

		LOGGER.info(" SquashTm-convergence-export-plugin");
		// lecture des critères
	
		// Afficher le contenu du MAP
		Set<String> listKeys=criterias.keySet();  // Obtenir la liste des clés
		Iterator<String> iterateur=listKeys.iterator();
		// Parcourir les clés et afficher les entrées de chaque clé;
		while(iterateur.hasNext())
		{
			Object key= iterateur.next();
			//System.out.println (key+"=>"+myMap.get(key));
			LOGGER.info( key + " => " + criterias.get(key));
		}
		
		Long selectedProjectId = getProjectId(criterias);
		Long selectedMilestonesId = getMilestone(criterias);
		LOGGER.info(" selectedMilestonesId: " + selectedMilestonesId + " selectedProjectId: " + selectedProjectId );
		// lecture du statut et du nom du jalon => mode publication ou prépublication
		// l'objet perimeterData est construit sur lecture du Milestone (name, status)
		PerimeterData perimeterData = getPerimeterData(selectedMilestonesId, selectedProjectId);
		DSRData data = new DSRData(traceur, reqCollector, perimeterData);
		// Chargement des données
		data.loadData();
		// chargement du template Excel:
		XSSFWorkbook workbook = writer.loadWorkbookTemplate(TEMPLATE_EXPORT_CONVERGENCE);
	
		LOGGER.info(" Récupération du template Excel");

		// ecriture du workbook
		writer.putDatasInWorkbook(workbook, data);

		String fileName = createOutputFileName(getProjectTrigram(perimeterData.getProjectName()), perimeterData.getMilestoneName());
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
	private String createOutputFileName(String trigrammeProjet, String versionOuJalon) {
		
		  DateTimeFormatter pattern = DateTimeFormatter.ofPattern("ddMMyyyy");
          LocalDateTime nowDate = LocalDateTime.now();
  
		StringBuilder sFileName = new StringBuilder();
		sFileName.append(FILE_PREFIX).append(NAME_SEPARATOR).append(nowDate.format(pattern)).append(NAME_SEPARATOR).append(trigrammeProjet).append(NAME_SEPARATOR)
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

	@SuppressWarnings("unchecked")
	private String getTemplate(Map<String, Criteria> criterias) {
		String selectedTemplate = (String) criterias.get("templateSelectionMode").getValue();
		return selectedTemplate;
	}
}
