/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.model;

import lombok.Getter;
import lombok.Setter;


/**
 * The Class LinkedReq.
 */
@Getter
@Setter
public class LinkedReq {

	/**
	 * Instantiates a new linked req.
	 *
	 * @param resId the res id
	 * @param socleResId the socle res id
	 */
	public LinkedReq(Long resId, Long socleResId) {
		super();
		this.resId = resId;
		this.socleResId = socleResId;
	}

	// exigencde de l'arbre
	Long resId;
	// exigence (socle) liée
	Long socleResId;
	// référence de l'exigence socle liée
//	String relatedReference;
}
