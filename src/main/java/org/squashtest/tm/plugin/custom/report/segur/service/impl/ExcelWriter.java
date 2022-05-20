/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.Level;
import org.squashtest.tm.plugin.custom.report.segur.Message;
import org.squashtest.tm.plugin.custom.report.segur.Parser;
import org.squashtest.tm.plugin.custom.report.segur.Traceur;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelRow;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;


/**
 * The Class ExcelWriter.
 */
@Component
public class ExcelWriter {

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
	
	/** The Constant REM_COLUMN_PROFIL. */
	public static final int REM_COLUMN_PROFIL = 1;
	
	/** The Constant REM_COLUMN_ID_SECTION. */
	public static final int REM_COLUMN_ID_SECTION = 2;
	
	/** The Constant REM_COLUMN_SECTION. */
	public static final int REM_COLUMN_SECTION = 3;
	
	/** The Constant REM_COLUMN_BLOC. */
	public static final int REM_COLUMN_BLOC = 4;
	
	/** The Constant REM_COLUMN_FONCTION. */
	public static final int REM_COLUMN_FONCTION = 5;
	
	/** The Constant REM_COLUMN_NATURE. */
	public static final int REM_COLUMN_NATURE = 6;
	
	/** The Constant REM_COLUMN_NUMERO_EXIGENCE. */
	public static final int REM_COLUMN_NUMERO_EXIGENCE = 7;
	
	/** The Constant REM_COLUMN_ENONCE. */
	public static final int REM_COLUMN_ENONCE = 8;
	
	/** The Constant REM_COLUMN_NUMERO_SCENARIO. */
	public static final int REM_COLUMN_NUMERO_SCENARIO = 9;
	
	/** The Constant REM_COLUMN_SCENARIO_CONFORMITE. */
	public static final int REM_COLUMN_SCENARIO_CONFORMITE = 10;

	/** The Constant MAX_STEP_NUMBER. */
	public static final int MAX_STEP_NUMBER = 10;
	
	/** The Constant REM_COLUMN_FIRST_NUMERO_PREUVE. */
	public static final int REM_COLUMN_FIRST_NUMERO_PREUVE = REM_COLUMN_SCENARIO_CONFORMITE + 1;

	/** The Constant PREPUB_COLUMN_BON_POUR_PUBLICATION. */
	public static final int PREPUB_COLUMN_BON_POUR_PUBLICATION = REM_COLUMN_SCENARIO_CONFORMITE + MAX_STEP_NUMBER * 2
			+ 1;
	
	/** The Constant PREPUB_COLUMN_REFERENCE_EXIGENCE. */
	public static final int PREPUB_COLUMN_REFERENCE_EXIGENCE = PREPUB_COLUMN_BON_POUR_PUBLICATION + 1;
	
	/** The Constant PREPUB_COLUMN_REFERENCE_CAS_DE_TEST. */
	public static final int PREPUB_COLUMN_REFERENCE_CAS_DE_TEST = PREPUB_COLUMN_REFERENCE_EXIGENCE + 1;
	
	/** The Constant PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE. */
	public static final int PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE = PREPUB_COLUMN_REFERENCE_CAS_DE_TEST + 1;
	
	/** The Constant PREPUB_COLUMN_POINTS_DE_VERIF. */
	public static final int PREPUB_COLUMN_POINTS_DE_VERIF = PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE + 1;

//	private List<Message> msg = new ArrayList<Message>();
//	private static int COUNTER_MSG = 0;
/** The Constant ERROR_COLUMN_LEVEL. */
//	private static final int MAX_MSG = 30;
	public static final int ERROR_COLUMN_LEVEL = 0;
	
	/** The Constant ERROR_COLUMN_RESID. */
	public static final int ERROR_COLUMN_RESID = 1;
	
	/** The Constant ERROR_COLUMN_MSG. */
	public static final int ERROR_COLUMN_MSG = 2;

