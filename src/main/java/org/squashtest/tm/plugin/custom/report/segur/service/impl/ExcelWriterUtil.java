package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.Parser;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelData;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepBinding;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCaseStep;

import lombok.Getter;
import lombok.extern.java.Log;

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
	public static String EXTENSION = ".xlsx";

	// onglets
	public static int REM_SHEET_INDEX = 0;
	public static int METIER_SHEET_INDEX = 1;
	public static int ERROR_SHEET_INDEX = 2;

	// onglet 0
	public static int REM_FIRST_EMPTY_LINE = 2; // 0-based index '2' <=> line 3
	public static int REM_COLUMN_CONDITIONNELLE = 0;
	public static int REM_COLUMN_PROFIL = 1;
	public static int REM_COLUMN_ID_SECTION = 2;
	public static int REM_COLUMN_SECTION = 3;
	public static int REM_COLUMN_BLOC = 4;
	public static int REM_COLUMN_FONCTION = 5;
	public static int REM_COLUMN_NATURE = 6;
	public static int REM_COLUMN_NUMERO_EXIGENCE = 7;
	public static int REM_COLUMN_ENONCE = 8;
	public static int REM_COLUMN_NUMERO_SCENARIO = 9;
	public static int REM_COLUMN_SCENARIO_CONFORMITE = 10;

	public static int MAX_STEP_NUMBER = 10;
	public static int REM_COLUMN_FIRST_NUMERO_PREUVE = REM_COLUMN_SCENARIO_CONFORMITE + 1;

	public static int PREPUB_COLUMN_BON_POUR_PUBLICATION = REM_COLUMN_SCENARIO_CONFORMITE + MAX_STEP_NUMBER * 2 + 1;
	public static int PREPUB_COLUMN_REFERENCE_EXIGENCE = PREPUB_COLUMN_BON_POUR_PUBLICATION + 1;
	public static int PREPUB_COLUMN_REFERENCE_CAS_DE_TEST = PREPUB_COLUMN_REFERENCE_EXIGENCE + 1;
	public static int PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE = PREPUB_COLUMN_REFERENCE_CAS_DE_TEST + 1;
	public static int PREPUB_COLUMN_POINTS_DE_VERIF = PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE + 1;

	private XSSFCellStyle style = null;

	// index de la ligne à créer
	private int nextLine = REM_FIRST_EMPTY_LINE;

	//
	private ExcelData data = null;
	private Cell cell = null;
	private Row row = null;
	private List<Long> bindingCT = null;
	private Sheet sheet = null;
	private TestCase testCase = null;
	private List<Long> bindingSteps = null;
	private Step currentStep = null;

	@Getter
	private XSSFWorkbook workbook = null;

	public String createOutputFileName(boolean prepub, String trigrammeProjet, String versionOuJalon) {

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

	public void loadWorkbookTemplate() {

		// File file = new ClassPathResource(TEMPLATE_NAME).getFile();
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

		LOGGER.error(" ******** templatee :" + template);

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

	public void putDatasInWorkbook(String milestoneStatus, List<ReqModel> datas, List<ReqStepBinding> liste,
			Map<Long, TestCase> mapCT, Map<Long, Step> steps, boolean boolPrebub) {

		workbook.getSheetName(0);
		Sheet sheet = workbook.getSheetAt(0);

		// essai autosize de la largeur des lignes
		// global.. => XSSFCellStyle style pour ne pas avoir à le passer en argument ...
		style = workbook.createCellStyle();
		style.setWrapText(true);

		if (boolPrebub) {
	//	removePrepubColumns(sheet);
			addPrepubHeaders(sheet);
		}

		// TODO Mode prepublication => données pour les 5 dernières colonnes

		// ecriture des données
		nextLine = REM_FIRST_EMPTY_LINE;

		// TODO voir passage de la row courrante entre les methodes writeExigencePart,
		// writeCaseTestPart

		// boucle sur les exigences
		for (ReqModel req : datas) {

			// extraire les CTs liés de la map du binding
			bindingCT = liste.stream().filter(p -> p.getResId().equals(req.getResId())).map(val -> val.getTclnId())
					.distinct().collect(Collectors.toList());

			if (bindingCT.isEmpty()) {
				writeExigencePart(req.getExcelData(), sheet, nextLine);
				nextLine += 1;
			}

			// si il existe des CTs
			for (Long tcID : bindingCT) {
				testCase = mapCT.get(tcID);
				// on ecrit (ou réecrit) les colonnes sur les exigences
				writeExigencePart(req.getExcelData(), sheet, nextLine);
				
				// liste des steps pour l'exigence ET le cas de test courant
				if (testCase.getIsCoeurDeMetier()) 
				{
				
//				bindingSteps = liste.stream().filter(p -> p.getResId().equals(req.getResId()))
//						.map(val -> val.getStepId()).distinct().collect(Collectors.toList());
				writeCaseTestPartCoeurDeMetier(testCase, bindingSteps, steps);
				}
				else { // non coeur de métier => on prends tous les steps du CT
					writeCaseTestPart(testCase, steps);
				}

				
				
				nextLine += 1;
			}

		} // exigences
	
		LOGGER.error(" **************  fin remplissage du woorkbook: " + workbook);
	}
	
	public void addPrepubHeaders(Sheet sheet) {
		
		Row refrow = sheet.getRow(0);
		Cell refcell = refrow.getCell(REM_COLUMN_SCENARIO_CONFORMITE);
		CellStyle style = refcell.getCellStyle();
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.BROWN.getIndex());

		addHeadColumnPrepub(refrow, PREPUB_COLUMN_BON_POUR_PUBLICATION, "bon pour publication", style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_EXIGENCE, "référence exigence", style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_CAS_DE_TEST, "référence cas de test", style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE, "référence exigence socle", style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_POINTS_DE_VERIF, "points de vérification", style);
			
		refrow = sheet.getRow(1);
		refcell = refrow.getCell(REM_COLUMN_SCENARIO_CONFORMITE);
		style = refcell.getCellStyle();
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_BON_POUR_PUBLICATION,String.valueOf(PREPUB_COLUMN_BON_POUR_PUBLICATION +1), style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_EXIGENCE,String.valueOf(PREPUB_COLUMN_REFERENCE_EXIGENCE +1 ) , style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_CAS_DE_TEST, String.valueOf(PREPUB_COLUMN_REFERENCE_CAS_DE_TEST +1 ), style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE, String.valueOf(PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE +1), style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_POINTS_DE_VERIF, String.valueOf(PREPUB_COLUMN_POINTS_DE_VERIF), style);			
	}

	//rename generic => addCell ? ...
	public void addHeadColumnPrepub(Row targetRow, int columnIndex, String label, CellStyle style) {
		Cell newcell = targetRow.createCell(columnIndex);
		newcell.setCellStyle(style);
		newcell.setCellValue(label);
	}

	
