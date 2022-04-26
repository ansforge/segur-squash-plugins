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
package org.squashtest.tm.plugin.custom.report.segur.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.squashtest.tm.plugin.custom.report.segur.model.BasicReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepCaseBinding;

public interface RequirementsCollector {

	// todelete ?
	public List<ReqModel> findRequirementByProjectAndMilestone(Long projectId, Long milestoneId);

	public List<Cuf> findCUFsByResId(Long resId);
	
	//todelete?
	public List<Cuf> findCUFsForTypeAndByEntityId(String entityType, Long resId);
	
	//lecture de CUF de type 'CF' (valeur dans custom_field_value.value)
	public List<Cuf> findCUFsTypeCFForEntityTypeAndByEntity(String entityType, Long resId);
	
	public String findStepReferenceByTestStepId(Long testStepId);
	
	//public List<Cuf> findCUFsTypeCFForEntityTypeAndByEntity(String entityType, String cufCode, Long resId);
	
	public Map<Long, ReqModel> mapFindRequirementByProjectAndMilestone(Long projectId, Long milestoneId);

	//todelete ?
	public Map<Long, ReqModel> mapFindRequirementByProjectAndMilestoneBRIDEEEEEEE(Long projectId, Long milestoneId);
	
	//TODO => renvoyer une liste pour gérer porprement les cas ou il y a plus qu'une référence ...
	public String readMilestoneStatus(Long milestoneId); 
	
	public List<ReqStepCaseBinding>findTestRequirementBinding(Set<Long> reqId);
}
