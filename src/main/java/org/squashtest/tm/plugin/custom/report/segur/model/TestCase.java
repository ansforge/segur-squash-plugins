package org.squashtest.tm.plugin.custom.report.segur.model;

import java.util.List;
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
	
	Boolean isCoeurDeMetier = false;
	
	//Steps du TestCase ordonnés
	List<Long> orderedStepIds;
	
	public TestCase(Long tcln_id, String reference, String prerequisite, String description) {
		this.tcln_id = tcln_id;
		this.reference = reference;
		this.prerequisite = prerequisite;
		this.description = description;				
	}
}
