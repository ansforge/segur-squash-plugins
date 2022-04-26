package org.squashtest.tm.plugin.custom.report.segur.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Step  {

	Long testSTepId;
	
	String expectedResult;
	
	//CUF
	String reference;
	
	int stepOrder;
	
	public Step(Long testSTepId, String expectedResult, int stepOrder) {
		this.testSTepId = testSTepId;
		this.expectedResult = expectedResult;
		this.stepOrder = stepOrder;	
	}
}
