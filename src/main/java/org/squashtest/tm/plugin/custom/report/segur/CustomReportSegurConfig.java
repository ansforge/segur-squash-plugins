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
import org.squashtest.tm.api.report.form.composite.MilestonePickerOption;
import org.squashtest.tm.api.report.form.composite.ProjectPickerOption;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomReportSegurConfig {

	@Bean
	public ReportPlugin segurReportPlugin(SegurExcelReport segurReport) {
		Report[] reports = { segurReport };
		ReportPlugin reportPlugin = new ReportPlugin();
		reportPlugin.setReports(reports);
		return reportPlugin;
	}

	@Bean
	public SegurExcelReport segurReport(Form segurForm) {
		SegurExcelReport segurReport = new SegurExcelReport();
		segurReport.setCategory(StandardReportCategory.PREPARATION_PHASE);
		segurReport.setType(StandardReportType.GENERIC);
		segurReport.setLabelKey("title");
		segurReport.setDescriptionKey("description");
		segurReport.setForm(segurForm.getInputs().toArray(new Input[segurForm.getInputs().size()]));
		return segurReport;
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
	    treePicker.setLabelKey("custom.report.demo.picker.label");
	    treePicker.setNodeSelectionLimit(1);
	    treePicker.setRequired(true);
	    treePicker.setStrict(true);
	    return treePicker;
	  }
	
//	@Bean
//	//public Form segurForm(@Named("projectPicker") ProjectPicker projectPicker,@Named("milestonePickerOption") MilestonePicker milestonePickerOption) {
//	public Form segurForm(@Named("milestonePickerOption") MilestonePickerOption milestonePickerOption) {
//		Form form = new Form();
//		List<Input> inputs = new ArrayList();
////		inputs.add(projectPicker);
//		inputs.add((Input) milestonePickerOption);
//		form.setInputs(inputs);
//		return form;
//	}
//
//	@Bean(name = "milestonePickerOption")
//	public MilestonePickerOption milestonePickerOption() {
//		MilestonePickerOption picker = new MilestonePickerOption();
//		picker.setName("milestoneId");
//		picker.setLabelKey("select.milestone");
//		picker.setDefaultSelected(false);
//		return picker;
//	}

//	@Bean(name = "projectPicker")
//	public ProjectPicker projectPicker() {
//		ProjectPicker picker = new ProjectPicker();
//		picker.setName("projectId");
//		picker.setLabelKey("select.project");
//		picker.setRequired(true);
//		return picker;
//	}
}
