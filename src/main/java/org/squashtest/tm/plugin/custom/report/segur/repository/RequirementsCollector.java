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

import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ExtractedData;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepCaseBinding;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;


public interface RequirementsCollector {

	public ExtractedData findMilestoneByMilestoneId(Long milestoneId);
	
	
	public String findProjectNameByProjectId(Long projectId);
	

	public List<Cuf> findCUFsByResId(Long resId);
	
	//todelete?
//	public List<Cuf> findCUFsForTypeAndByEntityId(String entityType, Long resId);
	
	//lecture de CUF de type 'CF' (valeur dans custom_field_value.value)
	public List<Cuf> findCUFsTypeCFForEntityTypeAndByEntity(String entityType, Long resId);
	
	public String findStepReferenceByTestStepId(Long testStepId);
	
	//public List<Cuf> findCUFsTypeCFForEntityTypeAndByEntity(String entityType, String cufCode, Long resId);
	
	public Map<Long, ReqModel> mapFindRequirementByProjectAndMilestone(Long projectId, Long milestoneId);

	//todelete ?
	public Map<Long, ReqModel> mapFindRequirementByProjectAndMilestoneBRIDEEEEEEE(Long projectId, Long milestoneId);
	
	//public String readMilestoneStatus(Long milestoneId); 
	
	// Tableau lien Exigence - CT-Step (sans filtre Jalon sur CT)
	public List<ReqStepCaseBinding> findTestRequirementBinding(Set<Long> reqId);
	// Tableau lien Exigence - CT-Step (avec filtre Jalon sur CT)
	//public List<ReqStepCaseBinding> findTestRequirementBindingFiltreJalonTC(Set<Long> reqId);
	
	
	public Map<Long, TestCase> findTestCase(List<Long> tc_ids) ;
	
	public Map<Long, Step> findSteps(List<Long> tc_ids);
	
	//test parcours de l'arbre
	public Long findIdFolderMetier(Long projectId);
	
	public List<Long> findCoeurMetierIdsByRootTcln_Id(Long rootTcln_id);

}
