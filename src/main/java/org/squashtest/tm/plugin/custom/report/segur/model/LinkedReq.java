package org.squashtest.tm.plugin.custom.report.segur.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkedReq {

	public LinkedReq(Long resId, Long socleResId) {
		super();
		this.resId = resId;
		this.socleResId = socleResId;
	}
	//exigencde de l'arbre
	Long resId;
	//exigence (socle) liée
	Long socleResId;
	//référence de l'exigence socle liée
//	String relatedReference;
}
