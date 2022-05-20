/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.repository.impl;

import static org.jooq.impl.DSL.nvl2;
import static org.squashtest.tm.jooq.domain.Tables.ACTION_TEST_STEP;
import static org.squashtest.tm.jooq.domain.Tables.CUSTOM_FIELD;
import static org.squashtest.tm.jooq.domain.Tables.CUSTOM_FIELD_BINDING;
import static org.squashtest.tm.jooq.domain.Tables.CUSTOM_FIELD_VALUE;
import static org.squashtest.tm.jooq.domain.Tables.CUSTOM_FIELD_VALUE_OPTION;
import static org.squashtest.tm.jooq.domain.Tables.INFO_LIST_ITEM;
import static org.squashtest.tm.jooq.domain.Tables.MILESTONE;
import static org.squashtest.tm.jooq.domain.Tables.MILESTONE_REQ_VERSION;
import static org.squashtest.tm.jooq.domain.Tables.MILESTONE_TEST_CASE;
import static org.squashtest.tm.jooq.domain.Tables.PROJECT;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT_LIBRARY_NODE;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT_VERSION;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT_VERSION_COVERAGE;
import static org.squashtest.tm.jooq.domain.Tables.REQUIREMENT_VERSION_LINK;
import static org.squashtest.tm.jooq.domain.Tables.RESOURCE;
import static org.squashtest.tm.jooq.domain.Tables.TCLN_RELATIONSHIP_CLOSURE;
import static org.squashtest.tm.jooq.domain.Tables.TEST_CASE;
import static org.squashtest.tm.jooq.domain.Tables.TEST_CASE_LIBRARY_NODE;
import static org.squashtest.tm.jooq.domain.Tables.TEST_CASE_STEPS;
import static org.squashtest.tm.jooq.domain.Tables.TEST_STEP;
import static org.squashtest.tm.jooq.domain.Tables.VERIFYING_STEPS;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.LinkedReq;
import org.squashtest.tm.plugin.custom.report.segur.model.PerimeterData;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepBinding;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;
import org.squashtest.tm.plugin.custom.report.segur.repository.RequirementsCollector;

@Repository
public class RequirementsCollectorImpl implements RequirementsCollector {

	@Inject
	private DSLContext dsl;

	@Override
	public PerimeterData findMilestoneByMilestoneId(Long milestoneId) {
		return dsl.select(MILESTONE.LABEL.as("milestoneName"), MILESTONE.STATUS.as("milestoneStatus")).from(MILESTONE)
				.where(MILESTONE.MILESTONE_ID.eq(milestoneId)).fetchOne().into(PerimeterData.class);
	}

	@Override
	public String findProjectNameByProjectId(Long projectId) {
		return dsl.select(PROJECT.NAME).from(PROJECT).where(PROJECT.PROJECT_ID.eq(projectId))
				.fetchOneInto(String.class);
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
				.select(CUSTOM_FIELD.CODE, nvl2(CUSTOM_FIELD_VALUE_OPTION.LABEL, CUSTOM_FIELD_VALUE_OPTION.LABEL, ""))
				.from(CUSTOM_FIELD_VALUE).innerJoin(CUSTOM_FIELD_BINDING)
				.on(CUSTOM_FIELD_BINDING.CFB_ID.eq(CUSTOM_FIELD_VALUE.CFB_ID))

				.innerJoin(CUSTOM_FIELD).on(CUSTOM_FIELD.CF_ID.eq(CUSTOM_FIELD_BINDING.CF_ID))
				.leftJoin(CUSTOM_FIELD_VALUE_OPTION).on(CUSTOM_FIELD_VALUE.CFV_ID.eq(CUSTOM_FIELD_VALUE_OPTION.CFV_ID))
				.where(CUSTOM_FIELD_VALUE.BOUND_ENTITY_TYPE.eq(Constantes.CUF_TYPE_OBJECT_REQ)) // REQUIREMENT_VERSION
				.and(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID.eq(resId)).fetchInto(Cuf.class);

		return cufs;
	}

