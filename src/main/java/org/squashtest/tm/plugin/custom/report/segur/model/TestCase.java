package org.squashtest.tm.plugin.custom.report.segur.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCase {

	//à supprimer ?
	Long tcln_id;
	
	String reference;
	
	String prerequisite;
	
	String description;
	
	//Map<Long, Step> steps;
	Map<Long, Step> steps;
	
	public TestCase(Long tcln_id, String reference, String prerequisite, String description) {
		this.tcln_id = tcln_id;
		this.reference = reference;
		this.prerequisite = prerequisite;
		this.description = description;				
	}
}
