package org.squashtest.tm.plugin.custom.report.segur.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.Parser;
import org.squashtest.tm.plugin.custom.report.segur.Traceur;
import org.squashtest.tm.plugin.custom.report.segur.model.Cuf;
import org.squashtest.tm.plugin.custom.report.segur.model.ExcelData;
import org.squashtest.tm.plugin.custom.report.segur.model.ReqModel;

public class ReqHelper {

	public ReqHelper() {
	}

	ReqModel req;
	ExcelData excelData;

//	public ExcelData updateData (ReqModel req, Traceur traceur) {
	public ExcelData updateData(ReqModel req) {
		this.req = req;

		excelData = new ExcelData();

		// les cufs ont �t� lus en BDD, on met � jour "excelData"

		Cuf rawProfil = findSpecificCuf(Constantes.PROFIL);
		if (rawProfil == null) {
			// ExcelWriterUtil.addMessage(Level.ERROR, "res_Id = " + this.resId + " CUF
			// profil non trouvé impossible de calculer 'exigenceConditionnelle'");
		} else {
			calculExigenceConditionelle(rawProfil.getLabel());
		}

		excelData.setProfil_2(rawProfil.getLabel());

		// traitement de la section
		Cuf rawSection = findSpecificCuf(Constantes.SECTION);
		splitSectionAndSetExcelData(rawSection.getLabel());

		excelData.setBloc_5(findSpecificCuf(Constantes.BLOC).getLabel());

		excelData.setFonction_6(findSpecificCuf(Constantes.FONCTION).getLabel());

		calculCategorieNature(req.getCategorie());

		excelData.setNumeroExigence_8(req.getReference());

		excelData.setEnonceExigence_9(Parser.convertHTMLtoString(req.getDescription()));

		return excelData;

	}

	public Cuf findSpecificCuf(String cufCode) {
		List<Cuf> found = req.getCufs().stream().filter(currentCuf -> cufCode.equals(currentCuf.getCode()))
				.collect(Collectors.toList());

		if ((found != null) && (found.size() == 1)) {
			return found.get(0);
		} else {
			// TODO tracer les erreurs ....
			// ExcelWriterUtil.addMessage(Level.WARNING, "res_Id = " + this.resId + "cuf ("
			// + cufCode + ") non trouvé ou plusieurs valeurs possibles");
			// System.out.println("findSpecificCuf " + cufCode + "aucun element trouv� ou
			// plus d'un elt");
		}
		return null; // TODO � supprimer ...
	}

	public void splitSectionAndSetExcelData(String cufSection) {
		int separator = cufSection.indexOf(Constantes.SECTION_SEPARATOR);
		if (separator == -1) {
			// ExcelWriterUtil.addMessage(Level.WARNING, "res_Id = " + this.resId + " Erreur
			// sur découpage du cuf 'SECTION' : " + cufSection);
			// System.out.println("TODO erreur � tracer sur plit du cuf section");
			// TODO tracer une erreur et renvoyer chaine vide ou cuf Non splitter? ....
		} else {
			excelData.setId_section_3(cufSection.substring(0, separator));
			excelData.setSection_4(cufSection.substring(separator + 1));
		}
	}

	public void calculExigenceConditionelle(String labelProfil) {
		if (labelProfil.equalsIgnoreCase(Constantes.PROFIL_GENERAL)) {
			excelData.setBoolExigenceConditionnelle_1(Constantes.NON);
		} else {
			excelData.setBoolExigenceConditionnelle_1(Constantes.OUI);
		}
	}

	public void calculCategorieNature(String categorie) {
		categorie = categorie.replace('�', 'e');
		categorie = categorie.replace('�', 'E');

		if (categorie.toUpperCase().contains(Constantes.CATEGORIE_EXIGENCE)) {
			excelData.setNatureExigence_7(Constantes.CATEGORIE_EXIGENCE);
		} else if (categorie.toUpperCase().contains(Constantes.CATEGORIE_PRECONISATION)) {
			excelData.setNatureExigence_7(Constantes.CATEGORIE_PRECONISATION);
		} else {
			// TODO ERREUR
			System.out.println("pas d'identifaction de nature pour la cat�gorie = " + categorie);
		}
	}
}