	@Override
	public List<String> findPointsDeVerificationByTcStepsIds(List<Long> steps) {
//		select cfv.large_value, tcs.step_order from custom_field_value cfv
//		inner join custom_field_binding cfb on cfb.cfb_id = cfv.cfb_id
//		inner join custom_field cf on cfb.cf_id = cf.cf_id
//		inner join test_case_steps tcs on tcs.step_id = cfv.bound_entity_id
//		where cfv.bound_entity_type ='TEST_STEP' and cfv.field_type = 'RTF'
//		and cf.code='VERIF_PREUVE'
//		and cfv.bound_entity_id in ('10754','10755', '10756')
//		order by tcs.step_order ASC

		return dsl.select(nvl2(CUSTOM_FIELD_VALUE.LARGE_VALUE, CUSTOM_FIELD_VALUE.LARGE_VALUE, ""))
				.from(CUSTOM_FIELD_VALUE).innerJoin(CUSTOM_FIELD_BINDING)
				.on(CUSTOM_FIELD_BINDING.CFB_ID.eq(CUSTOM_FIELD_VALUE.CFB_ID)).innerJoin(CUSTOM_FIELD)
				.on(CUSTOM_FIELD.CF_ID.eq(CUSTOM_FIELD_BINDING.CF_ID)).innerJoin(TEST_CASE_STEPS)
				.on(TEST_CASE_STEPS.STEP_ID.eq(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID))
				.where(CUSTOM_FIELD_VALUE.BOUND_ENTITY_TYPE.eq(Constantes.CUF_TYPE_OBJECT_TEST_STEP))
				.and(CUSTOM_FIELD.FIELD_TYPE.eq(Constantes.CUF_FIELD_TYPE_RTF))
				.and(CUSTOM_FIELD.CODE.eq(Constantes.VERIF_PREUVE)).and(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID.in(steps))
				.orderBy(TEST_CASE_STEPS.STEP_ORDER.asc()).fetchInto(String.class);

	}

	@Override
	public String findStepReferenceByTestStepId(Long testStepId) {
		return dsl.select(nvl2(CUSTOM_FIELD_VALUE.VALUE, CUSTOM_FIELD_VALUE.VALUE, "")).from(CUSTOM_FIELD_VALUE)
				.innerJoin(CUSTOM_FIELD_BINDING).on(CUSTOM_FIELD_BINDING.CFB_ID.eq(CUSTOM_FIELD_VALUE.CFB_ID))
				.innerJoin(CUSTOM_FIELD).on(CUSTOM_FIELD.CF_ID.eq(CUSTOM_FIELD_BINDING.CF_ID))
				.where(CUSTOM_FIELD_VALUE.BOUND_ENTITY_TYPE.eq(Constantes.CUF_TYPE_OBJECT_TEST_STEP))
				.and(CUSTOM_FIELD.FIELD_TYPE.eq(Constantes.CUF_FIELD_TYPE_CF))
				.and(CUSTOM_FIELD.CODE.eq(Constantes.REF_PREUVE)).and(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID.eq(testStepId))
				.fetchOneInto(String.class);

	}

