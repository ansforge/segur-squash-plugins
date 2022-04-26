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
package org.squashtest.tm.plugin.custom.report.segur.repository.impl;

import static org.squashtest.tm.jooq.domain.Tables.INFO_LIST_ITEM;
import static org.squashtest.tm.jooq.domain.Tables.PROJECT;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT_LIBRARY_NODE;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT_VERSION;
import static org.squashtest.tm.jooq.domain.Tables.RESOURCE;
import static org.squashtest.tm.jooq.domain.Tables.CUSTOM_FIELD;
import static org.squashtest.tm.jooq.domain.Tables.CUSTOM_FIELD_VALUE;
import static org.squashtest.tm.jooq.domain.Tables.CUSTOM_FIELD_VALUE_OPTION;
import static org.squashtest.tm.jooq.domain.Tables.CUSTOM_FIELD_BINDING;
import static org.squashtest.tm.jooq.domain.Tables.MILESTONE;
import static org.squashtest.tm.jooq.domain.Tables.MILESTONE_BINDING;
import static org.squashtest.tm.jooq.domain.Tables.MILESTONE_REQ_VERSION;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT_VERSION_COVERAGE;
import static org.squashtest.tm.jooq.domain.Tables.VERIFYING_STEPS;
import static org.squashtest.tm.jooq.domain.Tables.TEST_CASE;
import static org.squashtest.tm.jooq.domain.Tables.TEST_CASE_LIBRARY_NODE;
import static org.squashtest.tm.jooq.domain.Tables.TEST_CASE_STEPS;
import static org.squashtest.tm.jooq.domain.Tables.TEST_STEP;
import static org.squashtest.tm.jooq.domain.Tables.ACTION_TEST_STEP;




import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.model.BasicReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepCaseBinding;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;
import org.squashtest.tm.plugin.custom.report.segur.repository.RequirementsCollector;


@Repository
public class RequirementsCollectorImpl implements RequirementsCollector {

	@Inject
	private DSLContext dsl;

	// non utilisé?  à supprimer?
	@Override
	public List<ReqModel> findRequirementByProjectAndMilestone(Long projectId, Long milestoneId) {

		List<ReqModel> reqList = dsl
				.select(PROJECT.PROJECT_ID, REQUIREMENT_VERSION.RES_ID, REQUIREMENT_VERSION.REFERENCE,
						REQUIREMENT_VERSION.REQUIREMENT_STATUS, INFO_LIST_ITEM.LABEL.as("CATEGORY"),
						RESOURCE.DESCRIPTION)
				.from(REQUIREMENT)
				.innerJoin(REQUIREMENT_LIBRARY_NODE).on(REQUIREMENT_LIBRARY_NODE.RLN_ID.eq(REQUIREMENT.RLN_ID))
				.innerJoin(PROJECT).on(PROJECT.PROJECT_ID.eq(REQUIREMENT_LIBRARY_NODE.PROJECT_ID))
				.innerJoin(REQUIREMENT_VERSION).on(REQUIREMENT_VERSION.REQUIREMENT_ID.eq(REQUIREMENT_LIBRARY_NODE.RLN_ID))
				.innerJoin(RESOURCE).on(RESOURCE.RES_ID.eq(REQUIREMENT_VERSION.RES_ID))
				.innerJoin(INFO_LIST_ITEM).on(INFO_LIST_ITEM.ITEM_ID.eq(REQUIREMENT_VERSION.CATEGORY))
				.where(PROJECT.NAME.eq("DSR_DUI_MS1")
						.and(REQUIREMENT_VERSION.REFERENCE.in("M/A.01A", "M/A.03", "M/A.04A", "M/A.08C")))
				.fetchInto(ReqModel.class);

		return reqList;
	}

