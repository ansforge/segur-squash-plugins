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

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.squashtest.tm.api.report.criteria.Criteria;
import org.squashtest.tm.plugin.custom.report.segur.model.CampaignDto;
import org.squashtest.tm.plugin.custom.report.segur.repository.CampaignCollector;
import org.squashtest.tm.plugin.custom.report.segur.service.ReportGeneratorService;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    List<String> headers = Arrays.asList("Name", "Description", "Reference", "Created by");

    @Inject
    CampaignCollector campaignCollector;

    @Override
    public File generateReport(Map<String, Criteria> criterias) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        //récupération des critères
        Map<String,Object> map = (Map<String, Object>) criterias.get("campaignId").getValue();
        List<String> campaigns = (ArrayList)map.get("campaigns");
        Long campaignId = Long.parseLong(campaigns.get(0));
        
        //DAO
        CampaignDto campaign = campaignCollector.findCampaignById(campaignId);

        //A suuprimer ...Ecriture de l'excel
//        printHeaders(sheet);
//        printRow(campaign,sheet);
//        formatColumns(sheet);
        
        //tmp chargement du template et écriture de lignes bidons...
        
        File report = null;
        try {
        	String fileName = ExcelWriterUtil.createOutputFileName(false, "INS", "V1.3");
			report = ExcelWriterUtil.flushToTemporaryFile(workbook,fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return report;
    }

//    private void printHeaders(Sheet sheet) {
//        Row row = sheet.createRow(0);
//        for (int i = 0; i < headers.size(); i++) {
//
//            Cell cell = row.createCell(i);
//            cell.setCellValue(headers.get(i));
//        }
//    }

//    private void printRow(CampaignDto campaign, Sheet sheet) {
//        Row row = sheet.createRow(1);
//        Cell nameCell = row.createCell(0);
//        nameCell.setCellValue(campaign.getName());
//        Cell descriptionCell = row.createCell(1);
//        String description = campaign.getDescription() != null ? campaign.getDescription() : "";
//        descriptionCell.setCellValue(description);
//        Cell referenceCell = row.createCell(2);
//        referenceCell.setCellValue(campaign.getReference());
//        Cell createdByCell = row.createCell(3);
//        createdByCell.setCellValue(campaign.getCreatedBy());
//    }
//
//    private void clean(SXSSFWorkbook workbook) {
//        workbook.dispose();
//    }


//    private void formatColumns(Sheet sheet) {
//        for (int i = 0; i < headers.size(); i++) {
//            sheet.autoSizeColumn(i);
//        }
//    }

  
}
