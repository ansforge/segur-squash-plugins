/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Step implements Comparable<Step> {

	Long testSTepId;

	String expectedResult;

	// CUF
	String reference;

	int stepOrder;

	public Step(Long testSTepId, String expectedResult, int stepOrder) {
		this.testSTepId = testSTepId;
		this.expectedResult = expectedResult;
		this.stepOrder = stepOrder;
	}

	@Override
	public int compareTo(Step o) {
		if (reference == null || o.getReference() == null) {
			return 0;
		}
		return reference.compareTo(o.getReference());
	}
}