	@Override
	public List<Cuf> findCUFsByResId(Long resId) {
//		String query = "select " + "cuf.code, cufvo.label" + " from custom_field_value cufv "
//	
//				+ "INNER JOIN custom_field_binding cufb " + "ON cufv.cfb_id = cufb.cfb_id "
//				+ "INNER JOIN custom_field cuf " + "ON cuf.cf_id=cufb.cf_id "
//				+ "LEFT JOIN custom_field_value_option cufvo " + "ON cufv.cfv_id = cufvo.cfv_id "
//				+ "where cufv.bound_entity_type ='REQUIREMENT_VERSION' " + "and cufv.bound_entity_id=?";
		
		List<Cuf> cufs = dsl
				.select(CUSTOM_FIELD.CODE, CUSTOM_FIELD_VALUE_OPTION.LABEL)
				.from(CUSTOM_FIELD_VALUE)
				.innerJoin(CUSTOM_FIELD_BINDING).on(CUSTOM_FIELD_BINDING.CFB_ID.eq(CUSTOM_FIELD_VALUE.CFB_ID))
				
				.innerJoin(CUSTOM_FIELD).on(CUSTOM_FIELD.CF_ID.eq(CUSTOM_FIELD_BINDING.CF_ID))
				.leftJoin(CUSTOM_FIELD_VALUE_OPTION).on(CUSTOM_FIELD_VALUE.CFV_ID.eq(CUSTOM_FIELD_VALUE_OPTION.CFV_ID))
				.where(CUSTOM_FIELD_VALUE.BOUND_ENTITY_TYPE.eq(Constantes.CUF_TYPE_OBJECT_REQ)) // REQUIREMENT_VERSION
						.and(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID.eq(resId))
				.fetchInto(Cuf.class);

		return cufs;
	}

	
	//a supprimer? valable uniquement pour type CUF = 'CF' ...
	@Override
	public List<Cuf> findCUFsForTypeAndByEntityId(String entityType, Long resId) {
	
		List<Cuf> cufs = dsl
				.select(CUSTOM_FIELD.CODE, CUSTOM_FIELD_VALUE_OPTION.LABEL)
				.from(CUSTOM_FIELD_VALUE)
				.innerJoin(CUSTOM_FIELD_BINDING).on(CUSTOM_FIELD_BINDING.CFB_ID.eq(CUSTOM_FIELD_VALUE.CFB_ID))
				
				.innerJoin(CUSTOM_FIELD).on(CUSTOM_FIELD.CF_ID.eq(CUSTOM_FIELD_BINDING.CF_ID))
				.leftJoin(CUSTOM_FIELD_VALUE_OPTION).on(CUSTOM_FIELD_VALUE.CFV_ID.eq(CUSTOM_FIELD_VALUE_OPTION.CFV_ID))
				.where(CUSTOM_FIELD_VALUE.BOUND_ENTITY_TYPE.eq(entityType))
						.and(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID.eq(resId))
				.fetchInto(Cuf.class);

		return cufs;
	}
	
	@Override
	public List<Cuf> findCUFsTypeCFForEntityTypeAndByEntity(String entityType, Long entityId) {
		return dsl
				.select(CUSTOM_FIELD.CODE, CUSTOM_FIELD_VALUE.VALUE)
				.from(CUSTOM_FIELD_VALUE)
				.innerJoin(CUSTOM_FIELD_BINDING).on(CUSTOM_FIELD_BINDING.CFB_ID.eq(CUSTOM_FIELD_VALUE.CFB_ID))				
				.innerJoin(CUSTOM_FIELD).on(CUSTOM_FIELD.CF_ID.eq(CUSTOM_FIELD_BINDING.CF_ID))
				.where(CUSTOM_FIELD_VALUE.BOUND_ENTITY_TYPE.eq(entityType))
				        .and(CUSTOM_FIELD.FIELD_TYPE.eq(Constantes.CUF_FIELD_TYPE_CF))
						.and(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID.eq(entityId))
				.fetchInto(Cuf.class);

	}
	
