package org.squashtest.tm.plugin.custom.report.segur.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerimeterData {
	/* ********************************************** */
	// prefix 'tech_' => data will not appear in excel
	/* ********************************************** */

	// project
	private String projectId;
	private String projectName;

	private String milestoneId;
	private String milestoneName;
	private String milestoneStatus;

	private Long tclnIdFolderMetier;

	public void extractedData(String milestoneName, String milestoneStatus) {
		this.milestoneName = milestoneName;
		this.milestoneStatus = milestoneStatus;
	}

	// liste des CTs coeur de metier
	List<Long> IdsCasDeTestCoeurDeMetier;
}
