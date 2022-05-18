/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cuf {

	private String code;

	private String label;

	public Cuf(String code, String label) {
		this.code = code;
		this.label = label;
	}
}
