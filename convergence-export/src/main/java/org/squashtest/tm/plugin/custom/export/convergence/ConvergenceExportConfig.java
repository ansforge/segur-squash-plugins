/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence;

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
public class ConvergenceExportConfig {

	/**
	 * Segur report plugin.
	 *
	 * @param segurReport the segur report
	 * @return the report plugin
	 */
	@Bean
	public ReportPlugin segurConvergencePlugin(SegurConvergenceReport convergenceExport) {
		Report[] reports = { convergenceExport };
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
	public SegurConvergenceReport convergenceExport(Form convergenceForm) {
		SegurConvergenceReport convergenceExport = new SegurConvergenceReport();
		convergenceExport.setCategory(StandardReportCategory.PREPARATION_PHASE);
		convergenceExport.setType(StandardReportType.SPECIFICATION_BOOK); // GENERIC
		convergenceExport.setLabelKey("ans.convergence.export.title");
		convergenceExport.setDescriptionKey("ans.convergence.export.description");
		convergenceExport.setForm(convergenceForm.getInputs().toArray(new Input[convergenceForm.getInputs().size()]));
		return convergenceExport;
	}
	/**
	 * Segur form.
	 *
	 * @param segurRadioButton the segur radio button
	 * @return the form
	 */
	@Bean
	public Form convergenceForm(@Named("convergenceRadioButton") RadioButtonsGroup convergenceRadioButton) {
		Form form = new Form();
		List<Input> inputs = new ArrayList<Input>();
		inputs.add(convergenceRadioButton);
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
	@Bean(name = "convergenceRadioButton")
	public RadioButtonsGroup convergenceRadioButton(
			@Named("milestonePickerExport") MilestonePickerOption milestonePickerExport,
			@Named("projectPickerExport") ProjectPickerOption projectPickerExport) {
		RadioButtonsGroup button = new RadioButtonsGroup();
		button.setLabelKey("ans.convergence.export.button.label.key");
		button.setName("segurSelectionMode");
		button.setRequired(true);
		List<OptionInput> options = new ArrayList<OptionInput>();
		options.add(projectPickerExport);
		options.add(milestonePickerExport);
		button.setOptions(options);
		return button;

	}

	/**
	 * Milestone picker option.
	 *
	 * @return the milestone picker option
	 */
	@Bean(name = "milestonePickerExport")
	public MilestonePickerOption milestonePickerExport() {
		MilestonePickerOption picker = new MilestonePickerOption();
		picker.setLabelKey("ans.convergence.export.select.milestone");
		picker.setPickerLabelKey("ans.convergence.export.select.milestone");
		picker.setPickerName("milestones"); //
		picker.setDefaultSelected(false);
		return picker;
	}

	/**
	 * Project picker option.
	 *
	 * @return the project picker option
	 */
	@Bean(name = "projectPickerExport")
	public ProjectPickerOption projectPickerExport() {
		ProjectPickerOption picker = new ProjectPickerOption();
		picker.setLabelKey("ans.convergence.export.select.project");
		picker.setPickerLabelKey("ans.convergence.export.select.project");
		picker.setPickerName("projects");
		picker.setDefaultSelected(true);
		return picker;
	}
}