	@Override
	public String findStepReferenceByTestStepId(Long testStepId) {
		return dsl
				.select(CUSTOM_FIELD_VALUE.VALUE)
				.from(CUSTOM_FIELD_VALUE)
				.innerJoin(CUSTOM_FIELD_BINDING).on(CUSTOM_FIELD_BINDING.CFB_ID.eq(CUSTOM_FIELD_VALUE.CFB_ID))				
				.innerJoin(CUSTOM_FIELD).on(CUSTOM_FIELD.CF_ID.eq(CUSTOM_FIELD_BINDING.CF_ID))
				.where(CUSTOM_FIELD_VALUE.BOUND_ENTITY_TYPE.eq(Constantes.CUF_TYPE_OBJECT_TEST_STEP))
				        .and(CUSTOM_FIELD.FIELD_TYPE.eq(Constantes.CUF_FIELD_TYPE_CF))
				        .and(CUSTOM_FIELD.CODE.eq(Constantes.REF_PREUVE))
						.and(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID.eq(testStepId))
				.fetchOneInto(String.class);
		         
	}
	
//	public List<Cuf> findCUFsTypeCFForEntityTypeAndByEntity(String entityType, String cufCode, Long resId) {
//		
//	}
//	
	
	
	@Override
	public Map<Long, ReqModel> mapFindRequirementByProjectAndMilestoneBRIDEEEEEEE(Long projectId, Long milestoneId) {

		Map<Long, ReqModel> reqList = dsl
				.select(PROJECT.PROJECT_ID, REQUIREMENT_VERSION.RES_ID, REQUIREMENT_VERSION.REFERENCE,
						REQUIREMENT_VERSION.REQUIREMENT_STATUS, INFO_LIST_ITEM.LABEL.as("CATEGORY"),
						RESOURCE.DESCRIPTION)
				.from(REQUIREMENT)
				.innerJoin(REQUIREMENT_LIBRARY_NODE).on(REQUIREMENT_LIBRARY_NODE.RLN_ID.eq(REQUIREMENT.RLN_ID))
				.innerJoin(PROJECT).on(PROJECT.PROJECT_ID.eq(REQUIREMENT_LIBRARY_NODE.PROJECT_ID))
				.innerJoin(REQUIREMENT_VERSION).on(REQUIREMENT_VERSION.REQUIREMENT_ID.eq(REQUIREMENT_LIBRARY_NODE.RLN_ID))
				.innerJoin(RESOURCE).on(RESOURCE.RES_ID.eq(REQUIREMENT_VERSION.RES_ID))
				.innerJoin(INFO_LIST_ITEM).on(INFO_LIST_ITEM.ITEM_ID.eq(REQUIREMENT_VERSION.CATEGORY))
				.where(PROJECT.NAME.eq("DSR_DUI_MS1")
						.and(REQUIREMENT_VERSION.REFERENCE.in("M/A.01A", "M/A.03", "M/A.04A", "M/A.08C")))
				.fetch().intoMap(REQUIREMENT_VERSION.RES_ID, ReqModel.class);

		return reqList;
	}
	
	
	@Override
	public Map<Long, ReqModel> mapFindRequirementByProjectAndMilestone(Long projectId, Long milestoneId) {

		Map<Long, ReqModel> reqList = dsl
				.select(REQUIREMENT_VERSION.RES_ID, REQUIREMENT_VERSION.REFERENCE,
						REQUIREMENT_VERSION.REQUIREMENT_STATUS, INFO_LIST_ITEM.LABEL.as("CATEGORY"),
						RESOURCE.DESCRIPTION)
				.from(REQUIREMENT)
				.innerJoin(REQUIREMENT_LIBRARY_NODE).on(REQUIREMENT_LIBRARY_NODE.RLN_ID.eq(REQUIREMENT.RLN_ID))
				.innerJoin(REQUIREMENT_VERSION).on(REQUIREMENT_VERSION.REQUIREMENT_ID.eq(REQUIREMENT_LIBRARY_NODE.RLN_ID))
				.innerJoin(RESOURCE).on(RESOURCE.RES_ID.eq(REQUIREMENT_VERSION.RES_ID))
				.innerJoin(INFO_LIST_ITEM).on(INFO_LIST_ITEM.ITEM_ID.eq(REQUIREMENT_VERSION.CATEGORY))			
				.innerJoin(MILESTONE_REQ_VERSION).on(MILESTONE_REQ_VERSION.REQ_VERSION_ID.eq(REQUIREMENT_VERSION.RES_ID))
				
				.where(REQUIREMENT_LIBRARY_NODE.PROJECT_ID.eq(projectId)
						.and(MILESTONE_REQ_VERSION.MILESTONE_ID.eq(milestoneId)))
				.fetch().intoMap(REQUIREMENT_VERSION.RES_ID, ReqModel.class);

		return reqList;
		
	}
	
	
	
	
	
	@Override
	public String readMilestoneStatus(Long milestoneId) {
		return dsl.select(MILESTONE.STATUS)
				.from(MILESTONE)
				.where(MILESTONE.MILESTONE_ID.eq(milestoneId))
				.fetchOne().into(String.class);			
	}
	
