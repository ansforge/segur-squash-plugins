/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.model;

import java.util.List;

import org.squashtest.tm.plugin.custom.export.convergence.Constantes;

import lombok.Getter;
import lombok.Setter;


/**
 * The Class PerimeterData.
 */
@Getter
@Setter
public class PerimeterData {
	/* ********************************************** */
	// prefix 'tech_' => data will not appear in excel
	/* ********************************************** */

	// project
	private String projectId;
	private String projectName;
	private String squashBaseUrl;

	private String milestoneId;
	private String milestoneName;
	private String milestoneStatus;

	private Long tclnIdFolderMetier;

	// liste des CTs coeur de metier
	List<Long> IdsCasDeTestCoeurDeMetier;

	public Long getProjectId() {
		return Long.valueOf(projectId);
	}

	public Long getMilestoneId() {
		return Long.valueOf(milestoneId);
	}

	public boolean isPrePublication() {
		return !milestoneStatus.equalsIgnoreCase(Constantes.MILESTONE_LOCKED);
	}

	/**
	 * Extracted data.
	 *
	 * @param milestoneName the milestone name
	 * @param milestoneStatus the milestone status
	 */
	public void extractedData(String milestoneName, String milestoneStatus) {
		this.milestoneName = milestoneName;
		this.milestoneStatus = milestoneStatus;
	}

}
