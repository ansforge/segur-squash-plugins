/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Iterator;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.squashtest.tm.plugin.custom.export.convergence.Constantes;
import org.squashtest.tm.plugin.custom.export.convergence.Level;
import org.squashtest.tm.plugin.custom.export.convergence.Parser;
import org.squashtest.tm.plugin.custom.export.convergence.Traceur;
import org.squashtest.tm.plugin.custom.export.convergence.model.Cuf;
import org.squashtest.tm.plugin.custom.export.convergence.model.ExcelRow;
import org.squashtest.tm.plugin.custom.export.convergence.model.LinkedReq;
import org.squashtest.tm.plugin.custom.export.convergence.model.PerimeterData;
import org.squashtest.tm.plugin.custom.export.convergence.model.ReqModel;
import org.squashtest.tm.plugin.custom.export.convergence.model.ReqStepBinding;
import org.squashtest.tm.plugin.custom.export.convergence.model.Step;
import org.squashtest.tm.plugin.custom.export.convergence.model.TestCase;
import org.squashtest.tm.plugin.custom.export.convergence.repository.RequirementsCollector;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class DSRData.
 */
@Getter
@Setter
public class DSRData {

	private static final String EXCLUDED_STATUS = "OBSOLETE";

	private static final Logger LOGGER = LoggerFactory.getLogger(DSRData.class);

	RequirementsCollector reqCollector;

	private List<ExcelRow> requirements = new ArrayList<>();

	private Map<Long, TestCase> testCases = new HashMap<>();

	private List<ReqStepBinding> bindings = new ArrayList<>();

	private Map<Long, Step> steps = new HashMap<>();

	private Traceur traceur;

	private PerimeterData perimeter;

	/**
	 * Instantiates a new DSR data.
	 *
	 * @param traceur      the traceur
	 * @param reqCollector the req collector
	 */
	public DSRData(Traceur traceur, @Qualifier("convergenceReportRepository")RequirementsCollector reqCollector, PerimeterData perimeter) {
		super();
		this.traceur = traceur;
		this.reqCollector = reqCollector;
		this.perimeter = perimeter;
	}

	public ExcelRow getRequirementById(Long id) {
		return requirements.stream().filter(row -> id == row.getReqId()).findAny().orElse(null);
	}

	/**
	 * Load data.
	 *
	 * @param perimeter the perimeter
	 */
	public void loadData() {
		traceur.addMessage(Level.INFO, "PROJET NAME",perimeter.getProjectName());
		
		List<LinkedReq> linkedOrNotReqs = reqCollector.findLinkedReq(perimeter.getProjectId(),
				perimeter.getMilestoneId());
		// Map des exgicences de l'arbre (projet) avec ID des exigences liées (attention
		// si une exigence n'est pas lié , elle n'est pas dans la map)
		////DEBUG DEBUT afffichage du contenu de la liste linkedOrNotReqs
		String msg3 = "";
		for (int j=0; j<linkedOrNotReqs.size();j++)
				{
					msg3 = linkedOrNotReqs.get(j) + " " + msg3;

				}
        traceur.addMessage(Level.INFO, "1-LIST",msg3);
		////DEBUG FIN afffichage du contenu de la liste linkedOrNotReqs


		Map<Long, Long> linkedReqs = getMapTreeRequirementAndlinkedRequirement(linkedOrNotReqs);
		
		////DEBUG DEBUT afffichage du contenu de la Map
		Set<Long> listKeys=linkedReqs.keySet();  // Obtenir la liste des clés
    	Iterator<Long> iterateur=listKeys.iterator();
    		// Parcourir les clés et afficher les entrées de chaque clé;
		String msg = "";
    	while(iterateur.hasNext())
    		{
    			Object key= iterateur.next();
				msg = key + "=>" + linkedReqs.get(key) + " " + msg;
    		}
		//traceur.addMessage(Level.INFO, "2-MAP",msg);
		////DEBUG FIN

		Set<Long> reqIds = populateRequirementData(linkedReqs, linkedOrNotReqs);
		
		////DEBUG DEBUT
		Iterator<Long> it = reqIds.iterator();
		String msg2 = "";
		while (it.hasNext()) {
			msg2 = it.next() + " " + msg2;
        }
		//traceur.addMessage(Level.INFO, "3-SET",msg2);
		////DEBUG FIN

		// lecture des liens exigence-CT et récupération de la liste des CTs à lire
		List<Long> distinctCT = setBinding(reqIds, perimeter.getMilestoneId(), linkedReqs);

		// lecture des données sur les CTs
		populateTestCases(distinctCT, perimeter);
		// Ajout des clefs de tri (REF SOCLE, puis réference Exigences, puis réference
		// scénario
		addSortingKeyToRequirementsRow();
		Collections.sort(requirements);
		// lecture des IDs des CTs 'coeur de métier' => sous un répertoire "_METIER" et
		// mise à jour de la propriété dans l'objet TestCase
		addTestCaseCoeurDeMetier(perimeter);

		// lecture des données sur les steps
		populateStepsData(distinctCT);
	}

