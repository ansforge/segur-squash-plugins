/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.squashtest.tm.api.report.Report;
import org.squashtest.tm.api.report.ReportPlugin;
import org.squashtest.tm.api.report.StandardReportCategory;
import org.squashtest.tm.api.report.StandardReportType;
import org.squashtest.tm.api.report.form.Form;
import org.squashtest.tm.api.report.form.Input;
import org.squashtest.tm.api.report.form.OptionInput;
import org.squashtest.tm.api.report.form.RadioButtonsGroup;
import org.squashtest.tm.api.report.form.composite.MilestonePickerOption;
import org.squashtest.tm.api.report.form.composite.ProjectPickerOption;


/**
 * The Class CustomReportSegurConfig.
 */
@Configuration
public class CustomReportSegurConfig {

	/**
	 * Segur report plugin.
	 *
	 * @param segurReport the segur report
	 * @return the report plugin
	 */
	@Bean
	public ReportPlugin segurReportPlugin(SegurExcelReport segurReport) {
		Report[] reports = { segurReport };
		ReportPlugin reportPlugin = new ReportPlugin();
		reportPlugin.setReports(reports);
		return reportPlugin;
	}

	/**
	 * Segur report.
	 *
	 * @param segurForm the segur form
	 * @return the segur excel report
	 */
	@Bean
	public SegurExcelReport segurReport(Form segurForm) {
		SegurExcelReport segurReport = new SegurExcelReport();
		segurReport.setCategory(StandardReportCategory.PREPARATION_PHASE);
		segurReport.setType(StandardReportType.SPECIFICATION_BOOK); // GENERIC
		segurReport.setLabelKey("title");
		segurReport.setDescriptionKey("description");
		segurReport.setForm(segurForm.getInputs().toArray(new Input[segurForm.getInputs().size()]));
		return segurReport;
	}
	/**
	 * Segur form.
	 *
	 * @param segurRadioButton the segur radio button
	 * @return the form
	 */
	@Bean
	public Form segurForm(@Named("segurRadioButton") RadioButtonsGroup segurRadioButton) {//,
	//					  @Named("templateSelectionRadioButton") RadioButtonsGroup templateSelectionRadioButton) {
		Form form = new Form();
		List<Input> inputs = new ArrayList<Input>();
		inputs.add(segurRadioButton);
		//inputs.add(templateSelectionRadioButton);
		form.setInputs(inputs);
		return form;
	}

	/**
	 * Segur radio button.
	 *
	 * @param milestonePickerOption the milestone picker option
	 * @param projectPickerOption the project picker option
	 * @return the radio buttons group
	 */
	@Bean(name = "segurRadioButton")
	public RadioButtonsGroup segurRadioButton(
			@Named("milestonePickerOption") MilestonePickerOption milestonePickerOption,
			@Named("projectPickerOption") ProjectPickerOption projectPickerOption) {
		RadioButtonsGroup button = new RadioButtonsGroup();
		button.setLabelKey("button.label.key");
		button.setName("segurSelectionMode");
		button.setRequired(true);
		List<OptionInput> options = new ArrayList<OptionInput>();
		options.add(projectPickerOption);
		options.add(milestonePickerOption);
		button.setOptions(options);
		return button;

	}

	/**
	 * Milestone picker option.
	 *
	 * @return the milestone picker option
	 */
	@Bean(name = "milestonePickerOption")
	public MilestonePickerOption milestonePickerOption() {
		MilestonePickerOption picker = new MilestonePickerOption();
		picker.setLabelKey("select.milestone");
		picker.setPickerLabelKey("select.milestone");
		picker.setPickerName("milestones"); //
		picker.setDefaultSelected(false);
		return picker;
	}

	/**
	 * Project picker option.
	 *
	 * @return the project picker option
	 */
	@Bean(name = "projectPickerOption")
	public ProjectPickerOption projectPickerOption() {
		ProjectPickerOption picker = new ProjectPickerOption();
		picker.setLabelKey("select.project");
		picker.setPickerLabelKey("select.project");
		picker.setPickerName("projects");
		picker.setDefaultSelected(true);
		return picker;
	}

	@Bean(name = "templateSelectionRadioButton")
	public RadioButtonsGroup templateSelectionRadioButton(
			@Named("templateDevOption") OptionInput templateDevOption,
			@Named("templateRemRcOption") OptionInput templateRemRcOption
	) {
		RadioButtonsGroup button2 = new RadioButtonsGroup();
		button2.setLabelKey("button2.label.key");
		button2.setName("templateSelectionMode");
		button2.setRequired(true);
		List<OptionInput> options = new ArrayList<OptionInput>();
		options.add(templateDevOption);
		options.add(templateRemRcOption);
		button2.setOptions(options);
		return button2;
	}
	/**
	 * Template dev option.
	 *
	 * @return the template dev option
	 */
	@Bean(name = "templateDevOption")
	public OptionInput templateDevOption() {
		OptionInput devOption = new OptionInput();
		devOption.setLabelKey("dev.template.label");
		devOption.setValue("TEST");
		devOption.setGivesAccessTo("TEST"); //
		devOption.setDefaultSelected(false);
		return devOption;
	}

	/**
	 * Template RemRC option.
	 *
	 * @return the template RemRC option
	 */
	@Bean(name = "templateRemRcOption")
	public OptionInput templateRemRcOption() {
		OptionInput remRCOption = new OptionInput();
		remRCOption.setLabelKey("remrc.template.label");
		remRCOption.setValue("TEST2");
		remRCOption.setGivesAccessTo("TEST2");
		remRCOption.setDefaultSelected(true);
		return remRCOption;
	}
}
