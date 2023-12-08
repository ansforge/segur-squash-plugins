/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence.model;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.squashtest.tm.plugin.custom.export.convergence.Constantes;
import org.squashtest.tm.plugin.custom.export.convergence.Level;
import org.squashtest.tm.plugin.custom.export.convergence.Traceur;

import lombok.Getter;
import lombok.Setter;


/**
 * The Class ReqModel.
 */
@Getter
@Setter
public class ReqModel {

	private Long resId;
	private Long reqId;
	private Long projectId;
	private String category;
	private String description;
	private String reference;
	private String requirementStatus;

	private ExcelRow row = new ExcelRow();

	private List<Cuf> cufs;

	// calculé
	private String idSection;
	private String section;

	private Traceur traceur;

	/**
 * Instantiates a new req model.
 *
 * @param resId the res id
 * @param reference the reference
 * @param requirementStatus the requirement status
 * @param category the category
 * @param description the description
 */
public ReqModel(Long resId, Long reqId, String reference, String requirementStatus, String category, String description) {
		super();
		this.resId = resId;
		this.reqId = reqId;
		this.category = category;
		this.description = description;
		this.reference = reference;
		this.requirementStatus = requirementStatus;
	}

	/**
	 * Update data.
	 *
	 * @param traceur the traceur
	 * @return the excel row
	 */
	public ExcelRow updateData(@Qualifier("convergence.traceur")Traceur traceur) {
		this.traceur = traceur;

		// id nécessaire pour lecture des liens exigence-CTs-(steps)
		row.setResId(resId);
		
		row.setReqId(reqId);

		// les cufs ont été lus en BDD, on met à jour "excelRow"

		Cuf rawProfil = findSpecificCuf(Constantes.PROFIL);
		if (rawProfil == null) {
			traceur.addMessage(Level.ERROR, resId, "pas de cuf 'PROFIL' trouvé");
		} else {
			calculExigenceConditionelle(rawProfil.getLabel());
		}

		row.setProfil_2(rawProfil.getLabel());

		// traitement de la section
		Cuf rawSection = findSpecificCuf(Constantes.SECTION);
		splitSectionAndSetExcelData(rawSection.getLabel());

		row.setBloc_5(findSpecificCuf(Constantes.BLOC).getLabel());

		row.setFonction_6(findSpecificCuf(Constantes.FONCTION).getLabel());

		calculCategorieNature(category);

		row.setNumeroExigence_8(reference);

		row.setEnonceExigence_9(description);

		// colonnes prepublications:
		row.setReqStatus(requirementStatus);
		row.setReference(reference);
		row.setNoteInterne(findSpecificCuf(Constantes.NOTE_INTERNE).getLabel());
		row.setSegurRem(findSpecificCuf(Constantes.SEGUR_REM).getLabel());
		row.setCommentaire(findSpecificCuf(Constantes.COMMENTAIRE).getLabel());
		row.setStatutPublication(findSpecificCuf(Constantes.STATUT_PUBLICATION).getLabel());
		return row;
	}

	/**
	 * Find specific cuf.
	 *
	 * @param cufCode the cuf code
	 * @return the cuf
	 */
	public Cuf findSpecificCuf(String cufCode) {
		List<Cuf> found = cufs.stream().filter(currentCuf -> cufCode.equals(currentCuf.getCode()))
				.collect(Collectors.toList());
		// concatenation si plusieurs tags ...
		String labels = found.stream().map(n -> n.getLabel()).collect(Collectors.joining(" , "));
		found.get(0).setLabel(labels);
		return found.get(0);
	}

	/**
	 * Split section and set excel data.
	 *
	 * @param cufSection the cuf section
	 */
	public void splitSectionAndSetExcelData(String cufSection) {
		int separator = cufSection.indexOf(Constantes.SECTION_SEPARATOR);
		if (separator == -1) {
			traceur.addMessage(Level.ERROR, resId,
					"Impossible d'extraitre d'idSection et la section du CUF Section: " + cufSection);
		} else {
			row.setId_section_3(cufSection.substring(0, separator));
			row.setSection_4(cufSection.substring(separator + 1));
		}
	}

	/**
	 * Calcul exigence conditionelle.
	 *
	 * @param labelProfil the label profil
	 */
	public void calculExigenceConditionelle(String labelProfil) {

		if (labelProfil.isEmpty()) {
			traceur.addMessage(Level.ERROR, resId, "calculExigenceConditionelle impossible cuf Profil non renseigné ");
			return; // non renseigné par défaut
		}

		if (labelProfil.equalsIgnoreCase(Constantes.PROFIL_GENERAL)) {
			row.setBoolExigenceConditionnelle_1(Constantes.NON);
		} else {
			row.setBoolExigenceConditionnelle_1(Constantes.OUI);
		}
	}

	/**
	 * Calcul categorie nature.
	 *
	 * @param categorie the categorie
	 */
	public void calculCategorieNature(String categorie) {
		Boolean update = false;
		if (categorie != null) {
			categorie = categorie.replace('é', 'e');
			categorie = categorie.replace('É', 'E');

			if (categorie.toUpperCase().contains(Constantes.CATEGORIE_EXIGENCE)) {
				row.setNatureExigence_7(Constantes.CATEGORIE_EXIGENCE);
				update = true;
			} else if (categorie.toUpperCase().contains(Constantes.CATEGORIE_PRECONISATION)) {
				row.setNatureExigence_7(Constantes.CATEGORIE_PRECONISATION);
				update = true;
			}
		}
		if (update == false) {
			traceur.addMessage(Level.WARNING, resId,
					"Impossible d'identifier la nature pour l'exigence. Cuf'Catégorie'= " + categorie);
			row.setNatureExigence_7("");
		}

	}

}