	private Set<Long> populateRequirementData(Map<Long, Long> linkedReqs, List<LinkedReq> linkedOrNotReqs) {

		// liste des exigences de l'arbre
		Set<Long> treeReqIs = getTreeResId(linkedOrNotReqs);

		////DEBUG DEBUT affichage SET treeReqIs
		Iterator<Long> it1 = treeReqIs.iterator();
		String msg4 = "";
		while (it1.hasNext()) {
			msg4 = it1.next() + " " + msg4;
        }
		//traceur.addMessage(Level.INFO, "4-SET",msg4);
		////DEBUG FIN affichage SET treeReqIs

		// liste de toutes les exigences: arbre et liées
		Set<Long> allReqIds = new HashSet<Long>();
		allReqIds.addAll(treeReqIs);
		////DEBUG DEBUT affichage SET allReqIds
		Iterator<Long> it2 = allReqIds.iterator();
		String msg5 = "";
		while (it2.hasNext()) {
			msg5 = it2.next() + " " + msg5;
        }
		//traceur.addMessage(Level.INFO, "5-SET",msg5);
		////DEBUG FIN affichage SET allReqIds

		allReqIds.addAll(linkedReqs.values());

		////DEBUG DEBUT affichage SET allReqIds
		Iterator<Long> it3 = allReqIds.iterator();
		String msg6 = "";
		while (it3.hasNext()) {
			msg6 = it3.next() + " " + msg6;
        }
		//traceur.addMessage(Level.INFO, "6-SET",msg6);
		////DEBUG FIN affichage SET allReqIds

		// liste des exigences de l'arbre

		LOGGER.info(" treeReqIs " + treeReqIs.size());
		LOGGER.info(" allReqIds " + allReqIds.size());
		LOGGER.info(" linkedReqs " + linkedReqs.size());

		// Lecture des données de toutes les exigences (arbre et liées)
		Map<Long, ReqModel> reqs = reqCollector.mapFindRequirementByResId(allReqIds);
		LOGGER.info(" nombre d'exigences lues: " + reqs.size() + " dont nombre d'exigence dans l'arbre: "
				+ treeReqIs.size());

		Set<Long> reqKetSet = reqs.keySet();
		if (reqKetSet.size() == 0) {
			traceur.addMessage(Level.WARNING, "", "aucune exigence trouvée pour le projet");
		}

		// lecture des CUFs sur les exigences => cuf.field_type='MSF' => label dans
		// custom_field_value_option
		for (Long res_id : reqKetSet) {
			// cufs de l'exigence
			List<Cuf> cufs = reqCollector.findCUFsByResId(res_id);
			reqs.get(res_id).setCufs(cufs);
			// mise à jour des données
			reqs.get(res_id).updateData(traceur);
		}

		// construction de la liste des exigences pour le writer
		requirements = new ArrayList<ExcelRow>();

		// Mise à jour des données uniquement pour les exigences de l'arbre liées à une
		// exigence du socle
		for (Long resId : linkedReqs.keySet()) {
			ExcelRow projet = reqs.get(resId).getRow();
			ExcelRow socle = reqs.get(linkedReqs.get(resId)).getRow();
			ExcelRow update = mergeData(projet, socle);
			reqs.get(resId).setRow(update);
		}

		// ajout des exigences de l'arbre à la liste
		for (Long resIdP : treeReqIs) {
			ExcelRow row = reqs.get(resIdP).getRow();
			if (!row.getReqStatus().equals(EXCLUDED_STATUS)) {
				requirements.add(row);
			}
		}
		return reqKetSet;
	}

	private void populateTestCases(List<Long> xdistinctCT, PerimeterData perimeterData) {
		testCases = reqCollector.findTestCase(xdistinctCT);
		LOGGER.info(" lecture des données sur les CTs. Nbre CT: " + testCases.size());

		// mise à jour de la liste des Step dans les CTs
		TestCase tcTmp;
		List<Long> ctSteps;

		for (Long testCaseId : testCases.keySet()) {
			tcTmp = testCases.get(testCaseId);
			ctSteps = reqCollector.findStepIdsByTestCaseId(testCaseId);
			tcTmp.setOrderedStepIds(ctSteps);
			if (perimeterData.isPrePublication()) {
				List<String> ptsDeVerif = reqCollector.findPointsDeVerificationByTcStepsIds(ctSteps);
				StringBuilder builder = new StringBuilder();
				for (String verif : ptsDeVerif) {
					builder.append(Parser.convertHTMLtoString(verif));
				}
				tcTmp.setPointsDeVerification(builder.toString());
			}
			testCases.put(testCaseId, tcTmp);
		}
	}

