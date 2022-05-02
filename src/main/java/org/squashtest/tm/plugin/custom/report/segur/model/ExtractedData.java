package org.squashtest.tm.plugin.custom.report.segur.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtractedData {
	/* ********************************************** */
	// prefix 'tech_' => data will not appear in excel
	/* ********************************************** */

//	public ExtractedData() {
//		tech_map_requirement 
//	}
	// project
	private String projectId;
	private String projectName;
	
	private String milestoneId;
	private String milestoneName;
	private String milestoneStatus;
	

	 public void extractedData(String milestoneName, String milestoneStatus) {
		 this.milestoneName = milestoneName;
		 this.milestoneStatus = milestoneStatus;
	 }
	 
	// requirements
	private Map<String, ReqModel> tech_map_requirement = new HashMap<String, ReqModel>();

	// Donn�es tri�es, format�es
	//private List<ExcelData> datas;
	
}
