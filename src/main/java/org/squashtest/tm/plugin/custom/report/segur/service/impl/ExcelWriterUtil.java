package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriterUtil {
	public static String REM = "REM";
	public static String PREPUB = "prepub";
	public static String UNDERSCORE = "_";
	public static String EXTENSION = ".xls";
	
//	 public static File createOutputFile(String fileName) {
//			// TODO Path du fichier en dir ici ....
//			return new File("C:\\Users\\cjuillard\\Desktop\\" + fileName);
//		}

		public static String createOutputFileName(boolean prepub, String trigrammeProjet, String versionOuJalon) {

			// publication: REM_[trigramme projet]_version.xls => REM_HOP-RI_V1.3.xls
			// prépublication: prepub_[datedujourJJMMAAAA]_REM_[trigramme
			// projet]_[version].xls
			// avec versiopn= nom du Jalon courant

			StringBuilder sFileName = new StringBuilder();
			if (prepub) {
				DateTimeFormatter pattern = DateTimeFormatter.ofPattern("ddMMyyyy");
				LocalDateTime nowDate = LocalDateTime.now();
				sFileName.append(PREPUB).append(UNDERSCORE).append(nowDate.format(pattern)).append(UNDERSCORE);
			}

			sFileName.append(REM).append(UNDERSCORE).append(trigrammeProjet).append(UNDERSCORE).append(versionOuJalon)
					.append(EXTENSION);
			return sFileName.toString();
		}
		
		//A supprimer ...
//		public static File flushToTemporaryFile(XSSFWorkbook workbook) throws IOException {
//	        File temp = File.createTempFile("campaign_report", ".xls");
//	        temp.deleteOnExit();
//
//	        FileOutputStream fos = new FileOutputStream(temp);
//	        workbook.write(fos);
//	        fos.close();
//	        return temp;
//	    }
		
		//TODO
		public static File flushToTemporaryFile(XSSFWorkbook workbook, String FileName) throws IOException {
	     
			String tempDir = System.getProperty("java.io.tmpdir");
			File temFile = new File(tempDir, FileName);
			temFile.deleteOnExit();

			FileOutputStream out = null;
			out = new FileOutputStream(temFile);
			workbook.write(out);
			workbook.close();
			out.close();
	        return temFile;
	    }

		
}
