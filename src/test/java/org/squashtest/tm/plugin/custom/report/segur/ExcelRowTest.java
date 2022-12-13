package org.squashtest.tm.plugin.custom.report.segur;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelRow;

class ExcelRowTest {
	
	List<ExcelRow> requirements = new ArrayList<>();
	
	@Test
	void testSortWithOutTestReference(){
		ExcelRow row1 = new ExcelRow();
		row1.setReference("SC.INS.01.02");
		row1.setId_section_3("INS");
		row1.setSortingKey("");
		
		ExcelRow row2 = new ExcelRow();
		row2.setReference("SC.PSC.01.01");
		row2.setId_section_3("PSC");
		row2.setSortingKey("");
		
		ExcelRow row3 = new ExcelRow();
		row3.setReference("CH.AUT.01.01");
		row3.setId_section_3("AUT");
		row3.setSortingKey("");
		
		requirements.add(row3);
		requirements.add(row2);
		requirements.add(row1);
		
		Collections.sort(requirements);
		assertTrue(requirements.get(0).getReference().equals(row1.getReference()));
		assertTrue(requirements.get(1).getReference().equals(row2.getReference()));
	}

	@Test
	void testSortWithTestReference() {
		ExcelRow row1 = new ExcelRow();
		row1.setReference("SC.INS.01.02");
		row1.setId_section_3("INS");
		row1.setSortingKey("SC.INS.01.02");
		
		ExcelRow row2 = new ExcelRow();
		row2.setReference("SC.INS.01.02");
		row2.setId_section_3("INS/CONF");
		row2.setSortingKey("SC.INS.01.03");
		
		ExcelRow row3 = new ExcelRow();
		row3.setReference("CH.AUT.01.01");
		row3.setId_section_3("AUT");
		row3.setSortingKey("");
		
		
		ExcelRow row4 = new ExcelRow();
		row4.setReference("SC.INS.01.02");
		row4.setId_section_3("INS/CONF");
		row4.setSortingKey("SC.INS.01.01");
		
		requirements.add(row3);
		requirements.add(row4);
		requirements.add(row2);
		requirements.add(row1);
		
		Collections.sort(requirements);
		assertTrue(requirements.get(0).getReference().equals(row4.getReference()));
		assertTrue(requirements.get(1).getReference().equals(row1.getReference()));
		assertTrue(requirements.get(2).getReference().equals(row2.getReference()));
	}

}