//	public void removePrepubColumns(Sheet sheet) {
//		LOGGER.error("PREPUB_COLUMN_BON_POUR_PUBLICATION " + PREPUB_COLUMN_BON_POUR_PUBLICATION);
//		deleteColumns(sheet, PREPUB_COLUMN_BON_POUR_PUBLICATION);
//		LOGGER.error("PREPUB_COLUMN_POINTS_DE_VERIF " + PREPUB_COLUMN_POINTS_DE_VERIF);
//		deleteColumns(sheet, PREPUB_COLUMN_POINTS_DE_VERIF);
//		LOGGER.error("PREPUB_COLUMN_REFERENCE_CAS_DE_TEST " + PREPUB_COLUMN_REFERENCE_CAS_DE_TEST);
//		deleteColumns(sheet, PREPUB_COLUMN_REFERENCE_CAS_DE_TEST);
//		LOGGER.error("PREPUB_COLUMN_REFERENCE_EXIGENCE " + PREPUB_COLUMN_REFERENCE_EXIGENCE);
//		deleteColumns(sheet, PREPUB_COLUMN_REFERENCE_EXIGENCE);
//		LOGGER.error("PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE " + PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE);
//		deleteColumns(sheet, PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE);
//		
//	}

	
//	public void deleteColumns(Sheet sheet, int columnIndex) {
//	    for (Row row : sheet) {
//	    	LOGGER.error("row NUM" + row.getRowNum());
//	        int j = 0;
//
//	        for (Cell cell : row) {
//	            if (j >= columnIndex) {
//	                row.removeCell(cell);
//	            }
//	            j++;
//	        }
//	    }
//	}
	
	public void writeExigencePart(ExcelData data, Sheet sheet, int nextline) {
		// ecriture des données

		row = sheet.createRow(nextLine);
		// row.setRowStyle(style); ça marche pas ...

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
		cell.setCellValue(data.getNumeroExigence_8());

		cell = row.createCell(REM_COLUMN_ENONCE);
		cell.setCellValue(data.getEnonceExigence_9());

	}

	public void writeCaseTestPart(TestCase testcase,  Map<Long, Step> steps) {
		// ecriture des données

		cell = row.createCell(REM_COLUMN_NUMERO_SCENARIO);
		cell.setCellValue(testcase.getReference());

		//cas des CTs non coeur de métier
		cell = row.createCell(REM_COLUMN_SCENARIO_CONFORMITE);
		cell.setCellValue("Prérequis:\n " + Parser.convertHTMLtoString(testcase.getPrerequisite()) + "\n Description: \n  "
				+ Parser.convertHTMLtoString(testcase.getDescription()));

		// TODO => erreur si la liste à plus de 10 steps et limiter bindingSteps à 10
		// TODO trier les steps à partir du StepOrder ....
		int currentExcelColumn = REM_COLUMN_FIRST_NUMERO_PREUVE;
		for (Long stepId : testcase.getOrderedStepIds()) {
//			LOGGER.error(" ******** BUG STEP stepId:." + stepId);
//			LOGGER.error(" ******** BUG STEP map steps" + steps);
			currentStep = steps.get(stepId);
//			LOGGER.error(" ******** BUG cuurentStep expectedResult" + currentStep.getExpectedResult());

			cell = row.createCell(currentExcelColumn);
			cell.setCellValue(currentStep.getReference());
			currentExcelColumn++;

			cell = row.createCell(currentExcelColumn);
			cell.setCellValue(Parser.convertHTMLtoString(currentStep.getExpectedResult()));
			currentExcelColumn++;
		}

	}
	
	
	public void writeCaseTestPartCoeurDeMetier (TestCase testcase, List<Long> bindedStepIds, Map<Long, Step> steps) {
		cell = row.createCell(REM_COLUMN_NUMERO_SCENARIO);
		cell.setCellValue(testcase.getReference());

		//cas des CTs  coeur de métier
		cell = row.createCell(REM_COLUMN_SCENARIO_CONFORMITE);
		cell.setCellValue(" Cas de Test Coeur de métier ... ");
		//TODO cf. SFD
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

	public static String getTrigramProject(String projectName) {

		String trigram = "";

		// CH_ANN_xxxxx SC_ANN_xxx ou prefix_ANN_xxxxxxxxxxx
		String[] frags = projectName.split(UNDERSCORE);
		if ((frags.length < 3) || (frags[1].length() != 3)) {
			LOGGER.warn("project name format not as expected : " + projectName);
			// TODO ecrire une trace pour 3ème onglet si format non respecté ...
			trigram = projectName;
		} else {
			trigram = frags[1];
		}
		return trigram;
	}

}
