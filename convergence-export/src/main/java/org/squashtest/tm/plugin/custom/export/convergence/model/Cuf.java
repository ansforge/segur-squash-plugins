/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.model;

import lombok.Getter;
import lombok.Setter;


/**
 * The Class Cuf.
 */
@Getter
@Setter
public class Cuf {

	private String code;

	private String label;

	/**
	 * Instantiates a new cuf.
	 *
	 * @param code the code
	 * @param label the label
	 */
	public Cuf(String code, String label) {
		this.code = code;
		this.label = label;
	}
}
