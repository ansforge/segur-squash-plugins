/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;



/**
 * The Class CufToExcelDataTest.
 */
public class CufToExcelDataTest {
	
	
	
	List<Cuf> cufs = new ArrayList<Cuf>();
	
	static /*final*/ String labelSection = "MSS_2changes_via_MS-Sant�";
	static final String idSection = "MSS";
	static final String section = "2changes_via_MS-Sant�";
	
	/**
	 * Find specific cuf test.
	 */
	@Test
	public void findSpecificCufTest() {
		// JDD
		ReqModel reqModel = new  ReqModel(null,null,null,null,null,null);
		cufs.clear();
		cufs.add(new Cuf(Constantes.SECTION, labelSection));
		//cufs.add(new Cuf(CufCode.SECTION, "ttttt"));
		cufs.add(new Cuf("label1", "NotImportant"));
		cufs.add(new Cuf("label1", "NotImportantToo"));
		cufs.add(new Cuf("label2", "PeuImporte"));
		reqModel.setCufs(cufs);
		
		
		Cuf selectedCuf =  reqModel.findSpecificCuf(Constantes.SECTION); 
		assertEquals(selectedCuf.getLabel(),labelSection);
		assertEquals(selectedCuf.getCode(), Constantes.SECTION);
	}
	
	/**
	 * Split section test.
	 */
	@Test
	public void splitSectionTest() {
		ReqModel reqModel = new  ReqModel(null,null,null,null,null,null);
		//labelSection = "MSS_";
		reqModel.splitSectionAndSetExcelData(labelSection);
		assertEquals(idSection, reqModel.getExcelData().getId_section_3() );
		assertEquals(section, reqModel.getExcelData().getSection_4() );
//		System.out.println(reqModel.getExcelData().getId_section_3());
//		System.out.println(reqModel.getExcelData().getSection_4());
	}
	
	/**
	 * Calcul exigence conditionelle test.
	 */
	@Test
	public void calculExigenceConditionelleTest() {
		ReqModel reqModel = new  ReqModel(null,null,null,null,null,null);
		reqModel.calculExigenceConditionelle("Général");
		assertEquals(reqModel.getExcelData().getBoolExigenceConditionnelle_1(), Constantes.NON);
		reqModel.calculExigenceConditionelle("général");
		assertEquals(reqModel.getExcelData().getBoolExigenceConditionnelle_1(), Constantes.NON);
		reqModel.calculExigenceConditionelle("quelquonque_général");
		assertEquals(reqModel.getExcelData().getBoolExigenceConditionnelle_1(), Constantes.OUI);		
	}
	
	/**
	 * Calcul categorie test.
	 */
	@Test
	public void calculCategorieTest() {
		ReqModel reqModel = new ReqModel(null,null,null,null,null,null);
		reqModel.calculCategorieNature("exigence (Doit)");
		assertEquals(reqModel.getExcelData().getNatureExigence_7(), Constantes.CATEGORIE_EXIGENCE);
		reqModel.calculCategorieNature("eXigeNce (Doit)");
		assertEquals(reqModel.getExcelData().getNatureExigence_7(), Constantes.CATEGORIE_EXIGENCE);
		reqModel.calculCategorieNature("Exigence (Doit)");
		assertEquals(reqModel.getExcelData().getNatureExigence_7(), Constantes.CATEGORIE_EXIGENCE);
		reqModel.calculCategorieNature("Éxigence (Doit)");
		assertEquals(reqModel.getExcelData().getNatureExigence_7(), Constantes.CATEGORIE_EXIGENCE);
		reqModel.calculCategorieNature("éxigence (Doit)");
		assertEquals(reqModel.getExcelData().getNatureExigence_7(), Constantes.CATEGORIE_EXIGENCE);
		
		reqModel.calculCategorieNature("préconisation (Peut)");
		assertEquals(reqModel.getExcelData().getNatureExigence_7(), Constantes.CATEGORIE_PRECONISATION);
		reqModel.calculCategorieNature("preconisation (Peut)");
		assertEquals(reqModel.getExcelData().getNatureExigence_7(), Constantes.CATEGORIE_PRECONISATION);
		reqModel.calculCategorieNature("PrÉconisation (Peut)");
		assertEquals(reqModel.getExcelData().getNatureExigence_7(), Constantes.CATEGORIE_PRECONISATION);



				
	}
	
}
