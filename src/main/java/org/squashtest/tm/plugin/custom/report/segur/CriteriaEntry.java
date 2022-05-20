/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur;

public enum CriteriaEntry {

	MILESTONES("milestones"), PROJECT_IDS("projects"), SEGUR_SELECTION_MODE("segurSelectionMode");

	String value;

	CriteriaEntry(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
