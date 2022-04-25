package org.squashtest.tm.plugin.custom.report.segur.model;

import java.util.List;
import java.util.stream.Collectors;

import org.squashtest.tm.plugin.custom.report.segur.Constantes;
import org.squashtest.tm.plugin.custom.report.segur.Parser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqModel extends BasicReqModel {

	//id de requirement version
//	private String sResId;
	
	private Long resId;
	private Long projectId;
	private String categorie;
	private String description;
	private String reference;
	private String requirementStatus;
	
	private ExcelData excelData = new ExcelData();
	
	//tmp?
	private List<Cuf> cufs;
			
	//calcul�e
	private String idSection;
	private String section;
	
	//constructeur pour requête Jooq
	public ReqModel( Long projectId, Long resId, String reference,
			String requirementStatus, String categorie, String description) {
		super();
		this.resId = resId;
		this.projectId = projectId;
		this.categorie = categorie;
		this.description = description;
		this.reference = reference;
		this.requirementStatus = requirementStatus;
	}
	
	public void updateData () {
		//les cufs ont �t� lus en BDD, on met � jour "excelData"

		//
		Cuf rawProfil =  findSpecificCuf(Constantes.PROFIL);
		calculExigenceConditionelle(rawProfil.getLabel()); 
	
		excelData.setProfil_2(rawProfil.getLabel());
		
		//traitement de la section	
		Cuf rawSection =  findSpecificCuf(Constantes.SECTION);
		splitSectionAndSetExcelData(rawSection.getLabel());
		
		excelData.setBloc_5( findSpecificCuf(Constantes.BLOC).getLabel());
		
		excelData.setFonction_6( findSpecificCuf(Constantes.FONCTION).getLabel());
		
		calculCategorieNature(categorie);
		
		excelData.setNumeroExigence_8(reference);
		
	
		excelData.setEnonceExigence_9(Parser.parsedDescription(description)); 
		
	}
	
	public Cuf findSpecificCuf(String cufCode) {
		List<Cuf> found = cufs.stream()
				  .filter(currentCuf -> cufCode.equals(currentCuf.getCode()))
				  .collect(Collectors.toList());
				  

		if ((found != null) && (found.size()==1))
		{
			return found.get(0);
		}
		else {
			//TODO tracer les erreurs ....
			System.out.println("findSpecificCuf " + cufCode + "aucun element trouv� ou plus d'un elt"); 
		}
		return null; //TODO � supprimer ...
	}
	
	public void splitSectionAndSetExcelData(String cufSection) {
		int separator = cufSection.indexOf(Constantes.SECTION_SEPARATOR);
		if (separator == -1 ) { 
			System.out.println("TODO erreur � tracer sur plit du cuf section");
		//TODO tracer une erreur et renvoyer chaine vide ou cuf Non splitter? ....
	}
		else 
		{
			excelData.setId_section_3(cufSection.substring(0, separator));
			excelData.setSection_4(cufSection.substring(separator +1));
		}
	}
	
	public String calculFonction() {
		return "TODO: parse du CUF fonction = ";
	}
	
	public void calculExigenceConditionelle(String labelProfil) {
		if (labelProfil.equalsIgnoreCase(Constantes.PROFIL_GENERAL)) {
			excelData.setBoolExigenceConditionnelle_1(Constantes.NON);
		}
		else
		{
			excelData.setBoolExigenceConditionnelle_1(Constantes.OUI);
		}
	}
	
	public void calculCategorieNature(String categorie) {
		categorie = categorie.replace('�', 'e');
		categorie = categorie.replace('�', 'E');
		
		if (categorie.toUpperCase().contains(Constantes.CATEGORIE_EXIGENCE)) {
			excelData.setNatureExigence_7(Constantes.CATEGORIE_EXIGENCE);
		}
		else if (categorie.toUpperCase().contains(Constantes.CATEGORIE_PRECONISATION))
		{
			excelData.setNatureExigence_7(Constantes.CATEGORIE_PRECONISATION);
		}
		else
		{
			//TODO ERREUR
			System.out.println("pas d'identifaction de nature pour la cat�gorie = " + categorie);
		}
	}	
	
}
