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
import java.util.Arrays;
import java.util.HashMap;
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
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ExtractedData;
import org.squashtest.tm.plugin.custom.report.segur.model.Milestone;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepCaseBinding;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;
import org.squashtest.tm.plugin.custom.report.segur.repository.RequirementsCollector;
import org.squashtest.tm.plugin.custom.report.segur.service.ReportGeneratorService;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);

	List<String> headers = Arrays.asList("Name", "Description", "Reference", "Created by");

	@Inject
	RequirementsCollector reqCollector;

	@Autowired
	ExcelWriterUtil excel;

	// critères
	Long selectedMilestonesId = null;
	Long selectedProjectId = null;
	Boolean boolPrebub = true;
	//default Error FileName
	String fileName = "ERROR_SEGUR_EXPORT.xlsx";
	ExtractedData extractedData;

	@Override
	public File generateReport(Map<String, Criteria> criterias) {

		LOGGER.info(" SquashTm-segur plugin report ");

		// lecture des critères
		getSelectedIdsFromCrietrias(criterias);		
		LOGGER.info(" selectedMilestonesId: " + selectedMilestonesId + " selectedProjectId: " + selectedProjectId);

		// lecture du statut et du nom du jalon => mode publication ou prépublication
		extractedData =   reqCollector.findMilestoneByMilestoneId(selectedMilestonesId);
		LOGGER.info(" lecture du nom et du statut du jalon en base: " + extractedData.getMilestoneName()  + " ; " + extractedData.getMilestoneStatus());
		LOGGER.info(" .... " + extractedData.getMilestoneStatus() + " .." + Constantes.MILESTONE_LOCKED);
		if (extractedData.getMilestoneStatus().equalsIgnoreCase(Constantes.MILESTONE_LOCKED)) {
			boolPrebub = false;
		};
		LOGGER.info(" prépublication: " + boolPrebub);

		extractedData.setMilestoneId(String.valueOf(selectedMilestonesId));
		extractedData.setProjectId(String.valueOf(selectedProjectId));
		
		extractedData.setProjectName(reqCollector.findProjectNameByProjectId(selectedProjectId));		

		// lecture des exigences
		Map<Long, ReqModel> reqs = reqCollector.mapFindRequirementByProjectAndMilestone(selectedProjectId,
				selectedMilestonesId);
		LOGGER.info(" nombre d'exigences trouvées:eqs.size: " + reqs.size());

		Set<Long> reqKetSet = reqs.keySet();

		// lecture des CUFs sur les exigences => cuf.field_type='MSF' => label dans
		// custom_field_value_option
		for (Long res_id : reqKetSet) {
			List<Cuf> cufs = reqCollector.findCUFsByResId(res_id);
			reqs.get(res_id).setCufs(cufs);
			// mise à jour des champs de ExcelData pour l'exigence
			reqs.get(res_id).updateData();
		}
		
		
		//lecture des IDs des CTs coeur de métier => ous un répertoire "_METIER"
//		Long rootMetierId = reqCollector.findIdFolderMetier(selectedProjectId);
//		LOGGER.info(" rootMetierId: " + rootMetierId);
//		List<Long> coeurMetierIds = reqCollector.findCoeurMetierIdsByRootTcln_Id(rootMetierId);
//		LOGGER.info(" coeurMetierIds size: " + coeurMetierIds.size());
		
		// test lecture lien exigence-CT-Step
		List<ReqStepCaseBinding> liste = reqCollector.findTestRequirementBinding(reqKetSet);
		LOGGER.info(" lecture en base des liens exigence/CT/step. Nb liens: " + liste.size());

		// liste des CT à récupérer
		List<Long> distinctCT = liste.stream().map(val -> val.getTclnId()).distinct().collect(Collectors.toList());

		Map<Long, TestCase> mapCT = reqCollector.findTestCase(distinctCT);
		LOGGER.info(" lecture des données sur les CTs. Nbre CT: " + mapCT.size());

		Map<Long, Step> steps = reqCollector.findSteps(distinctCT);
		LOGGER.info(" lecture de tous les steps pour les CTs  steps. size: " + steps.size());

		// on ne garde que les Steps qui sont dans la Binding et on lit les références
		List<Long> usedStepKey = liste.stream().map(val -> val.getStepId()).distinct().collect(Collectors.toList());
		Map<Long, Step> usedSteps = new HashMap<Long, Step>();
		String ref_step = "";
		Step currentStep = null;
		for (Long stepId : usedStepKey) {
			// !!!! TODO => renvoyer une liste pour gérer proprement les cas ou il y a plus
			// qu'une référence ...
			ref_step = reqCollector.findStepReferenceByTestStepId(stepId);
			currentStep = steps.get(stepId);
			currentStep.setReference(ref_step);
			usedSteps.put(stepId, currentStep);
		}

		excel.loadWorkbookTemplate();
		LOGGER.info(" Récupération du template Excel");

		// ecriture du workbook
		//todo refactor => utilisation de ExtractedData
		excel.putDatasInWorkbook(extractedData.getMilestoneStatus(), new ArrayList<ReqModel>(reqs.values()), liste, mapCT, steps,boolPrebub);

		
		fileName = excel.createOutputFileName(boolPrebub, ExcelWriterUtil.getTrigramProject(extractedData.getProjectName()), extractedData.getMilestoneName());
		return writeInFile(excel.getWorkbook(),fileName);
	}

// ***************************************************************************************************	
	
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

	public File writeInFile (XSSFWorkbook workbook, String fileName) {
		File report = null;
		try {
			report = ExcelWriterUtil.flushToTemporaryFile(workbook, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return report;
	}
	
		


}
