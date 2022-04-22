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
package org.squashtest.tm.plugin.custom.report.segur;

//import java.util.ArrayList;
//import java.util.List;
//
//import javax.inject.Named;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.squashtest.tm.api.report.Report;
//import org.squashtest.tm.api.report.ReportPlugin;
//import org.squashtest.tm.api.report.StandardReportCategory;
//import org.squashtest.tm.api.report.StandardReportType;
//import org.squashtest.tm.api.report.form.Form;
//import org.squashtest.tm.api.report.form.Input;
//import org.squashtest.tm.api.report.form.NodeType;
//import org.squashtest.tm.api.report.form.TreePicker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.squashtest.tm.api.report.*;
import org.squashtest.tm.api.report.form.*;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomReportSegurConfig {

	 @Bean
	  public ReportPlugin demoReportPlugin(SegurExcelReport demoReport){
	    Report[] reports = {demoReport};
	    ReportPlugin reportPlugin = new ReportPlugin();
	    reportPlugin.setReports(reports);
	    return reportPlugin;
	  }

	  @Bean
	  public SegurExcelReport demoReport(Form demoForm){
		  SegurExcelReport demoReport = new SegurExcelReport();
	    demoReport.setCategory(StandardReportCategory.PREPARATION_PHASE);
	    demoReport.setType(StandardReportType.GENERIC);
	    demoReport.setLabelKey("title");
	    demoReport.setDescriptionKey("description");
	    demoReport.setForm(demoForm.getInputs().toArray(new Input[demoForm.getInputs().size()]));
	    return demoReport;
	  }


	  @Bean
	  public Form demoForm(@Named("demoCampaignTreePricker") TreePicker campaignTreePicker) {
	    Form form = new Form();
	    List<Input> inputs = new ArrayList();
	    inputs.add(campaignTreePicker);
	    form.setInputs(inputs);
	    return form;
	  }

	  @Bean(name = "demoCampaignTreePricker")
	  public TreePicker campaignTreePricker(){
	    TreePicker treePicker = new TreePicker();
	    treePicker.setPickedNodeType(NodeType.CAMPAIGN);
	    treePicker.setName("campaignId");
	    treePicker.setLabelKey("select.milestone");
	    treePicker.setNodeSelectionLimit(1);
	    treePicker.setRequired(true);
	    treePicker.setStrict(true);
	    return treePicker;
	    
	  }
//	 @Bean
//	  public ReportPlugin demoReportPlugin(SegurExcelReport demoReport){
//	    Report[] reports = {demoReport};
//	    ReportPlugin reportPlugin = new ReportPlugin();
//	    reportPlugin.setReports(reports);
//	    return reportPlugin;
//	  }
//
//
//  @Bean
//  public SegurExcelReport report(Form form){
//    SegurExcelReport report = new SegurExcelReport();
//    report.setCategory(StandardReportCategory.PREPARATION_PHASE);
//    report.setType(StandardReportType.GENERIC);
//    report.setLabelKey("title");
//    report.setDescriptionKey("description");
//    report.setForm(form.getInputs().toArray(new Input[form.getInputs().size()]));
//    return report;
//  }
//
//
//  @Bean
//  public Form demoForm(@Named("demoCampaignTreePricker") TreePicker campaignTreePicker) {
//    Form form = new Form();
//    List<Input> inputs = new ArrayList();
//    inputs.add(campaignTreePicker);
//    form.setInputs(inputs);
//    return form;
//  }
//
//  @Bean(name = "demoCampaignTreePricker")
//  public TreePicker campaignTreePricker(){
//    TreePicker treePicker = new TreePicker();
//    treePicker.setPickedNodeType(NodeType.CAMPAIGN);
//    treePicker.setName("campaignId");
//    treePicker.setLabelKey("custom.report.demo.picker.label");
//    treePicker.setNodeSelectionLimit(1);
//    treePicker.setRequired(true);
//    treePicker.setStrict(true);
//    return treePicker;
//  }
//  
//  @Bean
//  public Form form(/*@Named("projectTreePricker") ProjectPicker projectTreePricker,*/@Named("milestonePickerOption") MilestonePicker milestonePickerOption) {
//    Form form = new Form();
//    List<Input> inputs = new ArrayList<Input>();
//  //  inputs.add((Input) projectTreePricker);
//    inputs.add((Input) milestonePickerOption);
//    form.setInputs(inputs);
//    return form;
//  }
//
//  @Bean(name = "projectTreePricker")
//  public ProjectPicker projectPickerOption(){
//	ProjectPicker treePicker = new ProjectPicker();
//    treePicker.setName("projectId");
//    treePicker.setLabelKey("select.project");
//    treePicker.setRequired(true);
//    return treePicker;
//  }
//  
//  @Bean(name = "milestonePickerOption")
//  public MilestonePicker milestonePickerOption(){
//	  MilestonePicker treePicker = new MilestonePicker();
//    treePicker.setName("milestoneId");
//    treePicker.setLabelKey("select.milestone");
//    treePicker.setRequired(true);
//    return treePicker;
//  }

  
//  @Bean(name = "projectTreePricker")
//  public ProjectPickerOption projectPickerOption(){
//	  ProjectPickerOption treePicker = new ProjectPickerOption();
//    treePicker.setName("projectId");
//    treePicker.setLabelKey("select.project");
//    treePicker.setDefaultSelected(true);
//    return treePicker;
//  }
//  
//  @Bean(name = "milestonePickerOption")
//  public MilestonePickerOption milestonePickerOption(){
//	  MilestonePickerOption treePicker = new MilestonePickerOption();
//    treePicker.setName("milestoneId");
//    treePicker.setLabelKey("select.milestone");
//    //treePicker.setRequired(true);
//    treePicker.setDefaultSelected(false);
//    return treePicker;
//  }
}
