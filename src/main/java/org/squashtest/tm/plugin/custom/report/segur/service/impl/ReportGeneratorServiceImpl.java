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
import java.util.HashMap;
import java.util.HashSet;
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
import org.squashtest.tm.plugin.custom.report.segur.Parser;
import org.squashtest.tm.plugin.custom.report.segur.Traceur;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelData;
import org.squashtest.tm.plugin.custom.report.segur.model.LinkedReq;
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

		ExcelWriterUtil excel = new ExcelWriterUtil(traceur);
		
		LOGGER.info(" SquashTm-segur plugin report ");
		
		reset();
		
		// lecture des critères
		getSelectedIdsFromCrietrias(criterias);
		LOGGER.info(" selectedMilestonesId: " + selectedMilestonesId + " selectedProjectId: " + selectedProjectId);

		// lecture du statut et du nom du jalon => mode publication ou prépublication
		completePerimeterData();

		// lecture des exigences => mise à jour de List<ReqModel> reqs (excelWriter)
		Set<Long> reqIds = setRequirementData(selectedProjectId, selectedMilestonesId, excel);

		// lecture des liens exigence-CT et récupération de la liste des CTs à lire
		List<Long> distinctCT = setBinding(reqIds, selectedMilestonesId,excel);

		// lecture des données sur les CTs
		setMapTestCase(distinctCT,excel);

		// lecture des IDs des CTs 'coeur de métier' => sous un répertoire "_METIER" et
		// mise à jour de la propriété dans l'objet TestCase
		findTestCaseCoeurDeMetier(excel);

		// lecture des données sur les steps
		setStepsData(distinctCT, excel);

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

	public Set<Long> setRequirementData(Long xprojectId, Long xmilestonesId, ExcelWriterUtil excel) {
		
		List<LinkedReq> linkedOrNotReqs = reqCollector.findLinkedReq(xprojectId, xmilestonesId);
		
		//Map  des exgicences de l'arbre (projet) avec ID des exigences liées (attention si une exigence n'est pas lié , elle n'est pas dans la map)
		Map<Long, Long> linkedReqs = getMapTreeRequirementAndlinkedRequirement(linkedOrNotReqs);

		//liste des exigences de l'arbre
		Set<Long> treeReqIs = getTreeResId(linkedOrNotReqs);
		
		//liste de toutes les exigences: arbre et liées		
		Set<Long> allReqIds = new HashSet<Long>();
		allReqIds.addAll(treeReqIs);
		allReqIds.addAll(linkedReqs.values());
		//liste des exigences de l'arbre
	
		LOGGER.error(" treeReqIs " + treeReqIs.size());
		LOGGER.error(" allReqIds " + allReqIds.size());
		LOGGER.error(" linkedReqs " + linkedReqs.size());
		
		//Lecture des données de toutes les exigences (arbre et liées) 
		Map<Long, ReqModel> reqs = reqCollector.mapFindRequirementByResId(allReqIds);
		LOGGER.info(" nombre d'exigences lues: " + reqs.size() + " dont nombre d'exigence dans l'arbre: " + treeReqIs.size());

		Set<Long> reqKetSet = reqs.keySet();
		if (reqKetSet.size() ==0) {
			traceur.addMessage(Level.WARNING, "", "aucune exigence trouvée pour le projetId = "
					+ selectedProjectId + " et le jalon id = " + selectedMilestonesId);
		}
								
		// lecture des CUFs sur les exigences => cuf.field_type='MSF' => label dans
		// custom_field_value_option
		//construction liste de Excel pour les exigences de l'arbre
		List<ExcelData> datas = new ArrayList<ExcelData>();
		Long socleResId = null;
		for (Long res_id : treeReqIs) {
			//cufs de l'exigence
			List<Cuf> cufs = reqCollector.findCUFsByResId(res_id);
			reqs.get(res_id).setCufs(cufs);
			//si exigence liée
			socleResId = linkedReqs.get(res_id);
			if (socleResId != null) {
				List<Cuf> cufsSocle = reqCollector.findCUFsByResId(socleResId);
				reqs.get(socleResId).setCufs(cufsSocle);
			}
			
			// mise à jour des champs de ExcelData pour l'exigence
			datas.add(reqs.get(res_id).updateData(traceur, reqs.get(socleResId) ));			
		}

		excel.setReqs(datas);
		return reqKetSet;
	}

	public List<Long> setBinding(Set<Long> xreqIds, Long xmilestonesId, ExcelWriterUtil excel) {

		excel.setBindings(reqCollector.findTestRequirementBindingFiltreJalonTC(xreqIds, xmilestonesId));
		LOGGER.info(" lecture en base des liens exigence/CT/step. Nb liens: " + excel.getBindings().size());

		// liste des CT à récupérer
		return excel.getBindings().stream().map(val -> val.getTclnId()).distinct().collect(Collectors.toList());
	}

	public void setMapTestCase(List<Long> xdistinctCT, ExcelWriterUtil excel) {
		excel.setMapCT(reqCollector.findTestCase(xdistinctCT));
		LOGGER.info(" lecture des données sur les CTs. Nbre CT: " + excel.getMapCT().size());

		// mise à jour de la liste des Step dans les CTs
		TestCase tcTmp;
		List<Long> ctSteps;
		
		for (Long testCaseId : excel.getMapCT().keySet()) {
			tcTmp = excel.getMapCT().get(testCaseId);
			ctSteps = reqCollector.findStepIdsByTestCaseId(testCaseId);
			tcTmp.setOrderedStepIds(ctSteps);
			if (boolPrebub) {
				List<String> ptsDeVerif = reqCollector.findPointsDeVerificationByTcStepsIds(ctSteps);
				StringBuilder builder = new StringBuilder();
				    for (String verif : ptsDeVerif) {
				    	builder.append(Parser.convertHTMLtoString(verif));
					}
				    tcTmp.setPointsDeVerification(builder.toString());
			}	
			excel.getMapCT().put(testCaseId, tcTmp);
		}		
	}

	public void findTestCaseCoeurDeMetier(ExcelWriterUtil excel) {
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

	public void setStepsData(List<Long> distinctCT, ExcelWriterUtil excel) {
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
	
	public Map<Long, Long> getMapTreeRequirementAndlinkedRequirement(List<LinkedReq> linkedReqs) {
		
		Map<Long, Long> treeResIdAndLinkedResId = new HashMap();
		for (LinkedReq linkedReq : linkedReqs) {
			if (linkedReq.getSocleResId() != null) {
				if (treeResIdAndLinkedResId.containsKey(linkedReq.getResId())) {
					traceur.addMessage(Level.ERROR, linkedReq.getResId(),  "Cette exigence est ignorée car elle est liée à au moins 2 autres exigences: ");
					treeResIdAndLinkedResId.remove(linkedReq.getResId());
				} else {
					treeResIdAndLinkedResId.put(linkedReq.getResId(), linkedReq.getSocleResId());				
				}
			}

		}		
		return treeResIdAndLinkedResId;
	}
	
	public Set<Long> getTreeResId(List<LinkedReq> linkedReqs) {
		Set<Long> result = new HashSet();
		for (LinkedReq linkedReq : linkedReqs) {
			result.add(linkedReq.getResId());
		}
			return result;
	}
}
