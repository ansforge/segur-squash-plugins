package org.squashtest.tm.plugin.custom.report.segur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.squashtest.tm.plugin.custom.report.segur.model.LinkedReq;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;

import com.google.common.collect.ImmutableSet;

public class ToolsFunctionTest {

	@Test
	public void extractNumeroExigenceTest() {
		ReqModel req = new ReqModel();
		String numero = req.extractNumeroExigence("SC.TMSS.25");
		assertEquals(numero, "TMSS.25");
		numero = req.extractNumeroExigence("CH.XYZ.25");
		assertEquals(numero, "XYZ.25");
		numero = req.extractNumeroExigence("DSR.XYZ.28");
		assertEquals(numero, "XYZ.28");
		// A revoir => Pb instanciation du traceur
//		numero = req.extractNumeroExigence("DSR555");
//		assertTrue(numero.contains("ERREUR"));
//		numero = req.extractNumeroExigence("");
//		assertTrue(numero.contains("ERREUR"));
//		numero = req.extractNumeroExigence(".xxxxx");
//		assertTrue(numero.contains("ERREUR"));
	}

	@Test

	public void tmpTest() {
		LinkedReq l1 = new LinkedReq(1L, 11L);
		LinkedReq l2 = new LinkedReq(2L, 22L);
		LinkedReq l22 = new LinkedReq(2L, 23L);
		LinkedReq l3 = new LinkedReq(3L, 33L);
		LinkedReq l4 = new LinkedReq(4L, null);
		List<LinkedReq> linkedReqs = new ArrayList<LinkedReq>();
		linkedReqs.add(l1);
		linkedReqs.add(l2);
		linkedReqs.add(l22);
		linkedReqs.add(l3);
		linkedReqs.add(l4);

		Map<Long, Long> treeResIdAndLinkedResId = new HashMap();
		for (LinkedReq linkedReq : linkedReqs) {
			if (linkedReq.getSocleResId() != null) {
				if (treeResIdAndLinkedResId.containsKey(linkedReq.getResId())) {
					System.out.println("doublon " + linkedReq.getResId());
					treeResIdAndLinkedResId.remove(linkedReq.getResId());
				} else {
					treeResIdAndLinkedResId.put(linkedReq.getResId(), linkedReq.getSocleResId());
				
				}
			}
		}
		System.out.println("treeResIdLinkedResId size " + treeResIdAndLinkedResId.size());		
		//Construction de la liste des exigences à remonter: resID + exigences liées
		Set<Long> keyIds = treeResIdAndLinkedResId.keySet();
		//allResIds.addAll(ImmutableSet.copyOf(treeResIdLinkedResId.values()));
		Set<Long> values = new HashSet<Long>(treeResIdAndLinkedResId.values());
		Set<Long> allResIds = new HashSet<Long>();
		allResIds.addAll(keyIds);
		allResIds.addAll(values);
		System.out.println("allResIds" + allResIds);
		
		
	}
	
	
}
