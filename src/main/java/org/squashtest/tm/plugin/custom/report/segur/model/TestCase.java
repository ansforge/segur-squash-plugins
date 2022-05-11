package org.squashtest.tm.plugin.custom.report.segur.model;

import java.util.List;

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
	
	String tcStatus;
	
	Boolean isCoeurDeMetier = false;
	
	//Steps du TestCase ordonnés
	List<Long> orderedStepIds;
	
	//concatenation des CUfs pts de verif des step
	//non renseigné publication
	String pointsDeVerification;
	
	public TestCase(Long tcln_id, String reference, String prerequisite, String description, String tcStatus) {
		this.tcln_id = tcln_id;
		this.reference = reference;
		this.prerequisite = prerequisite;
		this.description = description;		
		this.tcStatus = tcStatus;
	}
}
