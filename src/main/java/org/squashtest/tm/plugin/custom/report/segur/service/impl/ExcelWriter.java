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
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
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

	private static final String REQ_CONTEXT_PATH = "%s/requirement-workspace/requirement/%d/content";

	private static final String TESTCASE_CONTEXT_PATH = "%s/test-case-workspace/test-case/%d/content";

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

	/** The Constant ERROR_COLUMN_LEVEL. */

	public static final int ERROR_COLUMN_LEVEL = 0;

	/** The Constant ERROR_COLUMN_RESID. */
	public static final int ERROR_COLUMN_RESID = 1;

	/** The Constant ERROR_COLUMN_MSG. */
	public static final int ERROR_COLUMN_MSG = 2;

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
		linkFont.setColor(HSSFColorPredefined.BLUE.getIndex());
		// boucle sur les exigences
		for (ExcelRow req : data.getRequirements()) {

			// extraire les CTs liés à l'exigence de la map du binding
			List<Long> bindingCT = data.getBindings().stream().filter(p -> p.getResId().equals(req.getResId()))
					.map(val -> val.getTclnId()).distinct().collect(Collectors.toList());

			if (bindingCT.isEmpty()) {
				// On ajoute un test vide pour pouvoir formater les cellules
				bindingCT.add(0L);
			}

			// si il existe des CTs
			for (Long tcID : bindingCT) {
				TestCase testCase;
				if (tcID != 0L) {
					testCase = data.getTestCases().get(tcID);
				} else {
					testCase = new TestCase(tcID, "", "", "", "", "");
					List<Long> stepIds = new ArrayList<Long>();
					// stepIds.add(0L);
					testCase.setOrderedStepIds(stepIds);
				}
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
				if (data.getPerimeter().isPrePublication()) {
					Cell c32 = rowWithTC.createCell(PREPUB_COLUMN_BON_POUR_PUBLICATION);
					c32.setCellStyle(style2apply.getCell(PREPUB_COLUMN_BON_POUR_PUBLICATION).getCellStyle());
					if ((req.getReqStatus().equals(Constantes.STATUS_APPROVED))
							&& (testCase.getTcStatus().equals(Constantes.STATUS_APPROVED))) {
						c32.setCellValue(" X ");
					} else {
						c32.setCellValue(" ");
					}

					Cell c33 = rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_EXIGENCE);
					CellStyle c33Style = rowWithTC.getSheet().getWorkbook().createCellStyle();
					c33Style.cloneStyleFrom(style2apply.getCell(PREPUB_COLUMN_REFERENCE_EXIGENCE).getCellStyle());
					c33Style.setFont(linkFont);
					c33.setCellStyle(c33Style);
					c33.setCellValue(req.getReference());
					XSSFHyperlink c33link = (XSSFHyperlink) helper.createHyperlink(HyperlinkType.URL);
					c33link.setAddress(String.format(REQ_CONTEXT_PATH, squashBaseUrl, req.getReqId()));
					c33.setHyperlink(c33link);

					CellStyle c34Style = rowWithTC.getSheet().getWorkbook().createCellStyle();
					c34Style.cloneStyleFrom(style2apply.getCell(PREPUB_COLUMN_REFERENCE_CAS_DE_TEST).getCellStyle());
					Cell c34 = rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_CAS_DE_TEST);
					c34Style.setFont(linkFont);
					c34.setCellStyle(c34Style);
					c34.setCellValue(testCase.getReference());
					XSSFHyperlink c34link = (XSSFHyperlink) helper.createHyperlink(HyperlinkType.URL);
					c34link.setAddress(String.format(TESTCASE_CONTEXT_PATH, squashBaseUrl, testCase.getTcln_id()));
					c34.setHyperlink(c34link);

					Cell c35 = rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE);
					CellStyle c35Style = rowWithTC.getSheet().getWorkbook().createCellStyle();
					c35Style.cloneStyleFrom(style2apply.getCell(PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE).getCellStyle());
					c35Style.setFont(linkFont);
					c35.setCellStyle(c35Style);
					c35.setCellValue(req.getReferenceSocle());
					XSSFHyperlink c35link = (XSSFHyperlink) helper.createHyperlink(HyperlinkType.URL);
					c35link.setAddress(String.format(REQ_CONTEXT_PATH, squashBaseUrl, req.getSocleResId()));
					c35.setHyperlink(c35link);

					Cell c36 = rowWithTC.createCell(PREPUB_COLUMN_POINTS_DE_VERIF);
					CellStyle c36Style = rowWithTC.getSheet().getWorkbook().createCellStyle();
					c36Style.cloneStyleFrom(style2apply.getCell(PREPUB_COLUMN_POINTS_DE_VERIF).getCellStyle());
					c36Style.setFont(linkFont);
					c36.setCellStyle(c36Style);
					c36.setCellValue(testCase.getPointsDeVerification());

				}
				lineNumber++;
			}

		} // exigences
			// Suppression de la ligne 1 (template de style)
		sheet.shiftRows(REM_LINE_STYLE_TEMPLATE_INDEX + 1, lineNumber - 1, -1);
		// TODO : add borders to cells
		for (Row row : sheet) {
			for (Cell cell : row) {
				CellStyle style = cell.getCellStyle();
				style.setBorderBottom(BorderStyle.THIN);
				style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderLeft(BorderStyle.THIN);
				style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderRight(BorderStyle.THIN);
				style.setRightBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderTop(BorderStyle.THIN);
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

	private Row writeRequirementRow(ExcelRow data, XSSFSheet sheet, int lineIndex, Row style2apply) {
		// ecriture des données

		Row row = sheet.createRow(lineIndex);

		Cell c0 = row.createCell(REM_COLUMN_CONDITIONNELLE);
		CellStyle c0Style = sheet.getWorkbook().createCellStyle();
		c0Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_CONDITIONNELLE).getCellStyle());
		c0.setCellStyle(c0Style);
		c0.setCellValue(data.getBoolExigenceConditionnelle_1());

		Cell c1 = row.createCell(REM_COLUMN_PROFIL);
		CellStyle c1Style = sheet.getWorkbook().createCellStyle();
		c1Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_PROFIL).getCellStyle());
		c1.setCellStyle(c1Style);
		c1.setCellValue(data.getProfil_2());

		Cell c2 = row.createCell(REM_COLUMN_ID_SECTION);
		CellStyle c2Style = sheet.getWorkbook().createCellStyle();
		c2Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_ID_SECTION).getCellStyle());
		c2.setCellStyle(c2Style);
		c2.setCellValue(data.getId_section_3());

		Cell c4 = row.createCell(REM_COLUMN_SECTION);
		CellStyle c4Style = sheet.getWorkbook().createCellStyle();
		c4Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_SECTION).getCellStyle());
		c4.setCellStyle(c4Style);
		c4.setCellValue(data.getSection_4());

		Cell c5 = row.createCell(REM_COLUMN_BLOC);
		CellStyle c5Style = sheet.getWorkbook().createCellStyle();
		c5Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_BLOC).getCellStyle());
		c5.setCellStyle(c5Style);
		c5.setCellValue(data.getBloc_5());

		Cell c6 = row.createCell(REM_COLUMN_FONCTION);
		CellStyle c6Style = sheet.getWorkbook().createCellStyle();
		c6Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_FONCTION).getCellStyle());
		c6.setCellStyle(c6Style);
		c6.setCellValue(data.getFonction_6());

		Cell c7 = row.createCell(REM_COLUMN_NATURE);
		CellStyle c7Style = sheet.getWorkbook().createCellStyle();
		c7Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_NATURE).getCellStyle());
		c7.setCellStyle(c7Style);
		c7.setCellValue(data.getNatureExigence_7());

		Cell c8 = row.createCell(REM_COLUMN_NUMERO_EXIGENCE);
		CellStyle c8Style = sheet.getWorkbook().createCellStyle();
		c8Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_NUMERO_EXIGENCE).getCellStyle());
		c8.setCellStyle(c8Style);
		c8.setCellValue(extractNumberFromReference(data.getNumeroExigence_8()));

		Cell c9 = row.createCell(REM_COLUMN_ENONCE);
		CellStyle c9Style = sheet.getWorkbook().createCellStyle();
		c9Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_ENONCE).getCellStyle());
		c9.setCellStyle(c9Style);
		c9Style.setWrapText(true);
		c9.setCellValue(Parser.convertHTMLtoString(data.getEnonceExigence_9()));
		return row;

	}

	private void writeCaseTestPart(TestCase testcase, Map<Long, Step> steps, Row row, Row style2apply) {
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
		if ("".equals(testcase.getPrerequisite()) || testcase.getPrerequisite() == null) {
			c11.setCellValue("Description : " + Constantes.LINE_SEPARATOR
					+ Parser.convertHTMLtoString(testcase.getDescription()));
		} else {
			c11.setCellValue("Prérequis :" + Constantes.LINE_SEPARATOR
					+ Parser.convertHTMLtoString(testcase.getPrerequisite()) + "\n\nDescription :"
					+ Constantes.LINE_SEPARATOR + Parser.convertHTMLtoString(testcase.getDescription()));
		}
		// les steps sont reordonnées dans la liste à partir de leur référence
		int currentExcelColumn = REM_COLUMN_FIRST_NUMERO_PREUVE;
		List<Step> testSteps = new ArrayList<>();
		if (testcase.getOrderedStepIds() != null) {
			for (Long id : testcase.getOrderedStepIds()) {
				testSteps.add(steps.get(id));
			}
		}
		Collections.sort(testSteps);
		if (testSteps.size() < MAX_STEPS) {
			for (int i = testSteps.size(); i < MAX_STEPS; i++) {
				testSteps.add(new Step(Long.valueOf(i), "", i));
			}
		}
		for (Step step : testSteps) {
			if (currentExcelColumn > REM_COLUMN_FIRST_NUMERO_PREUVE + MAX_STEPS * 2) {
				traceur.addMessage(Level.WARNING, testcase.getTcln_id(),
						String.format("Le test contient plus de %s preuves", MAX_STEPS));
				break;
			}
			Cell c12plus = row.createCell(currentExcelColumn);
			CellStyle c12Style = row.getSheet().getWorkbook().createCellStyle();
			c12Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_FIRST_NUMERO_PREUVE).getCellStyle());
			c12plus.setCellStyle(c12Style);
			c12plus.setCellValue(extractNumberFromReference(step.getReference()));
			currentExcelColumn++;

			Cell resultCell = row.createCell(currentExcelColumn);
			CellStyle c13Style = row.getSheet().getWorkbook().createCellStyle();
			c13Style.cloneStyleFrom(style2apply.getCell(REM_COLUMN_FIRST_NUMERO_PREUVE + 1).getCellStyle());
			c13Style.setWrapText(true);
			resultCell.setCellStyle(c13Style);
			resultCell.setCellValue(Parser.convertHTMLtoString(step.getExpectedResult()));
			currentExcelColumn++;
		}
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
		for (int i = REM_COLUMN_SCENARIO_CONFORMITE + 1 ; i <= REM_COLUMN_SCENARIO_CONFORMITE + MAX_STEPS * 2 ; i++) {
			Cell blank = row.createCell(i);
			blank.setCellStyle(row.getSheet().getWorkbook().createCellStyle());
		}
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
		sheet.lockInsertHyperlinks(false);
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
	public void removeRow(XSSFSheet sheet, int rowIndex) {
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