	private Traceur traceur;

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
	 * @param workbook the workbook
	 * @param data the data
	 */
	public void putDatasInWorkbook(boolean boolPrebub, XSSFWorkbook workbook, DSRData data) {

		// Get first sheet
		XSSFSheet sheet = workbook.getSheet("Exigences");
		// Récupération de la ligne 2 pour utilisation des styles
		Row style2apply = sheet.getRow(REM_LINE_STYLE_TEMPLATE_INDEX);
		// ecriture des données
		int lineNumber = REM_FIRST_EMPTY_LINE;

		// boucle sur les exigences
		for (ExcelRow req : data.getRequirements()) {

			// extraire les CTs liés à l'exigence de la map du binding
			List<Long> bindingCT = data.getBindings().stream().filter(p -> p.getResId().equals(req.getResId()))
					.map(val -> val.getTclnId()).distinct().collect(Collectors.toList());

			if (bindingCT.isEmpty()) {
				Row currentRow = writeRequirementRow(req, sheet, lineNumber, style2apply);
				// si prépublication => MAJ des données sur l'exigence
				if (boolPrebub) {
					currentRow.createCell(PREPUB_COLUMN_REFERENCE_EXIGENCE).setCellValue(req.getReference());
					currentRow.createCell(PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE).setCellValue(req.getReferenceSocle());
				}
				lineNumber++;
			}

			// si il existe des CTs
			for (Long tcID : bindingCT) {
				TestCase testCase = data.getTestCases().get(tcID);
				// on ecrit (ou réecrit) les colonnes sur les exigences
				Row rowWithTC = writeRequirementRow(req, sheet, lineNumber, style2apply);

				// liste des steps pour l'exigence ET le cas de test courant
				if (testCase.getIsCoeurDeMetier()) {

					// TODO onglet coeur de métier => lecture des steps dans le binding
//				bindingSteps = liste.stream().filter(p -> p.getResId().equals(req.getResId()))
//						.map(val -> val.getStepId()).distinct().collect(Collectors.toList());
					writeCaseTestPartCoeurDeMetier(testCase, null, data.getSteps(), rowWithTC, style2apply);
				} else { // non coeur de métier => on prends tous les steps du CT
					writeCaseTestPart(testCase, data.getSteps(), rowWithTC, style2apply);
				}

				// colonnes prépublication nécessitant le CT
				if (boolPrebub) {
					Cell c32 = rowWithTC.createCell(PREPUB_COLUMN_BON_POUR_PUBLICATION);
					c32.setCellStyle(style2apply.getCell(PREPUB_COLUMN_BON_POUR_PUBLICATION).getCellStyle());
					if ((req.getReqStatus().equals(Constantes.STATUS_APPROVED))
							&& (testCase.getTcStatus().equals(Constantes.STATUS_APPROVED))) {
						c32.setCellValue(" X ");
					} else {
						c32.setCellValue(" ");
					}
					Cell c33 = rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_EXIGENCE);
					c33.setCellStyle(style2apply.getCell(PREPUB_COLUMN_REFERENCE_EXIGENCE).getCellStyle());
					c33.setCellValue(req.getReference());
					rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_CAS_DE_TEST).setCellValue(testCase.getReference());
					rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE).setCellValue(req.getReferenceSocle());
					rowWithTC.createCell(PREPUB_COLUMN_POINTS_DE_VERIF)
							.setCellValue(testCase.getPointsDeVerification());
				}
				lineNumber++;
			}

		} // exigences
			// Suppression de la ligne 1 (template de style)
		removeRow(sheet, REM_LINE_STYLE_TEMPLATE_INDEX);

		writeErrorSheet(workbook);

		LOGGER.info("  fin remplissage du woorkbook: " + workbook);

		if (!boolPrebub) {
			lockWorkbook(workbook);
		}
	}

	/**
	 * Flush to temporary file.
	 *
	 * @param workbook the workbook
	 * @param FileName the file name
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File flushToTemporaryFile(XSSFWorkbook workbook, String FileName) throws IOException {

		File tempFile = File.createTempFile(FileName, "xlsx");
		tempFile.deleteOnExit();
		FileOutputStream out = new FileOutputStream(tempFile);
		workbook.write(out);
		workbook.close();
		out.close();
		return tempFile;
	}

	private Row writeRequirementRow(ExcelRow data, XSSFSheet sheet, int lineIndex, Row style2apply) {
		// ecriture des données

		Row row = sheet.createRow(lineIndex);

		Cell c0 = row.createCell(REM_COLUMN_CONDITIONNELLE);
		c0.setCellStyle(style2apply.getCell(REM_COLUMN_CONDITIONNELLE).getCellStyle());
		c0.setCellValue(data.getBoolExigenceConditionnelle_1());

		Cell c1 = row.createCell(REM_COLUMN_PROFIL);
		c1.setCellStyle(style2apply.getCell(REM_COLUMN_PROFIL).getCellStyle());
		c1.setCellValue(data.getProfil_2());

		Cell c2 = row.createCell(REM_COLUMN_ID_SECTION);
		c2.setCellStyle(style2apply.getCell(REM_COLUMN_ID_SECTION).getCellStyle());
		c2.setCellValue(data.getId_section_3());

		Cell c4 = row.createCell(REM_COLUMN_SECTION);
		c4.setCellStyle(style2apply.getCell(REM_COLUMN_SECTION).getCellStyle());
		c4.setCellValue(data.getSection_4());

		Cell c5 = row.createCell(REM_COLUMN_BLOC);
		c5.setCellStyle(style2apply.getCell(REM_COLUMN_BLOC).getCellStyle());
		c5.setCellValue(data.getBloc_5());

		Cell c6 = row.createCell(REM_COLUMN_FONCTION);
		c6.setCellStyle(style2apply.getCell(REM_COLUMN_FONCTION).getCellStyle());
		c6.setCellValue(data.getFonction_6());

		Cell c7 = row.createCell(REM_COLUMN_NATURE);
		c7.setCellStyle(style2apply.getCell(REM_COLUMN_NATURE).getCellStyle());
		c7.setCellValue(data.getNatureExigence_7());

		Cell c8 = row.createCell(REM_COLUMN_NUMERO_EXIGENCE);
		c8.setCellStyle(style2apply.getCell(REM_COLUMN_NUMERO_EXIGENCE).getCellStyle());
		c8.setCellValue(extractNumberFromReference(data.getNumeroExigence_8()));

		Cell c9 = row.createCell(REM_COLUMN_ENONCE);
		c9.setCellStyle(style2apply.getCell(REM_COLUMN_ENONCE).getCellStyle());
		CellStyle style = c9.getCellStyle();
		style.setWrapText(true);
		c9.setCellStyle(style);
		c9.setCellValue(data.getEnonceExigence_9());
		return row;

	}

	private void writeCaseTestPart(TestCase testcase, Map<Long, Step> steps, Row row, Row style2apply) {
		// ecriture des données

		Cell c10 = row.createCell(REM_COLUMN_NUMERO_SCENARIO);
		c10.setCellStyle(style2apply.getCell(REM_COLUMN_NUMERO_SCENARIO).getCellStyle());
		c10.setCellValue(extractNumberFromReference(testcase.getReference()));

		// cas des CTs non coeur de métier
		Cell c11 = row.createCell(REM_COLUMN_SCENARIO_CONFORMITE);
		c11.setCellStyle(style2apply.getCell(REM_COLUMN_SCENARIO_CONFORMITE).getCellStyle());
		if ("".equals(testcase.getPrerequisite()) || testcase.getPrerequisite() == null) {
			c11.setCellValue("Description: \n  " + Parser.convertHTMLtoString(testcase.getDescription()));
		} else {
			c11.setCellValue("Prérequis:\n " + Parser.convertHTMLtoString(testcase.getPrerequisite())
					+ "\n\nDescription:\n  " + Parser.convertHTMLtoString(testcase.getDescription()));
		}

		// TODO => erreur si la liste à plus de 10 steps et limiter bindingSteps à 10
		// les steps sont reordonnées dans la liste à partir de leur référence
		int currentExcelColumn = REM_COLUMN_FIRST_NUMERO_PREUVE;
		if (testcase.getOrderedStepIds() != null) {
			List<Step> testSteps = new ArrayList<>();
			for (Long id : testcase.getOrderedStepIds()) {
				testSteps.add(steps.get(id));
			}
			Collections.sort(testSteps);
			for (Step step : testSteps) {

				Cell c12plus = row.createCell(currentExcelColumn);
				c12plus.setCellStyle(style2apply.getCell(REM_COLUMN_FIRST_NUMERO_PREUVE).getCellStyle());
				c12plus.setCellValue(extractNumberFromReference(step.getReference()));
				currentExcelColumn++;

				Cell resultCell = row.createCell(currentExcelColumn);
				CellStyle style = style2apply.getCell(REM_COLUMN_FIRST_NUMERO_PREUVE + 1).getCellStyle();
				style.setWrapText(true);
				resultCell.setCellStyle(style);
				resultCell.setCellValue(step.getExpectedResult());
				currentExcelColumn++;
			}
		}

	}

	private void writeCaseTestPartCoeurDeMetier(TestCase testcase, List<Long> bindedStepIds, Map<Long, Step> steps,
			Row row, Row style2apply) {
		Cell c10 = row.createCell(REM_COLUMN_NUMERO_SCENARIO);
		c10.setCellStyle(style2apply.getCell(REM_COLUMN_NUMERO_SCENARIO).getCellStyle());
		c10.setCellValue(testcase.getReference());

		// cas des CTs coeur de métier
		Cell c11 = row.createCell(REM_COLUMN_SCENARIO_CONFORMITE);
		c11.setCellStyle(style2apply.getCell(REM_COLUMN_SCENARIO_CONFORMITE).getCellStyle());
		c11.setCellValue(" Cas de Test Coeur de métier ... ");
		// TODO cf. SFD
	}

	private void writeErrorSheet(XSSFWorkbook workbook) {
		List<Message> msg = traceur.getMsg();
		if (msg.size() != 0) {
			XSSFSheet errorSheet = workbook.createSheet(ERROR_SHEET_NAME);
			int line = 0;
			Row firstRow = errorSheet.createRow(line);
			firstRow.createCell(ERROR_COLUMN_MSG).setCellValue(
					"ATTENTION, le nombre maximum d'erreurs/warnings affichés est : " + Traceur.getMAX_MSG());

			for (Message msgLine : msg) {
				Row row = errorSheet.createRow(++line);
				row.createCell(ERROR_COLUMN_LEVEL).setCellValue(msgLine.getLevel().name());
				row.createCell(ERROR_COLUMN_RESID).setCellValue(msgLine.getResId());
				row.createCell(ERROR_COLUMN_MSG).setCellValue(msgLine.getMsg());
			}
		}
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
		String password = generateRandomPassword();
		LOGGER.info("Unlock Password : {}", password);
		sheet.protectSheet(password);
		sheet.enableLocking();
	}

	private String generateRandomPassword() {
		return RandomStringUtils.random(255, 33, 122, false, false);
	}

	/**
	 * Remove a row by its index
	 * 
	 * @param sheet    a Excel sheet
	 * @param rowIndex a 0 based index of removing row
	 */
	private void removeRow(XSSFSheet sheet, int rowIndex) {
		int lastRowNum = sheet.getLastRowNum();
		if (rowIndex >= 0 && rowIndex < lastRowNum) {
			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
		}
		if (rowIndex == lastRowNum) {
			XSSFRow removingRow = sheet.getRow(rowIndex);
			if (removingRow != null) {
				sheet.removeRow(removingRow);
			}
		}
	}
}
