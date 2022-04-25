package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelData;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;

import lombok.Getter;

@Component
public class ExcelWriterUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriterUtil.class);

	// nom et chemin du template dans src/main/resources
	public static String CLASS_PATH_TEMPLATE_NAME = "classpath:templates/template-segur-requirement-export.xlsx";
	public static String TEMPLATE_NAME = "template-segur-requirement-export.xlsx";
	                                      

	// nom du fichier Excel
	public static String REM = "REM";
	public static String PREPUB = "prepub";
	public static String UNDERSCORE = "_";
	public static String EXTENSION = ".xls";
	
	//onglets
	public static int REM_SHEET_INDEX = 0;
	public static int METIER_SHEET_INDEX = 1;
	public static int ERROR_SHEET_INDEX = 2;
	
	//onglet 0
	public static int REM_FIRST_EMPTY_LINE = 2; //0-based index '2' <=> line 3
	public static int REM_COLUMN_CONDITIONNELLE = 0;
	public static int REM_COLUMN_PROFIL = 1;
	public static int REM_COLUMN_ID_SECTION = 2;
	public static int REM_COLUMN_SECTION = 3;
	public static int REM_COLUMN_BLOC = 4;
	public static int REM_COLUMN_FONCTION = 5;
	public static int REM_COLUMN_NATURE = 6;
	public static int REM_COLUMN_NUMERO_EXIGENCE = 7;
	public static int REM_COLUMN_ENONCE = 8;
	
	//index de la ligne à créer
	private int nextLine =  REM_FIRST_EMPTY_LINE;
	
	@Getter
	private XSSFWorkbook workbook = null;
	
	

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

	public void loadWorkbookTemplate()  {

	//	File file = new ClassPathResource(TEMPLATE_NAME).getFile();
		LOGGER.error(" ******** tada ....");
		
		Resource resource = new ClassPathResource(TEMPLATE_NAME);

		
		LOGGER.error(" ******** loadWorkbookTemplate resource:" + resource);
		LOGGER.error(" ************ existe ?: " + resource.exists());
		
		InputStream template = null;
		try {
			template = resource.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			LOGGER.error(" ******** ça plante ici ... ");
		} 
		
	//	FileInputStream template = new FileInputStream(file);

		LOGGER.error(" ******** templatee :" + template);

		/*
		 * ClassLoader classLoader = null; try { classLoader =
		 * getClass().getClassLoader(); } catch (Exception e1) {s // TODO Auto-generated
		 * catch block e1.printStackTrace(); }
		 * 
		 * //InputStream template = classLoader.getResourceAsStream(TEMPLATE_NAME);
		 * 
		 * 
		 * File file = null; try { file =
		 * ResourceUtils.getFile(CLASS_PATH_TEMPLATE_NAME); } catch
		 * (FileNotFoundException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); } java.io.InputStream template = null; try { template =
		 * new FileInputStream(file); } catch (FileNotFoundException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 * 
		 * //java.io.InputStream inputStream =
		 * classLoader.getResourceAsStream(TEMPLATE_NAME);
		 * 
		 * //FileInputStream excelFile = null; // try { // excelFile = new
		 * FileInputStream(inputStream); // } catch (FileNotFoundException e) { // //
		 * TODO Auto-generated catch block // e.printStackTrace(); // } //
		 */

		
		
		// création du workbook

		try {
			workbook = new XSSFWorkbook(template);
			LOGGER.error(" ******** XSSFWorkbook :" + workbook);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.error(" ******** ça plante la ... ");
			LOGGER.error(" ******** " + e.getMessage());
			LOGGER.error(" ******** " + e.toString());
		}

		try {
			template.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// fermetures des flux
//			try {
//				excelFile.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

	}

	//TODO pour mise au point => à supprimer
	public void putDatasInWorkbookOLD(List<ReqModel> datas) {
		workbook.getSheetName(0);
		Sheet sheet = workbook.getSheetAt(0);

		LOGGER.error(" ******** XSSFWorkbook sheet:" + sheet);
		
		LOGGER.error(" ******** XSSFWorkbook:  " + workbook.getSheetName(0));
		// sheet.autoSizeColumn();

		
		
		CellReference cellReference = new CellReference("B3"); // 3 => 1ère ligne vide
		LOGGER.error(" ******** : cellRef OK => "+ cellReference);
	//	Row row = sheet.getRow(cellReference.getRow());
		Row row = sheet.createRow(cellReference.getRow());
		LOGGER.error(" ******** : Row OK =>" + row);
		Cell cell = row.createCell(cellReference.getCol());
		LOGGER.error(" ******** : Cell OK");
		cell.setCellValue("projet.A");
	
		
		LOGGER.error(" **************  fin remplissage du woorkbook: " + workbook);
		
	}
	
	public void putDatasInWorkbook(List<ReqModel> datas) {
		workbook.getSheetName(0);
		Sheet sheet = workbook.getSheetAt(0);

		LOGGER.error(" ******** XSSFWorkbook sheet:" + sheet);
		
		LOGGER.error(" ******** XSSFWorkbook:  " + workbook.getSheetName(0));
		// sheet.autoSizeColumn();

		ExcelData data = null;
		Cell cell = null;
		Row row = null;
		//ecriture des données
		nextLine =  REM_FIRST_EMPTY_LINE;
		for (ReqModel req : datas) {

			data = req.getExcelData();
			
			row = sheet.createRow(nextLine);
			
			cell = row.createCell(REM_COLUMN_CONDITIONNELLE);
			cell.setCellValue(data.getBoolExigenceConditionnelle_1());
			
			cell = row.createCell(REM_COLUMN_PROFIL);
			cell.setCellValue(data.getProfil_2());
			
			cell = row.createCell(REM_COLUMN_ID_SECTION);
			cell.setCellValue(data.getId_section_3());
			
			cell = row.createCell(REM_COLUMN_SECTION);
			cell.setCellValue(data.getSection_4());
			
			cell = row.createCell(REM_COLUMN_BLOC);
			cell.setCellValue(data.getBloc_5());
			
			cell = row.createCell(REM_COLUMN_FONCTION);
			cell.setCellValue(data.getFonction_6());
			
			cell = row.createCell(REM_COLUMN_NATURE);
			cell.setCellValue(data.getNatureExigence_7());
			
			cell = row.createCell(REM_COLUMN_NUMERO_EXIGENCE);
			cell.setCellValue( req.getReference());  // req? excalData?
			
			cell = row.createCell(REM_COLUMN_ENONCE);
			cell.setCellValue(data.getEnonceExigence_9());
			
			
			
			
			nextLine +=1 ;
		}
		
	
		
		LOGGER.error(" **************  fin remplissage du woorkbook: " + workbook);
		
	}
	
	public static File flushToTemporaryFile(XSSFWorkbook workbook, String FileName) throws IOException {

		String tempDir = System.getProperty("java.io.tmpdir");
		File temFile = new File(tempDir, FileName);
		temFile.deleteOnExit();

		FileOutputStream out = null;
		out = new FileOutputStream(temFile);
		LOGGER.error(" **************  workbook: " + workbook);
		workbook.write(out);
		workbook.close();
		out.close();
		return temFile;
	}

}