	@Override
	public Map<Long, ReqModel> mapFindRequirementByResId(Set<Long> resIds) {
//		select rv.RES_ID, rv.REFERENCE, rv.REQUIREMENT_STATUS, ili.LABEL as category, res.DESCRIPTION
//		from c rv
//		inner Join RESOURCE res on res.RES_ID = rv.RES_ID
//		inner Join INFO_LIST_ITEM ili on ili.ITEM_ID = rv.CATEGORY	
//		where rv.RES_ID in ('11008', '11010', '11011', '11012', '11013', '11016', '11017', '11018', '10975', '10988', '10977', '10976', '10978', '10983')

		return dsl
				.select(REQUIREMENT_VERSION.RES_ID, REQUIREMENT_VERSION.REFERENCE,
						REQUIREMENT_VERSION.REQUIREMENT_STATUS, INFO_LIST_ITEM.LABEL.as("CATEGORY"),
						RESOURCE.DESCRIPTION)
				.from(REQUIREMENT_VERSION).innerJoin(RESOURCE).on(RESOURCE.RES_ID.eq(REQUIREMENT_VERSION.RES_ID))
				.innerJoin(INFO_LIST_ITEM).on(INFO_LIST_ITEM.ITEM_ID.eq(REQUIREMENT_VERSION.CATEGORY))
				.where(REQUIREMENT_VERSION.RES_ID.in(resIds)).fetch()
				.intoMap(REQUIREMENT_VERSION.RES_ID, ReqModel.class);
	}

//	@Override
//	public List<Long> findReqWithMultiplelinks(Set<Long> reqIds) {
//		select tmp.reqId from (
//				select rvl.requirement_version_id as reqId, count(*) as nb from requirement_version_link rvl
//				inner join requirement_version rv on rv.res_id = rvl.related_requirement_version_id
//				where rvl.requirement_version_id in ('11010','11011','11016','11017','11018')
//				and rv.reference like 'SC%' 
//				group by rvl.requirement_version_id) tmp
//					 where tmp.nb !=1
//		Table<?> nested =
//			    create.select(REQUIREMENT_VERSION_LINK.REQUIREMENT_VERSION_ID, count().as("nb"))
//			          .from(REQUIREMENT_VERSION_LINK)
//			          .groupBy(BOOK.AUTHOR_ID).asTable("nested");
//
//		return dsl
//		  
//	}

	@Override
	public List<LinkedReq> findLinkedReq(Long projectId, Long milestoneId) {
//		select rv.RES_ID,  rvl.related_requirement_version_id
//		from REQUIREMENT r
//		inner Join REQUIREMENT_LIBRARY_NODE rln on rln.RLN_ID = r.RLN_ID
//		inner Join REQUIREMENT_VERSION rv on rv.REQUIREMENT_ID = rln.RLN_ID
//		inner Join MILESTONE_REQ_VERSION mrv on mrv.REQ_VERSION_ID = rv.RES_ID
//		left join requirement_version_link rvl on rvl.requirement_version_id = rv.res_id 
//		where rln.PROJECT_ID = '39'
//				and mrv.MILESTONE_ID = '20'
		return dsl
				.select(REQUIREMENT_VERSION.RES_ID.as("resId"),
						REQUIREMENT_VERSION_LINK.RELATED_REQUIREMENT_VERSION_ID.as("socleResId"))
				.from(REQUIREMENT).innerJoin(REQUIREMENT_LIBRARY_NODE)
				.on(REQUIREMENT_LIBRARY_NODE.RLN_ID.eq(REQUIREMENT.RLN_ID)).innerJoin(REQUIREMENT_VERSION)
				.on(REQUIREMENT_VERSION.REQUIREMENT_ID.eq(REQUIREMENT_LIBRARY_NODE.RLN_ID))
				.innerJoin(MILESTONE_REQ_VERSION)
				.on(MILESTONE_REQ_VERSION.REQ_VERSION_ID.eq(REQUIREMENT_VERSION.RES_ID))
				.leftJoin(REQUIREMENT_VERSION_LINK)
				.on(REQUIREMENT_VERSION_LINK.REQUIREMENT_VERSION_ID.eq(REQUIREMENT_VERSION.RES_ID))
				.where(REQUIREMENT_LIBRARY_NODE.PROJECT_ID.eq(projectId)
						.and(MILESTONE_REQ_VERSION.MILESTONE_ID.eq(milestoneId)))
				.fetchInto(LinkedReq.class);

	}

