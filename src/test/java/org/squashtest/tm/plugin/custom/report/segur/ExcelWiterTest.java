package org.squashtest.tm.plugin.custom.report.segur;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.squashtest.tm.plugin.custom.report.segur.service.impl.ExcelWriterUtil;

public class ExcelWiterTest {

	ExcelWriterUtil excel = new ExcelWriterUtil();

	@Test
	public void createOutputFilenameTest() {

		String filename = excel.createOutputFileName(false, "INS", "v1.3");
		assertEquals(filename,"REM_INS_v1.3.xlsx");
		
		filename = excel.createOutputFileName(true, "INS", "v1.3");
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("ddMMyyyy");
		LocalDateTime nowDate = LocalDateTime.now();
		String expected = "prepub_" + nowDate.format(pattern) + "_REM_INS_v1.3.xlsx";
		System.out.println(expected);
		assertEquals(filename,expected);
	}
	
	@Test
	public void todelete() {
		String trigram = excel.getTrigramProject("sc_fdsre_trrt_fdsdf");
		assertEquals(trigram,"sc_fdsre_trrt_fdsdf"); //trigram>3...
		
		trigram = excel.getTrigramProject("sc_123_trrt_fdsdf");
		assertEquals(trigram,"123"); //trigram>3...

		trigram = excel.getTrigramProject("sc_123");
		assertEquals(trigram,"sc_123");

	}
}
