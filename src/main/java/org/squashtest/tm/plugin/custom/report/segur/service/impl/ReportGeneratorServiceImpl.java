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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.squashtest.tm.api.report.criteria.Criteria;
import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.Level;
import org.squashtest.tm.plugin.custom.report.segur.Traceur;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelData;
import org.squashtest.tm.plugin.custom.report.segur.model.PerimeterData;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;
import org.squashtest.tm.plugin.custom.report.segur.repository.RequirementsCollector;
import org.squashtest.tm.plugin.custom.report.segur.service.ReportGeneratorService;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);

	@Autowired
	Traceur traceur;
	
	@Inject
	RequirementsCollector reqCollector;

	@Autowired
	ExcelWriterUtil excel;

	// critères
	Long selectedMilestonesId = null;
	Long selectedProjectId = null;
	Boolean boolPrebub = true;
	// default Error FileName
	String fileName = "ERROR_SEGUR_EXPORT.xlsx";
	// l'objet perimeterData est construit sur lecture du Milestone (name, status)
	PerimeterData perimeterData;

	@Override
	public File generateReport(Map<String, Criteria> criterias) {

		LOGGER.info(" SquashTm-segur plugin report ");
		
		reset();
		
		// lecture des critères
		getSelectedIdsFromCrietrias(criterias);
		LOGGER.info(" selectedMilestonesId: " + selectedMilestonesId + " selectedProjectId: " + selectedProjectId);

		// lecture du statut et du nom du jalon => mode publication ou prépublication
		completePerimeterData();

		// lecture des exigences => mise à jour de List<ReqModel> reqs (excelWriter)
		Set<Long> reqIds = setRequirementData(selectedProjectId, selectedMilestonesId);

		// lecture des liens exigence-CT et récupération de la liste des CTs à lire
		List<Long> distinctCT = setBinding(reqIds, selectedMilestonesId);

		// lecture des données sur les CTs
		setMapTestCase(distinctCT);

		// lecture des IDs des CTs 'coeur de métier' => sous un répertoire "_METIER" et
		// mise à jour de la propriété dans de l'objet TestCase
		findTestCaseCoeurDeMetier();

		// lecture des données sur les steps
		setStepsData(distinctCT);

		// chargement du template Excel
		excel.loadWorkbookTemplate();
		LOGGER.info(" Récupération du template Excel");

		// ecriture du workbook
		excel.putDatasInWorkbook(perimeterData.getMilestoneStatus(), boolPrebub);

		fileName = excel.createOutputFileName(boolPrebub,
				ExcelWriterUtil.getTrigramProject(perimeterData.getProjectName()), perimeterData.getMilestoneName());
		return writeInFile(excel.getWorkbook(), fileName);
	}

