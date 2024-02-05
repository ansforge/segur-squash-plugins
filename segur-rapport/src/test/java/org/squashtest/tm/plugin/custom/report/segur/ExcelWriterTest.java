/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelRow;
import org.squashtest.tm.plugin.custom.report.segur.model.PerimeterData;
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
	//public static final String TEMPLATE_NAME = "template-segur-requirement-export.xlsx";
	//public static final String PREPUB_TEMPLATE_NAME = "template-segur-requirement-export-avec-colonnes-prepub.xlsx";
	public static final String TEMPLATE_NAME = "template-segur-requirement-export-modifie.xlsx";
	public static final String PREPUB_TEMPLATE_NAME = "template-segur-requirement-export-avec-colonnes-prepub-modifie.xlsx";
	private ExcelWriter excel;

	private DSRData data;

	@BeforeEach
	void loadData() {
		Traceur traceur = new Traceur();
		PerimeterData perimeterData = new PerimeterData();
		perimeterData.setMilestoneId(String.valueOf(1L));
		perimeterData.setProjectId(String.valueOf(1L));

		perimeterData.setProjectName("DSR_1");
		perimeterData.setMilestoneName("MILESTONE");
		perimeterData.setSquashBaseUrl("https://squash-segur.henix.com/squash/");

		data = new DSRData(traceur, new RequirementsCollectorImpl(), perimeterData);
		excel = new ExcelWriter(new Traceur());
		ExcelRow requirement1 = new ExcelRow();
		requirement1.setPerimetre_10("Vague 1");
		requirement1.setResId(1L);
		requirement1.setReqId(1L);
		requirement1.setBoolExigenceConditionnelle_1(Constantes.NON);
		requirement1.setProfil_2("Général");
		requirement1.setId_section_3("INS");
		requirement1.setSection_4("Gestion de l'ins");
		requirement1.setBloc_5("null");
		requirement1.setFonction_6("Alimentation manuelle");
		requirement1.setNatureExigence_7(Constantes.CATEGORIE_EXIGENCE);
		requirement1.setNumeroExigence_8("PSC.03");
		requirement1
				.setEnonceExigence_9("texte de l'éxigence avec paragraphes :</br> <p>P1 : text </p><p>P2 : text 2</p>");
		requirement1.setReqStatus(Constantes.STATUS_APPROVED);
		requirement1.setReference("REF-1");
		requirement1.setReferenceSocle("SC-PSC-03");
		requirement1.setNoteInterne("<p>Ceci est une note interne</p>");
		requirement1.setSegurRem("Remarque Ségur");
		requirement1.setCommentaire("Ceci est un autre commentaire");
		requirement1.setStatutPublication("Inchangée");
		requirement1.setVaNMoins1("OUI");
		data.getRequirements().add(requirement1);
		ExcelRow requirement2 = new ExcelRow();
		requirement2.setPerimetre_10("Vague 8");
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
				+ "        Lorsqu&#39;une BAL est bloqu&eacute;e par un administrateur global, des &quot;traces fonctionnelles&quot; et applicatives sont constitu&eacute;es et doivent au moins contenir les informations suivantes :</p>\r\n"
				+ "<ul>\r\n" + "        <li>"
				+ "                type d&#39;action ;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;</li>"
				+ "        <li>" + "                identit&eacute; de son auteur ;</li>" + "        <li>"
				+ "                dates et heures ;</li>" + "        <li>"
				+ "                moyens techniques utilis&eacute;s (LPS, WPS, etc..) ;</li>" + "        <li>"
				+ "                adresse r&eacute;seau</li>" + "        <li>" + "                ...</li>"
				+ "</ul>" + "<p>" + "        &nbsp;</p>\r\n"));
		requirement2.setReqStatus(Constantes.STATUS_APPROVED);
		requirement2.setReference("REF-2");
		requirement2.setReferenceSocle("SC-2");
		requirement2.setSocleResId(1L);
		requirement2.setCommentaire("Ceci est un commentaire");
		requirement2.setStatutPublication("Modifiée");
		requirement2.setVaNMoins1("NON");
		data.getRequirements().add(requirement2);
		ExcelRow requirement3 = new ExcelRow();
		requirement3.setPerimetre_10("Vague 13");
		requirement3.setResId(3L);
		requirement3.setReqId(3L);
		requirement3.setBoolExigenceConditionnelle_1(Constantes.NON);
		requirement3.setProfil_2("Général");
		requirement3.setId_section_3("MSS");
		requirement3.setSection_4("Échanges via MS-Santé");
		requirement3.setBloc_5("Transmission via MS-Santé");
		requirement3.setFonction_6("Envoi des messages MSS");
		requirement3.setNatureExigence_7(Constantes.CATEGORIE_EXIGENCE);
		requirement3.setNumeroExigence_8("CI-SIS/MSS.02");
		requirement3
				.setEnonceExigence_9("En cas de modification d'un des documents listés dans le DSR en annexe 3,"
						+ " le système DOIT pouvoir transmettre par messagerie sécurisée de santé"
						+ " la nouvelle version du document avec une mention du type &quot;annule"
						+ " &amp; remplace&quot; pré-paramétrée.");
		requirement3.setReqStatus(Constantes.STATUS_APPROVED);
		requirement3.setReference("SC.CI-SIS/MSS.02");
		requirement3.setCommentaire("Ceci est encore un autre commentaire");
		requirement3.setStatutPublication("Modifiée");
		requirement3.setVaNMoins1("OUI");
		data.getRequirements().add(requirement3);

		// TestCases
		TestCase test1 = new TestCase(1L, "SC.INS.01.01",
						"<ol>" 
						+ "<li>pré requis 1</li>" 
						+ "<li>pré requis 2</li>" 
						+ "</ol>"
						+ "<ol>" 
						+ "<li>element 1 ordonné liste 2</li>" 
						+ "<li>element 2 ordonné liste 2</li>" 
						+ "</ol>",
				"<p>paragraphe</p>\r\n" 
						+ "\r\n" 
						+ "<p>ligne 1<br />\r\n" + "ligne 2<br />\r\n" + "ligne 3</p>\r\n"
						+ "\r\n" + "<p>paragraphe 1</p>\r\n" + "\r\n" + "<p>paragraphe 2</p>\r\n" + "\r\n" + "<p> </p>",
				Constantes.STATUS_APPROVED, "Dossier Parent");
		data.getTestCases().put(1L, test1);
		// Steps
		Step s1t1 = new Step(1L, Parser.convertHTMLtoString(
				"résultat attendu step 2 (order 1)<BR/> <ul><li>1ere ligne</li><li>2eme ligne</li></ul>"), 0);
		s1t1.setReference("SC.INS.01.10");
		Step s2t1 = new Step(2L, Parser.convertHTMLtoString("<p>résultat attendu avec paragraphe (order 2)  &amp; (ampersand) ou &gt; (supérieur)<BR/></p>"),
				1);
		s2t1.setReference("SC.INS.01.01");
		List<Long> orderedStepIds = new ArrayList<>();
		orderedStepIds.add(s1t1.getTestSTepId());
		orderedStepIds.add(s2t1.getTestSTepId());
		test1.setOrderedStepIds(orderedStepIds);
		data.getSteps().put(1L, s1t1);
		data.getSteps().put(2L, s2t1);
		//TestCase test2 = new TestCase(2L, "SC.INS.02.01", "",
		TestCase test2 = new TestCase(2L, "SC.INS.02.01", "",
				"<strong>description</strong> du cas de test sans pré-requis"
				+ " et sans steps avec l&apos;apostrophe"
				+ "<ol start=\"3\">" 
				+ "<li>element ordonné (la liste démarre à 3)</li>" 
				+ "<li>element ordonné ( deuxième élément de la liste : numéro 4)</li>" 
				+ "</ol>"
				+"reprise du texte après la liste, il faut Une ligne blanche avant",
				Constantes.STATUS_APPROVED, "002 Dossier parent");
		data.getTestCases().put(2L, test2);
		TestCase test3 = new TestCase(3L, "SC.INS.04.01", "",
				"",
				Constantes.STATUS_APPROVED, "001 Dossier parent");
		test3.setIsCoeurDeMetier(true);
		List<Long> orderedStepIdsScenarioMetier = new ArrayList<>();
		orderedStepIdsScenarioMetier.add(1L);
		test3.setOrderedStepIds(orderedStepIdsScenarioMetier);
		data.getTestCases().put(3L, test3);
		// Test 4 obsolète : ne doit pas apparaitre
		TestCase test4 = new TestCase(4L, "SC.INS.03.01", "",
				"",
				"OBSOLETE", "001 Dossier parent");
		data.getTestCases().put(4L, test4);
		TestCase test5 = new TestCase(2L, "SC.INS.01.01", "",
				"<strong>description</strong> du cas de test sans pré-requis"
				+ " et sans steps avec l&apos;apostrophe"
				+ "<ol start=\"3\">" 
				+ "<li>element ordonné (la liste démarre à 3)</li>" 
				+ "<li>element ordonné ( deuxième élément de la liste : numéro 4)</li>" 
				+ "</ol>"
				+"reprise du texte après la liste, il faut Une ligne blanche avant",
				Constantes.STATUS_APPROVED, "002 Dossier parent");
		data.getTestCases().put(5L, test5);
		
		// binding REQ-TC
		ReqStepBinding r1t1 = new ReqStepBinding();
		r1t1.setResId(1L);
		r1t1.setTclnId(1L);
		ReqStepBinding r2t2 = new ReqStepBinding();
		r2t2.setResId(2L);
		r2t2.setTclnId(2L);
		ReqStepBinding r2t3 = new ReqStepBinding();
		r2t3.setResId(2L);
		r2t3.setTclnId(3L);
		ReqStepBinding r2t5 = new ReqStepBinding();
		r2t5.setResId(2L);
		r2t5.setTclnId(5L);
		ReqStepBinding r3t4 = new ReqStepBinding();
		r3t4.setResId(3L);
		r3t4.setTclnId(4L);

		data.getBindings().add(r1t1);
		data.getBindings().add(r2t2);
		data.getBindings().add(r2t3);
		data.getBindings().add(r2t5);
		data.getBindings().add(r3t4);

	}

	@Test
	void generateExcelFilePublication() throws Exception {
		XSSFWorkbook workbook = excel.loadWorkbookTemplate(TEMPLATE_NAME);
		// ecriture du workbook
		data.getPerimeter().setMilestoneStatus(Constantes.MILESTONE_LOCKED);
		excel.putDatasInWorkbook(workbook, data);
		String filename = this.getClass().getResource(".").getPath()
				+ "generateExcelFileWithOneRequirementNoTestCase.xlsx";
		LOGGER.info(filename);
		File tempFile = new File(filename);
		FileOutputStream out = new FileOutputStream(tempFile);
		workbook.write(out);
		assertEquals(242,workbook.getSheet("Exigences").getRow(2).getCell(10).getStringCellValue().length());
		
		workbook.close();
		out.close();
	}

	@Test
	void generateExcelFilePrepublication() throws Exception {
		data.getPerimeter().setMilestoneStatus("TEST");
		XSSFWorkbook workbook = excel.loadWorkbookTemplate(PREPUB_TEMPLATE_NAME);
		// ecriture du workbook
		data.getPerimeter().setMilestoneStatus("UNLOCKED");
		excel.putDatasInWorkbook(workbook, data);
		String filename = this.getClass().getResource(".").getPath() + "generateExcelFilePrepublication.xlsx";
		LOGGER.info(filename);
		File tempFile = new File(filename);
		FileOutputStream out = new FileOutputStream(tempFile);
		workbook.write(out);
		assertEquals(3,workbook.getSheet("Exigences").getRow(3).getCell(30).getStringCellValue().length());

		workbook.close();
		out.close();	
	}

	@Test
	void sortTestCaseList(){
		List<TestCase> tcList = new ArrayList<TestCase>();;
		tcList.add(data.getTestCases().get(1L));
		tcList.add(data.getTestCases().get(2L));
		tcList.add(data.getTestCases().get(3L));
		tcList.add(data.getTestCases().get(4L));

		for (TestCase tc : tcList ) {
			System.out.println(tc.getReference());
		}
		
		Collections.sort(tcList);

		for (TestCase tc : tcList ) {
			System.out.println(tc.getReference());
		}
	}
}
