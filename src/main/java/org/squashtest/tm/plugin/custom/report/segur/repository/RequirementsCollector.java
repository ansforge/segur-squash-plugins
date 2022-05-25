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

/**
 * The Interface RequirementsCollector.
 */
public interface RequirementsCollector {

	/**
	 * Find milestone by milestone id.
	 *
	 * @param milestoneId the milestone id
	 * @return the perimeter data
	 */
	public PerimeterData findMilestoneByMilestoneId(Long milestoneId);

	/**
	 * Find project name by project id.
	 *
	 * @param projectId the project id
	 * @return the string
	 */
	public String findProjectNameByProjectId(Long projectId);

	/**
	 * Find squah base url by project id.
	 *
	 * @param projectId the project id
	 * @return the string
	 */
	public String findSquashBaseUrlByProjectId(Long projectId);
	
	/**
	 * Find CU fs by res id.
	 *
	 * @param resId the res id
	 * @return the list
	 */
	
	public List<Cuf> findCUFsByResId(Long resId);

	// lecture de CUF de type 'CF' (valeur dans custom_field_value.value)
//	public List<Cuf> findCUFsTypeCFForEntityTypeAndByEntity(String entityType, Long resId);

	/**
	 * Find points de verification by tc steps ids.
	 *
	 * @param steps the steps
	 * @return the list
	 */
	public List<String> findPointsDeVerificationByTcStepsIds(List<Long> steps);

	/**
	 * Find step reference by test step id.
	 *
	 * @param testStepId the test step id
	 * @return the string
	 */
	public String findStepReferenceByTestStepId(Long testStepId);

	// lecture des exigences du projet et jalon (exigences dans l'arbre)
	// public Map<Long, ReqModel> mapFindRequirementByProjectAndMilestone(Long
	/**
	 * Map find requirement by res id.
	 *
	 * @param resIds the res ids
	 * @return the map
	 */
	// projectId, Long milestoneId);
	public Map<Long, ReqModel> mapFindRequirementByResId(Set<Long> resIds);

	// unicité de lien d'une exigence
	// public List<Long> findReqWithMultiplelinks(Set<Long> reqIds);

	/**
	 * Find linked req.
	 *
	 * @param projectId the project id
	 * @param milestoneId the milestone id
	 * @return the list
	 */
	// lecture des exigences liées aux exigences de l'arbre
	public List<LinkedReq> findLinkedReq(Long projectId, Long milestoneId);
	// public List<Long> findLinkedReq(Long reqId) ;

	/**
	 * Find test requirement binding filtre jalon TC.
	 *
	 * @param reqId the req id
	 * @param milestoneId the milestone id
	 * @return the list
	 */
	// Tableau lien Exigence - CT-Step (avec filtre Jalon sur CT)
	public List<ReqStepBinding> findTestRequirementBindingFiltreJalonTC(Set<Long> reqId, Long milestoneId);

	/**
	 * Find test case.
	 *
	 * @param tc_ids the tc ids
	 * @return the map
	 */
	public Map<Long, TestCase> findTestCase(List<Long> tc_ids);

	/**
	 * Find step ids by test case id.
	 *
	 * @param tc_id the tc id
	 * @return the list
	 */
	public List<Long> findStepIdsByTestCaseId(Long tc_id);

	/**
	 * Find steps.
	 *
	 * @param tc_ids the tc ids
	 * @return the map
	 */
	public Map<Long, Step> findSteps(List<Long> tc_ids);

	/**
	 * Find id folder metier.
	 *
	 * @param projectId the project id
	 * @return the long
	 */
	// test parcours de l'arbre
	public Long findIdFolderMetier(Long projectId);

	/**
	 * Find coeur metier ids by root tcln id.
	 *
	 * @param rootTcln_id the root tcln id
	 * @return the list
	 */
	public List<Long> findCoeurMetierIdsByRootTcln_Id(Long rootTcln_id);

}
