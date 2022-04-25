package org.squashtest.tm.plugin.custom.report.segur.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicReqModel {

	private Long projectId;
	
	private Long resId;

	private String categorie;

	private String description;

	private String reference;

	private String requirementStatus;
}
