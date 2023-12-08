/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.model;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class ReqStepBinding.
 */
@Getter
@Setter
public class ReqStepBinding {

	// id du lien avec le CT dans requirement_version_coverage
	Long reqVersionCoverageId;

	// requirement_version_coverage_id.verified_req_version_id
	Long resId;

	// id du CT
	Long tclnId;

	// id du step
	Long stepId;
	
	//Flag pour savoir si le binding a été surchargé
	Boolean fromSocle = Boolean.FALSE;
}
