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
import org.squashtest.tm.plugin.custom.report.segur.model.BasicReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
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

	@Override
	public File generateReport(Map<String, Criteria> criterias) {
		LOGGER.error(" *********  generateReportxxx ***************");
		// récupération des critères
		// Map<String,Object> map = (Map<String, Object>)
		// criterias.get("milestoneId").getValue();
		// Map<String,Object> map = (Map<String, Object>)
		// criterias.get("campaigns").getValue();
		// List<String> milestone = (ArrayList)map.get("campaigns");
		// Long campaignId = Long.parseLong(campaigns.get(0));

		// ICI test DAO OK
//		List<ReqModel> reqs = reqCollector.findRequirementByProjectAndMilestone(12L, 13L);
//		LOGGER.error(" *********  test DAO ***************");
//		LOGGER.error(" *********  reqs size: " + reqs.size());
//		LOGGER.error(" *********  reqs 0 descrip: " + reqs.get(0).getDescription());
//		LOGGER.error(" *********  reqs 0 resId: " + reqs.get(0).getResId());
//		LOGGER.error(" *********  reqs 1 resId: " + reqs.get(1).getResId());
//		LOGGER.error(" *********  reqs 2 resId: " + reqs.get(2).getResId());
//		LOGGER.error(" *********  reqs 3 resId: " + reqs.get(3).getResId());
		
		//Recup de la liste des exigences
//   	List<Long> reqIDList = reqs.stream().map(BasicReqModel::getResId).collect(Collectors.toList());
//   	LOGGER.error(" *********  recup des IDs **********************");
//   	for (Long long1 : reqIDList) {
//   		LOGGER.error(" *********  id recup: " + long1);
//   		
//	}

		//lecture du statut du jalon => mode publication ou prépublication
		LOGGER.error(" *********  LEcture du statut du jalon IN ***************");
		String milestoneStatus = reqCollector.readMilestoneStatus(19L);
		LOGGER.error(" *********  statut du jalon : " + milestoneStatus);
		
		

		
		//essai avec la Map
		LOGGER.error(" *********  test DAO avec MAp IN ***************");

		//Map<Long, ReqModel> reqs = reqCollector.mapFindRequirementByProjectAndMilestoneBRIDEEEEEEE(12L, 13L);
		Map<Long, ReqModel> reqs = reqCollector.mapFindRequirementByProjectAndMilestone(19L, 19L);
		
		LOGGER.error(" *********  test DAO avec MAp OUT OK ***************");
		LOGGER.error(" *********  map size: " + reqs.size());
		LOGGER.error(" *********  map get ref 7386: " + reqs.get(7386L).getReference());
		LOGGER.error(" *********  map get ref 7390: " + reqs.get(7390L).getReference());
	
		Set<Long> reqKetSet = reqs.keySet();
		
		for (Long res_id : reqKetSet) {
			List<Cuf> cufs = reqCollector.findCUFsByResId(res_id);
			LOGGER.error(" *********  map lecture cuf res_id, size: " + res_id + " /"  + cufs.size());
			reqs.get(res_id).setCufs(cufs);
			//mies à jour de ExcelData pour l'exigence (hors CT ....)
			reqs.get(res_id).updateData();
		}
		
		
		
		
		// tmp chargement du template et écriture de lignes bidons...
		LOGGER.error(" *********  appel chargement du template yyy ***************");

		
	//	ExcelWriterUtil util = new ExcelWriterUtil();

		excel.loadWorkbookTemplate();
		
		//excel.putDatasInWorkbook((List<ReqModel>) reqs.values());
		excel.putDatasInWorkbook(new ArrayList<ReqModel> (reqs.values()));
		
		LOGGER.error(" *********  ecriture dans le fichier ***************");
		// ecriture dans le fichier
		File report = null;
		try {
			String fileName = excel.createOutputFileName(false, "INS", "V1.3");
			report = excel.flushToTemporaryFile(excel.getWorkbook(), fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return report;
	}

}