	private void addSortingKeyToRequirementsRow() {
		for (ExcelRow requirement : requirements) {
			Optional<ReqStepBinding> binding = bindings.stream()
					.filter(b -> b.getResId().equals(requirement.getResId())).findFirst();
			if (binding.isPresent()) {
				requirement.setSortingKey(testCases.get(binding.get().getTclnId()).getReference());
			} else {
				requirement.setSortingKey("");
			}
		}
	}

	private void addTestCaseCoeurDeMetier(PerimeterData perimeterData) {
		perimeterData.setTclnIdFolderMetier(reqCollector.findIdFolderMetier(perimeterData.getProjectId()));
		LOGGER.info(" rootMetierId (tcln_id du répertoire des cas de test '_METIER' "
				+ perimeterData.getTclnIdFolderMetier());
		perimeterData.setIdsCasDeTestCoeurDeMetier(
				reqCollector.findCoeurMetierIdsByRootTcln_Id(perimeterData.getTclnIdFolderMetier()));
		LOGGER.info(" Nombre de cas de test coeurMetierIds trouvés sur le projet: "
				+ perimeterData.getIdsCasDeTestCoeurDeMetier().size());
		// Mise à jour de la propriété isCOeurDeMetier dans TestCase
		for (Long coeurDeMetierId : perimeterData.getIdsCasDeTestCoeurDeMetier()) {
			if (testCases.containsKey(coeurDeMetierId)) {
				testCases.get(coeurDeMetierId).setIsCoeurDeMetier(true);
			}
		}
	}

