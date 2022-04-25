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
	private String tech_project_id;
	private String tech_project_name;

	// requirements
	private Map<String, ReqModel> tech_map_requirement = new HashMap<String, ReqModel>();

	// Donn�es tri�es, format�es
	//private List<ExcelData> datas;
	
}
