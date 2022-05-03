package org.squashtest.tm.plugin.custom.report.segur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.jooq.CommonTableExpression;
import org.jooq.Table;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqStepCaseBinding;
import org.squashtest.tm.plugin.custom.report.segur.repository.RequirementsCollector;

import java.util.stream.Collectors;

import javax.inject.Inject;

public class TmpTest {
	
	
//	@Test
//	public void testMapTest() {
//		Map map = new HashedMap();
//		List<ReqStepCaseBinding> liste = new ArrayList<ReqStepCaseBinding>();
//		
//		ReqStepCaseBinding req1 = new ReqStepCaseBinding();
//		req1.setReqVersionCoverageId(7049L);
//		req1.setResId(7386L);
//		req1.setTclnId(8859L);
//		req1.setStepId(10716L);
//		
//		ReqStepCaseBinding req2 = new ReqStepCaseBinding();
//		req2.setReqVersionCoverageId(7049L);
//		req2.setResId(7386L);
//		req2.setTclnId(8859L);
//		req2.setStepId(10717L);
//		
//		ReqStepCaseBinding req3 = new ReqStepCaseBinding();
//		req3.setReqVersionCoverageId(7054L);
//		req3.setResId(7390L);
//		req3.setTclnId(8859L);
//		req3.setStepId(10716L);
//		
//		ReqStepCaseBinding req4 = new ReqStepCaseBinding();
//		req4.setReqVersionCoverageId(7055L);
//		req4.setResId(7391L);
//		req4.setTclnId(8861L);
//		req4.setStepId(10719L);
//		
//		ReqStepCaseBinding req5 = new ReqStepCaseBinding();
//		req5.setReqVersionCoverageId(7059L);
//		req5.setResId(7397L);
//		req5.setTclnId(8864L);
//		req5.setStepId(10726L);
//		
//		ReqStepCaseBinding req6 = new ReqStepCaseBinding();
//		req6.setReqVersionCoverageId(7111L);
//		req6.setResId(7386L);
//		req6.setTclnId(8864L);
//		req6.setStepId(10726L);
//		
//		
//		liste.add(req1);
//		liste.add(req2);
//		liste.add(req3);
//		liste.add(req4);
//		liste.add(req5);
//		liste.add(req6);
//		
//
////		List<Long> distinctCT = liste.stream()
////				.map(val -> val.getTclnId())
////				.distinct()
////				.collect(Collectors.toList());
//		
//		
//		List<Long> bindingCT = liste.stream()
//				.filter(p-> p.getResId().equals(7386L))
//				.map(val -> val.getTclnId())
//				.distinct()
//				.collect(Collectors.toList());
//	}
	


}
