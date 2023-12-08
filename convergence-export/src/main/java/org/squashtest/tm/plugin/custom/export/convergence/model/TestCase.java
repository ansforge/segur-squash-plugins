/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class TestCase.
 */
@Getter
@Setter
public class TestCase implements Comparable<TestCase> {

	Long tcln_id;

	String reference = "";

	String prerequisite = "";

	String description = "";

	String tcStatus;

	Boolean isCoeurDeMetier = false;

	// Steps du TestCase ordonnés
	List<Long> orderedStepIds;

	// concatenation des CUfs pts de verif des step
	// non renseigné publication
	String pointsDeVerification;

	String parentName;

	/**
	 * Instantiates a new test case.
	 *
	 * @param tcln_id      the tcln id
	 * @param reference    the reference
	 * @param prerequisite the prerequisite
	 * @param description  the description
	 * @param tcStatus     the tc status
	 */
	public TestCase(Long tcln_id, String reference, String prerequisite, String description, String tcStatus,
			String parentName) {
		this.tcln_id = tcln_id;
		this.reference = reference;
		this.prerequisite = prerequisite;
		this.description = description;
		this.tcStatus = tcStatus;
		this.parentName = parentName;
	}

	@Override
	public int compareTo(TestCase o) {
		if (reference == null || o.getReference() == null) {
			return 0;
		}
		return reference.compareTo(o.getReference());
	}
}
