package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.Message;
import org.squashtest.tm.plugin.custom.report.segur.Parser;
import org.squashtest.tm.plugin.custom.report.segur.Traceur;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelData;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;

@Component
public class ExcelWriterUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriterUtil.class);
	// nom et chemin du template dans src/main/resources
	public static final String CLASS_PATH_TEMPLATE_NAME = "classpath:templates/template-segur-requirement-export.xlsx";
	public static final String TEMPLATE_NAME = "template-segur-requirement-export.xlsx";

	// nom du fichier Excel
	public static final String REM = "REM";
	public static final String PREPUB = "prepub";
	public static final String UNDERSCORE = "_";
	public static final String EXTENSION = ".xlsx";

	// onglets
	public static final int REM_SHEET_INDEX = 0;
	public static final int METIER_SHEET_INDEX = 1;
	// public static final int ERROR_SHEET_INDEX = 2;
	public static final String ERROR_SHEET_NAME = "WARNING-ERROR";

	// onglet 0
	public static final int REM_FIRST_EMPTY_LINE = 2; // 0-based index '2' <=> line 3
	public static final int REM_COLUMN_CONDITIONNELLE = 0;
	public static final int REM_COLUMN_PROFIL = 1;
	public static final int REM_COLUMN_ID_SECTION = 2;
	public static final int REM_COLUMN_SECTION = 3;
	public static final int REM_COLUMN_BLOC = 4;
	public static final int REM_COLUMN_FONCTION = 5;
	public static final int REM_COLUMN_NATURE = 6;
	public static final int REM_COLUMN_NUMERO_EXIGENCE = 7;
	public static final int REM_COLUMN_ENONCE = 8;
	public static final int REM_COLUMN_NUMERO_SCENARIO = 9;
	public static final int REM_COLUMN_SCENARIO_CONFORMITE = 10;

	public static final int MAX_STEP_NUMBER = 10;
	public static final int REM_COLUMN_FIRST_NUMERO_PREUVE = REM_COLUMN_SCENARIO_CONFORMITE + 1;

	public static final int PREPUB_COLUMN_BON_POUR_PUBLICATION = REM_COLUMN_SCENARIO_CONFORMITE + MAX_STEP_NUMBER * 2
			+ 1;
	public static final int PREPUB_COLUMN_REFERENCE_EXIGENCE = PREPUB_COLUMN_BON_POUR_PUBLICATION + 1;
	public static final int PREPUB_COLUMN_REFERENCE_CAS_DE_TEST = PREPUB_COLUMN_REFERENCE_EXIGENCE + 1;
	public static final int PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE = PREPUB_COLUMN_REFERENCE_CAS_DE_TEST + 1;
	public static final int PREPUB_COLUMN_POINTS_DE_VERIF = PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE + 1;

