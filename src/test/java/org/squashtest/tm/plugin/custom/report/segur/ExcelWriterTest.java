/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelRow;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepBinding;
import org.squashtest.tm.plugin.custom.report.segur.model.Step;
import org.squashtest.tm.plugin.custom.report.segur.model.TestCase;
import org.squashtest.tm.plugin.custom.report.segur.repository.impl.RequirementsCollectorImpl;
import org.squashtest.tm.plugin.custom.report.segur.service.impl.DSRData;
import org.squashtest.tm.plugin.custom.report.segur.service.impl.ExcelWriter;


/**
 * The Class ExcelWriterTest.
 */
public class ExcelWriterTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriterTest.class);
	
	/** The Constant TEMPLATE_NAME. */
	public static final String TEMPLATE_NAME = "template-segur-requirement-export.xlsx";
	private ExcelWriter excel;

	private DSRData data;

	@BeforeEach
	void loadData() {
		Traceur traceur = new Traceur();
		data = new DSRData(traceur, new RequirementsCollectorImpl());
		excel = new ExcelWriter(new Traceur());
		ExcelRow requirement1 = new ExcelRow();
		requirement1.setResId(1L);
		requirement1.setReqId(1L);
		requirement1.setBoolExigenceConditionnelle_1(Constantes.NON);
		requirement1.setProfil_2("Général");
		requirement1.setId_section_3("INS");
		requirement1.setSection_4("Gestion de l'ins");
		requirement1.setBloc_5("null");
		requirement1.setFonction_6("Alimentation manuelle");
		requirement1.setNatureExigence_7(Constantes.CATEGORIE_EXIGENCE);
		requirement1.setNumeroExigence_8("SC.INS.01.02");
		requirement1.setEnonceExigence_9("texte de l'éxigence non mis en forme");
		requirement1.setReqStatus(Constantes.STATUS_APPROVED);
		requirement1.setReference(null);
		requirement1.setReferenceSocle(null);
		data.getRequirements().add(requirement1);
		ExcelRow requirement2 = new ExcelRow();
		requirement2.setResId(2L);
		requirement2.setReqId(2L);
		requirement2.setBoolExigenceConditionnelle_1(Constantes.NON);
		requirement2.setProfil_2("Général");
		requirement2.setId_section_3("INS");
		requirement2.setSection_4("Gestion de l'ins");
		requirement2.setBloc_5("Alimentation du DMP via une PFI");
		requirement2.setFonction_6("Alimentation manuelle");
		requirement2.setNatureExigence_7(Constantes.CATEGORIE_EXIGENCE);
		requirement2.setNumeroExigence_8("SC.INS.01.01");
		requirement2.setEnonceExigence_9(Parser.convertHTMLtoString("<p>\r\n"
				+ "        Lorsqu&#39;une BAL est bloqu&eacute;e par un administrateur global, des traces fonctionnelles et applicatives sont constitu&eacute;es et doivent au moins contenir les informations suivantes :</p>\r\n"
				+ "<ul>\r\n"
				+ "        <li>\r\n"
				+ "                type d&#39;action ;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;</li>\r\n"
				+ "        <li>\r\n"
				+ "                identit&eacute; de son auteur ;</li>\r\n"
				+ "        <li>\r\n"
				+ "                dates et heures ;</li>\r\n"
				+ "        <li>\r\n"
				+ "                moyens techniques utilis&eacute;s (LPS, WPS, etc..) ;</li>\r\n"
				+ "        <li>\r\n"
				+ "                adresse r&eacute;seau</li>\r\n"
				+ "        <li>\r\n"
				+ "                ...</li>\r\n"
				+ "</ul>\r\n"
				+ "<p>\r\n"
				+ "        &nbsp;</p>\r\n"
				+ ""));
		requirement2.setReqStatus(Constantes.STATUS_APPROVED);
		requirement2.setReference(null);
		requirement2.setReferenceSocle(null);
		data.getRequirements().add(requirement2);
		
		//TestCases
		TestCase test1 = new TestCase(1L, "SC.INS.01.01", "mon pré-requis", "description du cas de test<BR/> multiligne", Constantes.STATUS_APPROVED);
		data.getTestCases().put(1L, test1);
		//Steps
		Step s1t1 = new Step(1L, Parser.convertHTMLtoString("résultat attendu step 2 (order 1)<BR/> <ul><li>1ere ligne</li><li>2eme ligne</li></ul>"), 0);
		s1t1.setReference("SC.INS.01.10");
		Step s2t1 = new Step(2L, Parser.convertHTMLtoString("<p>résultat attendu avec paragraphe (order 2)<BR/></p>"), 1);
		s2t1.setReference("SC.INS.01.01");
		List<Long> orderedStepIds = new ArrayList<>();
		orderedStepIds.add(s1t1.getTestSTepId());
		orderedStepIds.add(s2t1.getTestSTepId());
		test1.setOrderedStepIds(orderedStepIds);
		data.getSteps().put(1L, s1t1);
		data.getSteps().put(2L, s2t1);
		TestCase test2 = new TestCase(2L, "SC.INS.02.01", null, "description du cas de test sans pré-requis et sans steps avec l&apos;apostrophe", Constantes.STATUS_APPROVED);
		data.getTestCases().put(2L, test2);
		// binding REQ-TC
		ReqStepBinding r1t1 = new ReqStepBinding();
		r1t1.setResId(1L);
		r1t1.setTclnId(1L);
		ReqStepBinding r2t2 = new ReqStepBinding();
		r2t2.setResId(2L);
		r2t2.setTclnId(2L);
		data.getBindings().add(r1t1);
		data.getBindings().add(r2t2);
		
	}

	@Test
	void generateExcelFileWithOneRequirementNoTestCase() throws Exception {
		XSSFWorkbook workbook = excel.loadWorkbookTemplate(TEMPLATE_NAME);
		// ecriture du workbook
		excel.putDatasInWorkbook(false, workbook, data);
		String filename = this.getClass().getResource(".").getPath()
				+ "generateExcelFileWithOneRequirementNoTestCase.xlsx";
		LOGGER.error(filename);
		File tempFile = new File(filename);
		FileOutputStream out = new FileOutputStream(tempFile);
		workbook.write(out);
		workbook.close();
		out.close();
	}

}
