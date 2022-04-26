package org.squashtest.tm.plugin.custom.report.segur.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqStepCaseBinding {

	//a supprimer ?
	Long reqVersionCoverageId;
	
	//id de l'exigence (tables resource, requirement_version requirement_version_coverage_id.verified_req_version_id
	Long resId;
	
	//id du CT 
	Long tclnId;
	
	//id du step
	Long stepId;
}
