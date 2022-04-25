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


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.model.BasicReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
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
		String query = "select " + "cuf.code, cufvo.label" + " from custom_field_value cufv "
	
				+ "INNER JOIN custom_field_binding cufb " + "ON cufv.cfb_id = cufb.cfb_id "
				+ "INNER JOIN custom_field cuf " + "ON cuf.cf_id=cufb.cf_id "
				+ "LEFT JOIN custom_field_value_option cufvo " + "ON cufv.cfv_id = cufvo.cfv_id "
				+ "where cufv.bound_entity_type ='REQUIREMENT_VERSION' " + "and cufv.bound_entity_id=?";
		
		List<Cuf> cufs = dsl
				.select(CUSTOM_FIELD.CODE, CUSTOM_FIELD_VALUE_OPTION.LABEL)
				.from(CUSTOM_FIELD_VALUE)
				.innerJoin(CUSTOM_FIELD_BINDING).on(CUSTOM_FIELD_BINDING.CFB_ID.eq(CUSTOM_FIELD_VALUE.CFB_ID))
				
				.innerJoin(CUSTOM_FIELD).on(CUSTOM_FIELD.CF_ID.eq(CUSTOM_FIELD_BINDING.CF_ID))
				.leftJoin(CUSTOM_FIELD_VALUE_OPTION).on(CUSTOM_FIELD_VALUE.CFV_ID.eq(CUSTOM_FIELD_VALUE_OPTION.CFV_ID))
				.where(CUSTOM_FIELD_VALUE.BOUND_ENTITY_TYPE.eq(Constantes.CUF_TYPE_OBJECT)) // REQUIREMENT_VERSION
						.and(CUSTOM_FIELD_VALUE.BOUND_ENTITY_ID.eq(resId))
				.fetchInto(Cuf.class);

		return cufs;
	}

	
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
	
}
