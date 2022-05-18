/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.LinkedReq;
import org.squashtest.tm.plugin.custom.report.segur.model.PerimeterData;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepBinding;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;


public interface RequirementsCollector {

	
	public PerimeterData findMilestoneByMilestoneId(Long milestoneId);
	
	public String findProjectNameByProjectId(Long projectId);
	
	public List<Cuf> findCUFsByResId(Long resId);
	
	//lecture de CUF de type 'CF' (valeur dans custom_field_value.value)
//	public List<Cuf> findCUFsTypeCFForEntityTypeAndByEntity(String entityType, Long resId);
	
	public List<String> findPointsDeVerificationByTcStepsIds(List<Long> steps); 
	
	public String findStepReferenceByTestStepId(Long testStepId);
	
	//lecture des exigences du projet et jalon (exigences dans l'arbre)
	//public Map<Long, ReqModel> mapFindRequirementByProjectAndMilestone(Long projectId, Long milestoneId);
	public Map<Long, ReqModel> mapFindRequirementByResId(Set<Long> resIds);
	
	//unicité de lien d'une exigence 
	//public List<Long> findReqWithMultiplelinks(Set<Long> reqIds);
	
	//lecture des exigences liées aux exigences de l'arbre
	public List<LinkedReq>  findLinkedReq(Long projectId, Long milestoneId);
	//public List<Long> findLinkedReq(Long reqId) ;
	
	// Tableau lien Exigence - CT-Step (avec filtre Jalon sur CT)
	public List<ReqStepBinding> findTestRequirementBindingFiltreJalonTC(Set<Long> reqId, Long milestoneId);
	
	public Map<Long, TestCase> findTestCase(List<Long> tc_ids) ;
	
	public List<Long> findStepIdsByTestCaseId(Long tc_id) ;
	
	public Map<Long, Step> findSteps(List<Long> tc_ids);
	
	//test parcours de l'arbre
	public Long findIdFolderMetier(Long projectId);
	
	public List<Long> findCoeurMetierIdsByRootTcln_Id(Long rootTcln_id);

}