	private void populateStepsData(List<Long> distinctCT) {
		steps = reqCollector.findSteps(distinctCT);
		LOGGER.info(" lecture de tous les steps pour les CTs  steps. size: " + steps.size());
		// lecture des references des pas de test (CUF)
		String ref_step = "";
		Step currentStep = null;
		for (Long stepId : steps.keySet()) {
			// TODO => renvoyer une liste pour gérer proprement les cas ou il y a plus
			// d'une reference
//		nbRef = reqCollector.countStepReferenceByStepID(stepId);
//		if (nbRef!=1) {
//			//pb ...
//		}
			ref_step = reqCollector.findStepReferenceByTestStepId(stepId);
			currentStep = steps.get(stepId);
			currentStep.setReference(ref_step);
			steps.put(stepId, currentStep);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Long> setBinding(Set<Long> xreqIds, Long xmilestonesId, Map<Long, Long> linkedReqs) {

		bindings = reqCollector.findTestRequirementBindingFiltreJalonTC(xreqIds, xmilestonesId);
		LOGGER.info(" lecture en base des liens exigence/CT/step. Nb liens: " + bindings.size());

		// écrasement dans la liste des liens Req-Ct des resID des exigences liées par
		// celles qui seront publiées dans excel
		Map<Long, Long> inversedLinkedReq = MapUtils.invertMap(linkedReqs);
		for (ReqStepBinding reqStepBinding : bindings) {
			if (inversedLinkedReq.containsKey(reqStepBinding.getResId())) {
				reqStepBinding.setResId(inversedLinkedReq.get(reqStepBinding.getResId()));
				reqStepBinding.setFromSocle(true);
			}
		}

		// liste des CT à récupérer
		return bindings.stream().map(val -> val.getTclnId()).distinct().collect(Collectors.toList());
	}

	private Map<Long, Long> getMapTreeRequirementAndlinkedRequirement(List<LinkedReq> linkedReqs) {

		Map<Long, Long> treeResIdAndLinkedResId = new HashMap<Long, Long>();
		for (LinkedReq linkedReq : linkedReqs) {
			if (linkedReq.getSocleResId() != null) {
				if (treeResIdAndLinkedResId.containsKey(linkedReq.getResId())) {
					traceur.addMessage(Level.ERROR, linkedReq.getResId(),
							"Cette exigence est ignorée car elle est liée à au moins 2 autres exigences: ");
					treeResIdAndLinkedResId.remove(linkedReq.getResId());
				} else {
					treeResIdAndLinkedResId.put(linkedReq.getResId(), linkedReq.getSocleResId());
				}
			}

		}
		return treeResIdAndLinkedResId;
	}

	private Set<Long> getTreeResId(List<LinkedReq> linkedReqs) {
		Set<Long> result = new HashSet<Long>();
		for (LinkedReq linkedReq : linkedReqs) {
			result.add(linkedReq.getResId());
		}
		return result;
	}

	private ExcelRow mergeData(ExcelRow requirementRow, ExcelRow socleData) {
		ExcelRow updatedRequirement = new ExcelRow();
		// champs à ne pas merger
		updatedRequirement.setReferenceSocle(socleData.getReference());
		updatedRequirement.setSocleResId(socleData.getResId());
		updatedRequirement.setSocleReqId(socleData.getReqId());
		updatedRequirement.setReference(requirementRow.getReference());
		updatedRequirement.setResId(requirementRow.getResId());
		updatedRequirement.setReqId(requirementRow.getReqId());
		updatedRequirement.setOrdre(requirementRow.getOrdre());
		// Une exigence est obsolète si l'exigence socle l'est aussi.
		if (socleData.getReqStatus().equals(EXCLUDED_STATUS)) {
			updatedRequirement.setReqStatus(socleData.getReqStatus());
		} else {
			updatedRequirement.setReqStatus(requirementRow.getReqStatus());
		}
		// champs mergés
		if (requirementRow.getBoolExigenceConditionnelle_1().equals(Constantes.NON_RENSEIGNE)) {
			updatedRequirement.setBoolExigenceConditionnelle_1(socleData.getBoolExigenceConditionnelle_1());
		} else {
			updatedRequirement.setBoolExigenceConditionnelle_1(requirementRow.getBoolExigenceConditionnelle_1());
		}

		if (requirementRow.getEnonceExigence_9().isEmpty()) {
			updatedRequirement.setEnonceExigence_9(socleData.getEnonceExigence_9());
		} else {
			updatedRequirement.setEnonceExigence_9(requirementRow.getEnonceExigence_9());
		}

		if (requirementRow.getBloc_5().isEmpty()) {
			updatedRequirement.setBloc_5(socleData.getBloc_5());
		} else {
			updatedRequirement.setBloc_5(requirementRow.getBloc_5());
		}

		if (requirementRow.getFonction_6().isEmpty()) {
			updatedRequirement.setFonction_6(socleData.getFonction_6());
		} else {
			updatedRequirement.setFonction_6(requirementRow.getFonction_6());
		}

		if (requirementRow.getProfil_2().isEmpty()) {
			updatedRequirement.setProfil_2(socleData.getProfil_2());
		} else {
			updatedRequirement.setProfil_2(requirementRow.getProfil_2());
		}

		if (requirementRow.getProfil_2().isEmpty()) {
			updatedRequirement.setProfil_2(socleData.getProfil_2());
		} else {
			updatedRequirement.setProfil_2(requirementRow.getProfil_2());
		}

		if (requirementRow.getSection_4().isEmpty()) {
			updatedRequirement.setSection_4(socleData.getSection_4());
		} else {
			updatedRequirement.setSection_4(requirementRow.getSection_4());
		}

		if (requirementRow.getId_section_3().isEmpty()) {
			updatedRequirement.setId_section_3(socleData.getId_section_3());
		} else {
			updatedRequirement.setId_section_3(requirementRow.getId_section_3());
		}

		if (requirementRow.getNatureExigence_7().isEmpty()) {
			updatedRequirement.setNatureExigence_7(socleData.getNatureExigence_7());
		} else {
			updatedRequirement.setNatureExigence_7(requirementRow.getNatureExigence_7());
		}

		if (requirementRow.getNumeroExigence_8().isEmpty()) {
			updatedRequirement.setNumeroExigence_8(socleData.getNumeroExigence_8());
		} else {
			updatedRequirement.setNumeroExigence_8(requirementRow.getNumeroExigence_8());
		}
		if (requirementRow.getCommentaire().isEmpty()) {
			updatedRequirement.setCommentaire(socleData.getCommentaire());
		} else {
			updatedRequirement.setCommentaire(requirementRow.getCommentaire());
		}
		if (requirementRow.getStatutPublication().isEmpty()) {
			updatedRequirement.setStatutPublication(socleData.getStatutPublication());
		} else {
			updatedRequirement.setStatutPublication(requirementRow.getStatutPublication());
		}
		if (requirementRow.getPerimetre_10().isEmpty()) {
			updatedRequirement.setPerimetre_10(socleData.getPerimetre_10());
		} else {
			updatedRequirement.setPerimetre_10(requirementRow.getPerimetre_10());
		} 
		if (requirementRow.getProfilHistorique_11().isEmpty()) {
			updatedRequirement.setProfilHistorique_11(socleData.getProfilHistorique_11());
		} else {
			updatedRequirement.setProfilHistorique_11(requirementRow.getProfilHistorique_11());
		}
		if (requirementRow.getCriticite_12().isEmpty()) {
			updatedRequirement.setCriticite_12(socleData.getCriticite_12());
		} else {
			updatedRequirement.setCriticite_12(requirementRow.getCriticite_12());
		}

		return updatedRequirement;
	}

}