// ***************************************************************************************************	

	public void completePerimeterData() {
		perimeterData = reqCollector.findMilestoneByMilestoneId(selectedMilestonesId);
		LOGGER.info(" lecture du nom et du statut du jalon en base: " + perimeterData.getMilestoneName() + " ; "
				+ perimeterData.getMilestoneStatus());
		if (perimeterData.getMilestoneStatus().equalsIgnoreCase(Constantes.MILESTONE_LOCKED)) {
			boolPrebub = false;
		}
		else {
			boolPrebub = true;
		}
		
		LOGGER.info(" prépublication: " + boolPrebub);

		perimeterData.setMilestoneId(String.valueOf(selectedMilestonesId));
		perimeterData.setProjectId(String.valueOf(selectedProjectId));

		perimeterData.setProjectName(reqCollector.findProjectNameByProjectId(selectedProjectId));
	}

	public void getSelectedIdsFromCrietrias(Map<String, Criteria> criterias) {
		Set<String> keys = criterias.keySet();
		List<Integer> selectedMilestonesIds = null;
		List<String> selectedProjectIds = null;

		if (keys.contains("milestones")) {
			selectedMilestonesIds = (List<Integer>) criterias.get("milestones").getValue();
			// TODO: erreur si 0 ou plus d'un jalon selectedMilestonesIds.size() 0 ou >2
			selectedMilestonesId = Long.valueOf(selectedMilestonesIds.get(0));
		}

		if (keys.contains("projects")) {
			selectedProjectIds = (List<String>) criterias.get("projects").getValue();
			// TODO: erreur si 0 ou plus d'un projet selectedProjectIds.size()
			selectedProjectId = Long.parseLong(selectedProjectIds.get(0));
		}
	}

	public File writeInFile(XSSFWorkbook workbook, String fileName) {
		File report = null;
		try {
			report = ExcelWriterUtil.flushToTemporaryFile(workbook, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return report;
	}

	public Set<Long> setRequirementData(Long xprojectId, Long xmilestonesId) {
		Map<Long, ReqModel> reqs = reqCollector.mapFindRequirementByProjectAndMilestone(xprojectId, xmilestonesId);
		LOGGER.info(" nombre d'exigences trouvées:eqs.size: " + reqs.size());

		Set<Long> reqKetSet = reqs.keySet();
		if (reqKetSet.size() ==0) {
			traceur.addMessage(Level.WARNING, "", "aucune exigence trouvée pour le projetId = "
					+ selectedProjectId + " et le jalon id = " + selectedMilestonesId);
		}

		// lecture des CUFs sur les exigences => cuf.field_type='MSF' => label dans
		// custom_field_value_option
		List<ExcelData> datas = new ArrayList<ExcelData>();
		for (Long res_id : reqKetSet) {
			List<Cuf> cufs = reqCollector.findCUFsByResId(res_id);
			reqs.get(res_id).setCufs(cufs);
			// mise à jour des champs de ExcelData pour l'exigence
			datas.add(reqs.get(res_id).updateData(traceur));			
		}

		excel.setReqs(datas);
		return reqKetSet;
	}

	public List<Long> setBinding(Set<Long> xreqIds, Long xmilestonesId) {

		excel.setBindings(reqCollector.findTestRequirementBindingFiltreJalonTC(xreqIds, xmilestonesId));
		LOGGER.info(" lecture en base des liens exigence/CT/step. Nb liens: " + excel.getBindings().size());

		// liste des CT à récupérer
		return excel.getBindings().stream().map(val -> val.getTclnId()).distinct().collect(Collectors.toList());
	}

	public void setMapTestCase(List<Long> xdistinctCT) {
		excel.setMapCT(reqCollector.findTestCase(xdistinctCT));
		LOGGER.info(" lecture des données sur les CTs. Nbre CT: " + excel.getMapCT().size());

		// mise à jour de la liste des Step dans les CTs
		TestCase tcTmp;
		for (Long testCaseId : excel.getMapCT().keySet()) {
			tcTmp = excel.getMapCT().get(testCaseId);
			tcTmp.setOrderedStepIds(reqCollector.findStepIdsByTestCaseId(testCaseId));
			excel.getMapCT().put(testCaseId, tcTmp);
		}
	}

	public void findTestCaseCoeurDeMetier() {
		perimeterData.setTclnIdFolderMetier(reqCollector.findIdFolderMetier(selectedProjectId));
		LOGGER.info(" rootMetierId (tcln_id du répertoire des cas de test '_METIER' "
				+ perimeterData.getTclnIdFolderMetier());
		perimeterData.setIdsCasDeTestCoeurDeMetier(
				reqCollector.findCoeurMetierIdsByRootTcln_Id(perimeterData.getTclnIdFolderMetier()));
		LOGGER.info(" Nombre de cas de test coeurMetierIds trouvés sur le projet: "
				+ perimeterData.getIdsCasDeTestCoeurDeMetier().size());
		// Mise à jour de la propriété isCOeurDeMetier dans TestCase
		TestCase tmp = null;
		Map<Long, TestCase> map = excel.getMapCT();
		for (Long coeurDeMetierId : perimeterData.getIdsCasDeTestCoeurDeMetier()) {
			if (map.containsKey(coeurDeMetierId)) {
				tmp = map.get(coeurDeMetierId);
				tmp.setIsCoeurDeMetier(true);
				map.put(coeurDeMetierId, tmp);
			}
		}
	}

	public void setStepsData(List<Long> distinctCT) {
		excel.setSteps(reqCollector.findSteps(distinctCT));
		LOGGER.info(" lecture de tous les steps pour les CTs  steps. size: " + excel.getSteps().size());
		// lecture des references des pas de test (CUF)
		String ref_step = "";
		Step currentStep = null;
		int nbRef = 0;
		for (Long stepId : excel.getSteps().keySet()) {
			// TODO => renvoyer une liste pour gérer proprement les cas ou il y a plus
			// d'une reference
//		nbRef = reqCollector.countStepReferenceByStepID(stepId);
//		if (nbRef!=1) {
//			//pb ...
//		}
			ref_step = reqCollector.findStepReferenceByTestStepId(stepId);
			currentStep = excel.getSteps().get(stepId);
			currentStep.setReference(ref_step);
			excel.getSteps().put(stepId, currentStep);
		}
	}

	public void reset() {
		selectedMilestonesId = null;
		selectedProjectId = null;
		boolPrebub = true;
		perimeterData = null;
		traceur.reset();
	}
	

}
