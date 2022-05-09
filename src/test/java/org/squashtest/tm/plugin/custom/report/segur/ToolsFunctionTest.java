package org.squashtest.tm.plugin.custom.report.segur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;

public class ToolsFunctionTest {

	@Test
	public void extractNumeroExigenceTest() {
		ReqModel req= new ReqModel();
		String numero = req.extractNumeroExigence("SC.TMSS.25");
		assertEquals(numero,"TMSS.25");
		numero = req.extractNumeroExigence("CH.XYZ.25");
		assertEquals(numero,"XYZ.25");
		numero = req.extractNumeroExigence("DSR.XYZ.28");
		assertEquals(numero,"XYZ.28");
		// A revoir => Pb instanciation du traceur 
//		numero = req.extractNumeroExigence("DSR555");
//		assertTrue(numero.contains("ERREUR"));
//		numero = req.extractNumeroExigence("");
//		assertTrue(numero.contains("ERREUR"));
//		numero = req.extractNumeroExigence(".xxxxx");
//		assertTrue(numero.contains("ERREUR"));
	}
}
