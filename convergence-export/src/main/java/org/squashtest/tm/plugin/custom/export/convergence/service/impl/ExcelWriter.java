/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.squashtest.tm.plugin.custom.export.convergence.Constantes;
import org.squashtest.tm.plugin.custom.export.convergence.Level;
import org.squashtest.tm.plugin.custom.export.convergence.Parser;
import org.squashtest.tm.plugin.custom.export.convergence.Traceur;
import org.squashtest.tm.plugin.custom.export.convergence.model.ExcelRow;
import org.squashtest.tm.plugin.custom.export.convergence.model.ReqStepBinding;
import org.squashtest.tm.plugin.custom.export.convergence.model.Step;
import org.squashtest.tm.plugin.custom.export.convergence.model.TestCase;

/**
 * The Class ExcelWriter.
 */
@Component("convergence.excelWriter")
public class ExcelWriter {

	private static final String EXCLUDED_TC_STATUS = "OBSOLETE";

	private static final String REQ_CONTEXT_PATH = "%srequirement-workspace/requirement/%d/content";

	private static final String TESTCASE_CONTEXT_PATH = "%stest-case-workspace/test-case/%d/content";

	private static final int MAX_STEPS = 10;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriter.class);

	/** The Constant REM_SHEET_INDEX. */
	// onglets
	public static final int REM_SHEET_INDEX = 0;

	/** The Constant METIER_SHEET_INDEX. */
	public static final int METIER_SHEET_INDEX = 1;

	/** The Constant ERROR_SHEET_NAME. */
	// public static final int ERROR_SHEET_INDEX = 2;
	public static final String ERROR_SHEET_NAME = "WARNING-ERROR";

	/** The Constant REM_FIRST_EMPTY_LINE. */
	// onglet 0
	public static final int REM_FIRST_EMPTY_LINE = 2; // 0-based index '2' <=> line 3

	/** The Constant REM_LINE_STYLE_TEMPLATE_INDEX. */
	public static final int REM_LINE_STYLE_TEMPLATE_INDEX = 1;

	/** The Constant REM_COLUMN_CONDITIONNELLE. */
	public static final int REM_COLUMN_CONDITIONNELLE = 0;

	/** The Constant REM_COLUMN_ID_SECTION. */
	// public static final int REM_COLUMN_ID_SECTION = 2;

	/** The Constant REM_COLUMN_NUMERO_EXIGENCE. */
	public static final int REM_COLUMN_NUMERO_EXIGENCE = 0;

	/** The Constant REM_COLUMN_CHAPITRE. */
	public static final int REM_COLUMN_CHAPITRE = 1;

	/** The Constant REM_COLUMN_FONCTION. */
	public static final int REM_COLUMN_FONCTION = 2;

	/** The Constant REM_COLUMN_ENONCE. */
	public static final int REM_COLUMN_ENONCE = 3;

	/** The Constant REM_COLUMN_PROFIL. */
	public static final int REM_COLUMN_PROFIL = 4;

	/** The Constant REM_COLUMN_NUMERO_SCENARIO. */
	public static final int REM_COLUMN_NUMERO_SCENARIO = 5;

	/** The Constant REM_COLUMN_SCENARIO_CONFORMITE. */
	public static final int REM_COLUMN_SCENARIO_CONFORMITE = 6;

	/** The Constant REM_COLUMN_NUMERO_PREUVE. */
	public static final int REM_COLUMN_NUMERO_PREUVE = 7;

	/** The Constant REM_COLUMN_NUMERO_PREUVE. */
	public static final int REM_COLUMN_PREUVE = 8;

	private Traceur traceur;

	private String squashBaseUrl;

	/**
	 * Instantiates a new excel writer.
	 *
	 * @param traceur the traceur
	 */
	public ExcelWriter(Traceur traceur) {
		super();
		this.traceur = traceur;
	}

	/**
	 * Load workbook template.
	 *
	 * @param templateName the template name
	 * @return the XSSF workbook
	 */
	public XSSFWorkbook loadWorkbookTemplate(String templateName) {

		InputStream template = null;
		XSSFWorkbook wk = null;
		try {
			template = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateName);
			wk = new XSSFWorkbook(template);
			template.close();
		} catch (IOException e) {
			LOGGER.error(" erreur sur création du workbook ... ", e);
		}
		return wk;
	}

	/**
	 * Put datas in workbook.
	 *
	 * @param boolPrebub the bool prebub
	 * @param workbook   the workbook
	 * @param data       the data
	 */
	public void putDatasInWorkbook(XSSFWorkbook workbook, DSRData data) {
		squashBaseUrl = data.getPerimeter().getSquashBaseUrl();
		// Get first sheet
		XSSFSheet sheet = workbook.getSheet("Exigences");
		// Récupération de la ligne 2 pour utilisation des styles
		Row style2apply = sheet.getRow(REM_LINE_STYLE_TEMPLATE_INDEX);
		// ecriture des données
		int lineNumber = REM_FIRST_EMPTY_LINE;
		// Style links
		CreationHelper helper = workbook.getCreationHelper();
		short height = 200;
		Font linkFont = workbook.createFont();
		linkFont.setFontHeight(height);
		linkFont.setFontName("ARIAL");
		linkFont.setUnderline(XSSFFont.U_SINGLE);
		linkFont.setColor(HSSFColor.BLUE.index);
		
		//map avec les steps de tous les Cts
		Map<Long, Step> steps= data.getSteps();
		String milestone = data.getPerimeter().getMilestoneName();
		if (milestone.equals("Vague 1")) {
			milestone = "vague 1";
		} else {
			if (milestone.equals("va2_conception")) {
				milestone = "vague 2";
			} else {
				if (milestone.equals("va2_RC_0723")) {
					milestone = "vague 2";
				}
			}
		}
		
		
		// boucle sur les exigences
		for (ExcelRow req : data.getRequirements()) {


			// extraire les CTs liés à l'exigence de la map du binding
			List<ReqStepBinding> bindingCT = data.getBindings().stream()
					.filter(p -> p.getResId().equals(req.getResId())).distinct().collect(Collectors.toList());
			// Traitement des cas de test

			// Liste finale des cas de test à exporter
			List<Long> tcIds;

			// Il faut déterminer quels sont les cas de test à conserver :
			int tcNumberFromREM = bindingCT.stream().filter(b -> b.getFromSocle().equals(Boolean.FALSE))
					.map(item -> item.getTclnId()).collect(Collectors.toList()).size();
			// - Si présence de cas de test dérivés => On conserve uniquement ceux dérivés
			if (tcNumberFromREM > 0) {
				tcIds = bindingCT.stream().filter(b -> b.getFromSocle().equals(Boolean.FALSE))
						.map(item -> item.getTclnId()).collect(Collectors.toList());
				// - Sinon => On conserve tout
			} else {
				tcIds = bindingCT.stream().map(item -> item.getTclnId()).collect(Collectors.toList());
			}


			// construction d'une liste de testcase à partir de la liste des IDs des testcases
			List<TestCase> tcList = new ArrayList<TestCase>();
			TestCase test;
			for (Long tcID : tcIds) {
				test = data.getTestCases().get(tcID);
						if (EXCLUDED_TC_STATUS!=test.getTcStatus()/* && (0L != test.getTcln_id()*)*/ ) {
					    	tcList.add(test);											}				
			}

			Collections.sort(tcList);
			
			for (TestCase testCase : tcList) {
				
				// Construire la liste des steps du scénario	
				List<Step> testSteps = new ArrayList<>();
					if (testCase.getOrderedStepIds() != null) {
						for (Long id : testCase.getOrderedStepIds()) {
							testSteps.add(steps.get(id));
						}
					}
				//ordonner la liste des steps
				Collections.sort(testSteps);
				
				//step dummy si le scénario n'a pas de preuve
				if (testSteps.size() == 0)
				{
					testSteps.add(new Step(0L,"",0));
				}
				
				for (Step  step: testSteps) {
					
					//EVOL CONVERGENCE
					// on ecrit (ou réecrit) les colonnes sur les exigences
					Row currentRow = writeRequirementRow(req, sheet, lineNumber, style2apply,milestone);

					writeCaseTestStepByStep(testCase, step, currentRow, style2apply);

					//fin evol CONVERGENCE
					lineNumber++;
				}//boucle steps
			}//boucle scénarii
		} //boucle exigences
		
			// Suppression de la ligne 1 (template de style)
		sheet.shiftRows(REM_LINE_STYLE_TEMPLATE_INDEX + 1, lineNumber - 1, -1);
		// add borders to cells
		for (Row row : sheet) {
			for (Cell cell : row) {
				CellStyle style = cell.getCellStyle();
				style.setBorderBottom(CellStyle.BORDER_THIN);
				style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderLeft(CellStyle.BORDER_THIN);
				style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderRight(CellStyle.BORDER_THIN);
				style.setRightBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderTop(CellStyle.BORDER_THIN);
				style.setTopBorderColor(IndexedColors.BLACK.getIndex());
				cell.setCellStyle(style);
			}
		}

		writeErrorSheet(workbook);

		LOGGER.info("  fin remplissage du woorkbook: " + workbook);

		if (!data.getPerimeter().isPrePublication()) {
			lockWorkbook(workbook);
		}
	}

	/**
	 * Flush to temporary file.
	 *
	 * @param workbook the workbook
	 * @param filename the file name
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File flushToTemporaryFile(XSSFWorkbook workbook, String filename) throws IOException {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String absolutePath = tmpdir + File.separator + filename;
		File tempFile = new File(absolutePath);
		tempFile.delete();
		FileOutputStream out = new FileOutputStream(tempFile);
		workbook.write(out);
		workbook.close();
		out.close();
		return tempFile;
	}

	private Row writeRequirementRow(ExcelRow data, XSSFSheet sheet, int lineIndex, Row style2apply, String milestone) {
		// ecriture des données

		Row row = sheet.createRow(lineIndex);

		Cell c1 = row.createCell(REM_COLUMN_PROFIL);
		CellStyle c1Style = sheet.getWorkbook().createCellStyle();
		c1Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_PROFIL).getCellStyle());
		c1.setCellStyle(c1Style);
		String profil = data.getProfil_2();
		String profilDef;
		if (profil.startsWith("va1")) {
				profilDef = "Profil " + profil.substring(4) + " - vague 1";
		} else {
			if (profil.startsWith("va2")) {
					profilDef = "Profil " + profil.substring(4) + " - vague 2";
			} else {
				if (profil.startsWith("Profil")) {
					if (profil.contains(" - vague")) {
						profilDef = profil;
					} else {
						profilDef = profil + " - " + milestone;
					}
				} else {
					if (profil.isEmpty()){
						profilDef = "Profil Général - " + milestone;
					} else {
						profilDef = "Profil " + profil + " - " + milestone;
					}
					
				}
			}
		}
		
		c1.setCellValue(profilDef);


		Cell c3 = row.createCell(REM_COLUMN_CHAPITRE);
		CellStyle c3Style = sheet.getWorkbook().createCellStyle();
		c3Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_CHAPITRE).getCellStyle());
		c3.setCellStyle(c3Style);
		c3.setCellValue(data.getSection_4() + " " + data.getBloc_5());



		Cell c6 = row.createCell(REM_COLUMN_FONCTION);
		CellStyle c6Style = sheet.getWorkbook().createCellStyle();
		c6Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_FONCTION).getCellStyle());
		c6.setCellStyle(c6Style);
		c6.setCellValue(data.getFonction_6());


		Cell c8 = row.createCell(REM_COLUMN_NUMERO_EXIGENCE);
		CellStyle c8Style = sheet.getWorkbook().createCellStyle();
		c8Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_NUMERO_EXIGENCE).getCellStyle());
		c8.setCellStyle(c8Style);
		if (data.getReferenceSocle().isEmpty()) {
			c8.setCellValue(extractNumberFromReference(data.getNumeroExigence_8()));
		} else {
			c8.setCellValue(data.getNumeroExigence_8());
		}

		Cell c9 = row.createCell(REM_COLUMN_ENONCE);
		CellStyle c9Style = sheet.getWorkbook().createCellStyle();
		c9Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_ENONCE).getCellStyle());
		c9.setCellStyle(c9Style);
		c9Style.setWrapText(true);
		c9.setCellValue(Parser.convertHTMLtoString(data.getEnonceExigence_9()));

		return row;

	}

	private void writeCaseTestStepByStep(TestCase testcase, Step currentStep, Row row, Row style2apply) {
		// ecriture des données
		CellStyle c10Style = row.getSheet().getWorkbook().createCellStyle();
		c10Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_NUMERO_SCENARIO).getCellStyle());
		Cell c10 = row.createCell(REM_COLUMN_NUMERO_SCENARIO);
		c10.setCellStyle(c10Style);
		c10.setCellValue(extractNumberFromReference(testcase.getReference()));

		// cas des CTs non coeur de métier
		Cell c11 = row.createCell(REM_COLUMN_SCENARIO_CONFORMITE);
		CellStyle c11Style = row.getSheet().getWorkbook().createCellStyle();
		c11Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_SCENARIO_CONFORMITE).getCellStyle());
		c11.setCellStyle(c11Style);
		if (testcase.getTcln_id() > 0) {
		
			//EVOL CONVERGENCE  =>- non utile, on prend le html brut

			c11.setCellValue(testcase.getDescription());
		}

		    
		//Ecriture des données sur le step
			Cell c12plus = row.createCell(REM_COLUMN_NUMERO_PREUVE);
			CellStyle c12Style = row.getSheet().getWorkbook().createCellStyle();
			c12Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_NUMERO_PREUVE).getCellStyle());
			c12plus.setCellStyle(c12Style);
			c12plus.setCellValue(extractNumberFromReference(currentStep.getReference()));

			Cell resultCell = row.createCell(REM_COLUMN_PREUVE);
			CellStyle c13Style = row.getSheet().getWorkbook().createCellStyle();
			c13Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_PREUVE).getCellStyle());
			c13Style.setWrapText(true);
			resultCell.setCellStyle(c13Style);
			resultCell.setCellValue(Parser.convertHTMLtoString(currentStep.getExpectedResult()));
//		}
	}

	private void writeCaseTestPartCoeurDeMetier(TestCase testcase, List<Long> bindedStepIds, Map<Long, Step> steps,
			Row row, Row style2apply) {
		CellStyle c10Style = row.getSheet().getWorkbook().createCellStyle();
		c10Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_NUMERO_SCENARIO).getCellStyle());
		Cell c10 = row.createCell(REM_COLUMN_NUMERO_SCENARIO);
		c10.setCellStyle(c10Style);
		c10.setCellValue(testcase.getReference());

		// cas des CTs coeur de métier
		Cell c11 = row.createCell(REM_COLUMN_SCENARIO_CONFORMITE);
		CellStyle c11Style = row.getSheet().getWorkbook().createCellStyle();
		c11Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_SCENARIO_CONFORMITE).getCellStyle());
		c11.setCellStyle(c11Style);
		c11.setCellValue(String.format("Cf. Scénarios Coeur de métier\n%s\n[%d] preuve(s)", testcase.getDescription(),
				testcase.getOrderedStepIds().size()));
		// Création de cellules vides pour chaque step afin de respecter le formatage
		for (int i = REM_COLUMN_SCENARIO_CONFORMITE + 1; i <= REM_COLUMN_SCENARIO_CONFORMITE + MAX_STEPS * 2; i++) {
			Cell blank = row.createCell(i);
			blank.setCellStyle(row.getSheet().getWorkbook().createCellStyle());
		}
	}

	private void writeErrorSheet(XSSFWorkbook workbook) {
//		List<Message> msg = traceur.getMsg();
//		if (msg.size() != 0) {
//			XSSFSheet errorSheet = workbook.createSheet(ERROR_SHEET_NAME);
//			int line = 0;
//			Row firstRow = errorSheet.createRow(line);
//			firstRow.createCell(ERROR_COLUMN_MSG).setCellValue(
//					"ATTENTION, le nombre maximum d'erreurs/warnings affichés est : " + Traceur.getMAX_MSG());
//
//			for (Message msgLine : msg) {
//				Row row = errorSheet.createRow(++line);
//				LOGGER.info("  C1: " + msgLine.getLevel().name());
//				row.createCell(ERROR_COLUMN_LEVEL).setCellValue(msgLine.getLevel().name());
//				LOGGER.info("  C2 : " + msgLine.getResId());
//				row.createCell(ERROR_COLUMN_RESID).setCellValue(msgLine.getResId());
//				LOGGER.info("  C3: " + msgLine.getMsg());
//				row.createCell(ERROR_COLUMN_MSG).setCellValue(msgLine.getMsg());
//			}
//		}
	}

	private String extractNumberFromReference(String reference) {
		// supprime le prefix SC, CH, XXX
		String numero = "";
		String prefix = "";
		if (reference != null) {
			int separator = reference.indexOf(".");
			if (separator >= 1) {
				prefix = reference.substring(0, separator);
			}

			if ((prefix.equals(Constantes.PREFIX_PROJET_SOCLE)) || (prefix.equals(Constantes.PREFIX_PROJET_CHANTIER))
					|| (prefix.length() == Constantes.PREFIX_PROJET__METIER_SIZE)) {
				numero = reference.substring(separator + 1, reference.length());
			} else {
				traceur.addMessage(Level.ERROR, reference,
						"Calcul du numéro à partir de la référence : erreur sur suppression du prefix de l'item (ni SC., ni CH., ni XXX.)");
				numero = reference;
			}
		}
		return numero;
	}

	private void lockWorkbook(XSSFWorkbook workbook) {
		LOGGER.info("Appel pour lock d'une feuille du workbook");

		XSSFSheet requirementsSheet = workbook.getSheet("Exigences");
		if (requirementsSheet != null) {
			lockSheet(requirementsSheet);
		}

		XSSFSheet testCasesSheet = workbook.getSheet("Scénarios coeur de métier");
		if (testCasesSheet != null) {
			lockSheet(testCasesSheet);
		}

		workbook.lockStructure();

		workbook.lockRevision();

	}

	private void lockSheet(XSSFSheet sheet) {
		sheet.lockDeleteRows(true);
		sheet.lockDeleteColumns(true);
		sheet.lockInsertColumns(true);
		sheet.lockInsertRows(true);
		sheet.lockSort(false);
		sheet.lockFormatCells(false);
		sheet.lockFormatColumns(false);
		sheet.lockFormatRows(false);
		sheet.lockAutoFilter(false);
		sheet.lockInsertHyperlinks(false);
		String password = generateRandomPassword();
		LOGGER.info("Unlock Password : {}", password);
		sheet.protectSheet(password);
		sheet.enableLocking();
	}

	private TestCase createDummyTestCase(Long tcID) {
		TestCase testCase = new TestCase(tcID, "", "", "", "", "");
		List<Long> stepIds = new ArrayList<Long>();
		testCase.setOrderedStepIds(stepIds);
		return testCase;
	}

	private String generateRandomPassword() {
		return RandomStringUtils.random(255, 33, 122, false, false);
	}

}