	@Override
	public List<ReqStepBinding> findTestRequirementBindingFiltreJalonTC(Set<Long> reqId, Long milestoneId) {
//		select rvc.REQUIREMENT_VERSION_COVERAGE_ID, rvc.VERIFIED_REQ_VERSION_ID, rvc.VERIFYING_TEST_CASE_ID, vs.TEST_STEP_ID
//		from REQUIREMENT_VERSION_COVERAGE rvc
//		INNER JOIN TEST_CASE tc on tc.tcln_id = rvc.VERIFYING_TEST_CASE_ID
//		INNER JOIN MILESTONE_TEST_CASE mtc ON mtc.test_case_id = tc.tcln_id
//		LEFT JOIN VERIFYING_STEPS vs on rvc.REQUIREMENT_VERSION_COVERAGE_ID = vs.REQUIREMENT_VERSION_COVERAGE_ID 
//		where rvc.VERIFIED_REQ_VERSION_ID IN ('7386','7390', '7391', '7397', '7462') and mtc.MILESTONE_ID = '19'

		return dsl
				.select(REQUIREMENT_VERSION_COVERAGE.REQUIREMENT_VERSION_COVERAGE_ID.as("reqVersionCoverageId"),
						REQUIREMENT_VERSION_COVERAGE.VERIFIED_REQ_VERSION_ID.as("resId"),
						REQUIREMENT_VERSION_COVERAGE.VERIFYING_TEST_CASE_ID.as("tclnId"),
						VERIFYING_STEPS.TEST_STEP_ID.as("stepId"))
				.from(REQUIREMENT_VERSION_COVERAGE).innerJoin(TEST_CASE)
				.on(TEST_CASE.TCLN_ID.eq(REQUIREMENT_VERSION_COVERAGE.VERIFYING_TEST_CASE_ID))
				.innerJoin(MILESTONE_TEST_CASE).on(MILESTONE_TEST_CASE.TEST_CASE_ID.eq(TEST_CASE.TCLN_ID))
				.leftJoin(VERIFYING_STEPS)
				.on(REQUIREMENT_VERSION_COVERAGE.REQUIREMENT_VERSION_COVERAGE_ID
						.eq(VERIFYING_STEPS.REQUIREMENT_VERSION_COVERAGE_ID))
				.where(REQUIREMENT_VERSION_COVERAGE.VERIFIED_REQ_VERSION_ID.in(reqId))
				.and(MILESTONE_TEST_CASE.MILESTONE_ID.eq(milestoneId)).fetchInto(ReqStepBinding.class);
	}

	@Override
	public Map<Long, TestCase> findTestCase(List<Long> tc_ids) {
		// prerequisite, descrption:
//		select tc.prerequisite, tcln.description  from TEST_CASE tc
//		inner join TEST_CASE_LIBRARY_NODE tcln on tcln.tcln_id = tc.tcln_id
//		where tc.tcln_id = '8859';

//le nombre de pas de test liés à une exigence se calcule à partir de => List<ReqStepCaseBinding> liste = reqCollector.findTestRequirementBinding(reqKetSet);		
		return dsl
				.select(TEST_CASE.TCLN_ID, TEST_CASE.REFERENCE, TEST_CASE.PREREQUISITE,
						TEST_CASE_LIBRARY_NODE.DESCRIPTION, TEST_CASE.TC_STATUS)
				.from(TEST_CASE).innerJoin(TEST_CASE_LIBRARY_NODE)
				.on(TEST_CASE_LIBRARY_NODE.TCLN_ID.eq(TEST_CASE.TCLN_ID)).where(TEST_CASE.TCLN_ID.in(tc_ids)).fetch()
				.intoMap(TEST_CASE.TCLN_ID, TestCase.class);
	}

	@Override
	public List<Long> findStepIdsByTestCaseId(Long tc_id) {
//		select tcs.step_id, tcs.step_order from TEST_CASE tc
//		inner join TEST_CASE_LIBRARY_NODE tcln on tcln.tcln_id = tc.tcln_id
//		inner join TEST_CASE_STEPS tcs on  tcs.test_case_id = tc.tcln_id
//		where tc.tcln_id ='8864' --IN ('8859','8861','8864','8872')
//		order by tcs.step_order ASC
		return dsl.select(TEST_CASE_STEPS.STEP_ID).from(TEST_CASE).innerJoin(TEST_CASE_LIBRARY_NODE)
				.on(TEST_CASE_LIBRARY_NODE.TCLN_ID.eq(TEST_CASE.TCLN_ID)).innerJoin(TEST_CASE_STEPS)
				.on(TEST_CASE_STEPS.TEST_CASE_ID.eq(TEST_CASE.TCLN_ID)).where(TEST_CASE.TCLN_ID.in(tc_id))
				.orderBy(TEST_CASE_STEPS.STEP_ORDER.asc()).fetchInto(Long.class);
	}