	@Override
	public List<ReqStepCaseBinding>findTestRequirementBinding(Set<Long> reqId) {
//		select rvc.REQUIREMENT_VERSION_COVERAGE_ID, rvc.VERIFIED_REQ_VERSION_ID, rvc.VERIFYING_TEST_CASE_ID, vs.TEST_STEP_ID
//		from REQUIREMENT_VERSION_COVERAGE rvc
//		RIGHT JOIN VERIFYING_STEPS vs on rvc.REQUIREMENT_VERSION_COVERAGE_ID = vs.REQUIREMENT_VERSION_COVERAGE_ID 
//		where rvc.VERIFIED_REQ_VERSION_ID IN ('7386','7390', '7391', '7397', '7462')
		
		return  dsl.select(REQUIREMENT_VERSION_COVERAGE.REQUIREMENT_VERSION_COVERAGE_ID.as("reqVersionCoverageId"), 
				       REQUIREMENT_VERSION_COVERAGE.VERIFIED_REQ_VERSION_ID.as("resId"),
				       REQUIREMENT_VERSION_COVERAGE.VERIFYING_TEST_CASE_ID.as("tclnId"),
				       VERIFYING_STEPS.TEST_STEP_ID.as("stepId") )
				.from(REQUIREMENT_VERSION_COVERAGE)
				.rightJoin(VERIFYING_STEPS).on(REQUIREMENT_VERSION_COVERAGE.REQUIREMENT_VERSION_COVERAGE_ID.eq(VERIFYING_STEPS.REQUIREMENT_VERSION_COVERAGE_ID))
				.where(REQUIREMENT_VERSION_COVERAGE.VERIFIED_REQ_VERSION_ID.in(reqId))
				.fetchInto(ReqStepCaseBinding.class);
				
				
	}
	
	@Override
	public Map<Long, TestCase> findTestCase(List<Long> tc_ids) {
		//prerequisite, descrption:
//		select tc.prerequisite, tcln.description  from TEST_CASE tc
//		inner join TEST_CASE_LIBRARY_NODE tcln on tcln.tcln_id = tc.tcln_id
//		where tc.tcln_id = '8859';
		
		//même chose avec nombre de pas de test (attention pas le nombre liés à une exigence...)		
//		select tc.tcln_id, tc.prerequisite, tcln.description , count(*) from TEST_CASE tc
//		inner join TEST_CASE_LIBRARY_NODE tcln on tcln.tcln_id = tc.tcln_id
//		inner join TEST_CASE_STEPS tcs on  tcs.test_case_id = tc.tcln_id
//		where tc.tcln_id IN ('8859','8861','8864','8872')
//		group by tc.tcln_id, tc.prerequisite, tcln.description;
		
//le nombre de pas de test liés à une exigence se calcule à partir de => List<ReqStepCaseBinding> liste = reqCollector.findTestRequirementBinding(reqKetSet);		
        return    dsl.select(TEST_CASE.TCLN_ID,
        		TEST_CASE.REFERENCE,
        		TEST_CASE.PREREQUISITE, 				      
            		TEST_CASE_LIBRARY_NODE.DESCRIPTION )
				.from(TEST_CASE)
				.innerJoin(TEST_CASE_LIBRARY_NODE).on(TEST_CASE_LIBRARY_NODE.TCLN_ID.eq(TEST_CASE.TCLN_ID))
				.where(TEST_CASE.TCLN_ID.in(tc_ids))
				.fetch().intoMap(TEST_CASE.TCLN_ID, TestCase.class);
	}
	
	
	@Override
	public Map<Long, Step> findSteps(List<Long> tc_ids) {
//		select atc.test_step_id, atc.expected_result, tcs.step_order  from ACTION_test_step atc
//		inner join TEST_STEP ts on ts.test_step_id = atc.test_step_id
//		inner join TEST_CASE_STEPS tcs on tcs.step_id = ts.test_step_id
//		inner join TEST_CASE tc on tc.tcln_id = tcs.test_case_id
//		where tcln_id IN ('8859','8861','8864','8872')
        return    dsl.select(ACTION_TEST_STEP.TEST_STEP_ID,
        		  ACTION_TEST_STEP.EXPECTED_RESULT, 				      
            		TEST_CASE_STEPS.STEP_ORDER)
				.from(ACTION_TEST_STEP)
				.innerJoin(TEST_STEP).on(TEST_STEP.TEST_STEP_ID.eq(ACTION_TEST_STEP.TEST_STEP_ID))
				.innerJoin(TEST_CASE_STEPS).on(TEST_CASE_STEPS.STEP_ID.eq(TEST_STEP.TEST_STEP_ID))
				.innerJoin(TEST_CASE).on(TEST_CASE.TCLN_ID.eq(TEST_CASE_STEPS.TEST_CASE_ID))
				.where(TEST_CASE.TCLN_ID.in(tc_ids))
				.fetch().intoMap(ACTION_TEST_STEP.TEST_STEP_ID, Step.class);
	}
}