//	private List<Message> msg = new ArrayList<Message>();
//	private static int COUNTER_MSG = 0;
//	private static final int MAX_MSG = 30;
	public static final int ERROR_COLUMN_LEVEL = 0;
	public static final int ERROR_COLUMN_RESID = 1;
	public static final int ERROR_COLUMN_MSG = 2;


	private Traceur traceur;

	public ExcelWriterUtil(Traceur traceur) {
		super();
		this.traceur = traceur;
	}

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

	public XSSFWorkbook loadWorkbookTemplate() {

		Resource resource = new ClassPathResource(TEMPLATE_NAME);
		InputStream template = null;
		XSSFWorkbook wk = null;
		try {
			template = resource.getInputStream();
			wk = new XSSFWorkbook(template);
			template.close();
		} catch (IOException e) {
			LOGGER.error(" erreur sur création du workbook ... ", e);
		}
		return wk;
	}

	public void putDatasInWorkbook(boolean boolPrebub, XSSFWorkbook workbook, DSRData data) {

		// Get first sheet
		XSSFSheet sheet = workbook.getSheetAt(0);

		if (boolPrebub) {
			addPrepubHeaders(sheet);
		}

		// TODO Mode prepublication => données pour les 5 dernières colonnes

		// ecriture des données
		int lineNumber = REM_FIRST_EMPTY_LINE;

		// boucle sur les exigences
		for (ExcelData req : data.getRequirements()) {

			// extraire les CTs liés à l'exigence de la map du binding
			List<Long> bindingCT = data.getBindings().stream().filter(p -> p.getResId().equals(req.getResId())).map(val -> val.getTclnId())
					.distinct().collect(Collectors.toList());

			if (bindingCT.isEmpty()) {
				Row currentRow = writeRequirementRow(req, sheet, lineNumber);
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
				Row rowWithTC = writeRequirementRow(req, sheet, lineNumber);

				// liste des steps pour l'exigence ET le cas de test courant
				if (testCase.getIsCoeurDeMetier()) {

					// TODO onglet coeur de métier => lecture des steps dans le binding
//				bindingSteps = liste.stream().filter(p -> p.getResId().equals(req.getResId()))
//						.map(val -> val.getStepId()).distinct().collect(Collectors.toList());
					writeCaseTestPartCoeurDeMetier(testCase, null, data.getSteps(), rowWithTC);
				} else { // non coeur de métier => on prends tous les steps du CT
					writeCaseTestPart(testCase, data.getSteps(), rowWithTC);
				}

				// colonnes prépublication nécessitant le CT
				if (boolPrebub) {
					Cell cell = rowWithTC.createCell(PREPUB_COLUMN_BON_POUR_PUBLICATION);
					if ((req.getReqStatus().equals(Constantes.STATUS_APPROVED))
							&& (testCase.getTcStatus().equals(Constantes.STATUS_APPROVED))) {
						cell.setCellValue(" X ");
					} else {
						cell.setCellValue(" ");
					}

					rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_EXIGENCE).setCellValue(req.getReference());
					rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_CAS_DE_TEST).setCellValue(testCase.getReference());
					rowWithTC.createCell(PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE).setCellValue(req.getReferenceSocle());
					rowWithTC.createCell(PREPUB_COLUMN_POINTS_DE_VERIF)
							.setCellValue(testCase.getPointsDeVerification());
				}
				lineNumber++;
			}

		} // exigences

		writeErrorSheet(workbook);

		LOGGER.info("  fin remplissage du woorkbook: " + workbook);

		if (!boolPrebub) {
			lockWorkbook(workbook);
		}
	}

	public void addPrepubHeaders(XSSFSheet sheet) {

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
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_BON_POUR_PUBLICATION,
				String.valueOf(PREPUB_COLUMN_BON_POUR_PUBLICATION + 1), style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_EXIGENCE,
				String.valueOf(PREPUB_COLUMN_REFERENCE_EXIGENCE + 1), style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_CAS_DE_TEST,
				String.valueOf(PREPUB_COLUMN_REFERENCE_CAS_DE_TEST + 1), style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE,
				String.valueOf(PREPUB_COLUMN_REFERENCE_EXIGENCE_SOCLE + 1), style);
		addHeadColumnPrepub(refrow, PREPUB_COLUMN_POINTS_DE_VERIF, String.valueOf(PREPUB_COLUMN_POINTS_DE_VERIF),
				style);
	}

	public void addHeadColumnPrepub(Row targetRow, int columnIndex, String label, CellStyle style) {
		Cell newcell = targetRow.createCell(columnIndex);
		newcell.setCellStyle(style);
		newcell.setCellValue(label);
	}

	public Row writeRequirementRow(ExcelData data, XSSFSheet sheet, int nextline) {
		// ecriture des données

		Row row = sheet.createRow(nextline);

		row.createCell(REM_COLUMN_CONDITIONNELLE).setCellValue(data.getBoolExigenceConditionnelle_1());

		row.createCell(REM_COLUMN_PROFIL).setCellValue(data.getProfil_2());

		row.createCell(REM_COLUMN_ID_SECTION).setCellValue(data.getId_section_3());

		row.createCell(REM_COLUMN_SECTION).setCellValue(data.getSection_4());

		row.createCell(REM_COLUMN_BLOC).setCellValue(data.getBloc_5());

		row.createCell(REM_COLUMN_FONCTION).setCellValue(data.getFonction_6());

		row.createCell(REM_COLUMN_NATURE).setCellValue(data.getNatureExigence_7());

		row.createCell(REM_COLUMN_NUMERO_EXIGENCE).setCellValue(data.getNumeroExigence_8());

		Cell multiligneCell = row.createCell(REM_COLUMN_ENONCE);
		CellStyle style = multiligneCell.getCellStyle();
		style.setWrapText(true);
		multiligneCell.setCellStyle(style);
		multiligneCell.setCellValue(data.getEnonceExigence_9());
		return row;

	}

	public void writeCaseTestPart(TestCase testcase, Map<Long, Step> steps, Row row) {
		// ecriture des données

		row.createCell(REM_COLUMN_NUMERO_SCENARIO).setCellValue(testcase.getReference());

		// cas des CTs non coeur de métier
		row.createCell(REM_COLUMN_SCENARIO_CONFORMITE)
				.setCellValue("Prérequis:\n " + Parser.convertHTMLtoString(testcase.getPrerequisite())
						+ "\n Description: \n  " + Parser.convertHTMLtoString(testcase.getDescription()));

		// TODO => erreur si la liste à plus de 10 steps et limiter bindingSteps à 10
		// les steps sont ordonnées dans la liste à partir du StepOrder
		int currentExcelColumn = REM_COLUMN_FIRST_NUMERO_PREUVE;
		for (Long stepId : testcase.getOrderedStepIds()) {
			Step currentStep = steps.get(stepId);

			row.createCell(currentExcelColumn).setCellValue(currentStep.getReference());
			currentExcelColumn++;

			row.createCell(currentExcelColumn)
					.setCellValue(Parser.convertHTMLtoString(currentStep.getExpectedResult()));
			currentExcelColumn++;
		}

	}

	public void writeCaseTestPartCoeurDeMetier(TestCase testcase, List<Long> bindedStepIds, Map<Long, Step> steps,
			Row row) {
		row.createCell(REM_COLUMN_NUMERO_SCENARIO).setCellValue(testcase.getReference());

		// cas des CTs coeur de métier
		row.createCell(REM_COLUMN_SCENARIO_CONFORMITE).setCellValue(" Cas de Test Coeur de métier ... ");
		// TODO cf. SFD
	}

	public static File flushToTemporaryFile(XSSFWorkbook workbook, String FileName) throws IOException {

		File tempFile = File.createTempFile(FileName, "xlsx");
		tempFile.deleteOnExit();
		FileOutputStream out = new FileOutputStream(tempFile);
		workbook.write(out);
		workbook.close();
		out.close();
		return tempFile;
	}

	public static String getProjectTrigram(String projectName) {

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

	public void writeErrorSheet(XSSFWorkbook workbook) {
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

	public void lockWorkbook(XSSFWorkbook workbook) {
		LOGGER.info("Appel pour lock d'une feuille du workbook");
//		String password = "abcd";
//		byte[] pwdBytes = null;
//		    try {
//		        pwdBytes  = Hex.decodeHex(password.toCharArray());
//		    } catch (DecoderException e) {
//		        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//		        LOGGER.error("erreur sur encodage du password");
//		    }
		// XSSFSheet sheet = workbookx.getSheetAt(0);
		lockSheet(workbook.getSheetAt(0));
		lockSheet(workbook.getSheetAt(1));

		workbook.lockStructure();
		// workbookx.lockWindows();
		workbook.lockRevision();

	}

	public void lockSheet(XSSFSheet sheet) {
		sheet.lockDeleteRows(true);
		sheet.lockDeleteColumns(true);
		sheet.lockInsertColumns(true);
		sheet.lockInsertRows(true);
		sheet.lockSort(false);
		sheet.lockFormatCells(false);
		sheet.lockFormatColumns(false);
		sheet.lockFormatRows(false);
		String password = generateRandomPassword();
		LOGGER.info("Unlock Password : {}", password);
		sheet.protectSheet(password);
		sheet.enableLocking();
	}

	public String generateRandomPassword() {
		return RandomStringUtils.random(255, 33, 122, false, false);
	}

}
