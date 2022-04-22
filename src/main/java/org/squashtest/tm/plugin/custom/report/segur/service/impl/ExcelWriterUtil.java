package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lowagie.text.pdf.codec.Base64.InputStream;

public class ExcelWriterUtil {
	
	//nom et chemin du template dans src/main/resources
	public static String TEMPLATE_NAME = "templates/template-segur-requirement-export.xlsx";
	
	//nom du fichier Excel
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

		/*
		public  XSSFWorkbook loadWorkbookTemplate() {
			
			ClassLoader classLoader;
			try {
				classLoader = getClass().getClassLoader();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        InputStream inputStream = classLoader.getResourceAsStream(TEMPLATE_NAME);
	        
//			FileInputStream excelFile = null;
//			try {
//				excelFile = new FileInputStream(template);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
            //création du workbook
			XSSFWorkbook workbook = null;
		
				try {
					workbook = new XSSFWorkbook(template);
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			//fermetures des flux
//			try {
//				excelFile.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			return workbook;
		}
		*/
		
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