	@Override
	public Map<Long, Step> findSteps(List<Long> tc_ids) {
//		select atc.test_step_id, atc.expected_result, tcs.step_order  from ACTION_test_step atc
//		inner join TEST_STEP ts on ts.test_step_id = atc.test_step_id
//		inner join TEST_CASE_STEPS tcs on tcs.step_id = ts.test_step_id
//		inner join TEST_CASE tc on tc.tcln_id = tcs.test_case_id
//		where tcln_id IN ('8859','8861','8864','8872')
		return dsl.select(ACTION_TEST_STEP.TEST_STEP_ID, ACTION_TEST_STEP.EXPECTED_RESULT, TEST_CASE_STEPS.STEP_ORDER)
				.from(ACTION_TEST_STEP).innerJoin(TEST_STEP)
				.on(TEST_STEP.TEST_STEP_ID.eq(ACTION_TEST_STEP.TEST_STEP_ID)).innerJoin(TEST_CASE_STEPS)
				.on(TEST_CASE_STEPS.STEP_ID.eq(TEST_STEP.TEST_STEP_ID)).innerJoin(TEST_CASE)
				.on(TEST_CASE.TCLN_ID.eq(TEST_CASE_STEPS.TEST_CASE_ID)).where(TEST_CASE.TCLN_ID.in(tc_ids)).fetch()
				.intoMap(ACTION_TEST_STEP.TEST_STEP_ID, Step.class);

	}

	// 2 méthodes suivantes => liste des CTs coeur de métier à exclure
//	--1°) recup du tcln_id du folder '_METIER'
//	--2° recup des IDs de tous les CTs qui sont sous le dossier '_METIER'

	// 1°)
	@Override
	public Long findIdFolderMetier(Long projectId) {
//		select * from test_case_library_node where name = '_METIER' and project_id='19'   folder racine tcln_id = 8857
		return dsl.select(TEST_CASE_LIBRARY_NODE.TCLN_ID).from(TEST_CASE_LIBRARY_NODE)
				.where(TEST_CASE_LIBRARY_NODE.NAME.eq(Constantes.FOLDER_CT_METIER))
				.and(TEST_CASE_LIBRARY_NODE.PROJECT_ID.eq(projectId)).fetchOneInto(Long.class);
	}

	// 2°) pour lesite des CTS coeur de métier
	@Override
	public List<Long> findCoeurMetierIdsByRootTcln_Id(Long rootTcln_id) {
//		select descendant_id from tcln_relationship_closure trc 
//		   inner join test_case_library_node tcln on tcln.tcln_id = trc.descendant_id
//		   inner join TEST_CASE tc on tc.tcln_id = tcln.tcln_id --ne garder que les CTs
//		   where trc.ancestor_id = '8857' and trc.depth !=0
		return dsl.select(TCLN_RELATIONSHIP_CLOSURE.DESCENDANT_ID).from(TCLN_RELATIONSHIP_CLOSURE)
				.innerJoin(TEST_CASE_LIBRARY_NODE)
				.on(TEST_CASE_LIBRARY_NODE.TCLN_ID.eq(TCLN_RELATIONSHIP_CLOSURE.DESCENDANT_ID)).innerJoin(TEST_CASE)
				.on(TEST_CASE.TCLN_ID.eq(TEST_CASE_LIBRARY_NODE.TCLN_ID))
				.where(TCLN_RELATIONSHIP_CLOSURE.ANCESTOR_ID.eq(rootTcln_id))
				// .and(TCLN_RELATIONSHIP_CLOSURE.DEPTH.ne(0))
				.fetchInto(Long.class);

	}
}
