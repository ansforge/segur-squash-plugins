/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.model;

import lombok.Getter;
import lombok.Setter;


/**
 * The Class Step.
 */
@Getter
@Setter
public class Step implements Comparable<Step> {

	Long testSTepId;

	String expectedResult;

	// CUF
	String reference;

	int stepOrder;

	/**
	 * Instantiates a new step.
	 *
	 * @param testSTepId the test S tep id
	 * @param expectedResult the expected result
	 * @param stepOrder the step order
	 */
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
