package org.squashtest.tm.plugin.custom.report.segur.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelData {

	/* ********************************************** */
	// Données pour la partie exigence du template excel
	/* ********************************************** */
	// Donn�e calcul�e NON l'exigence est rattach�e au profil 'G�n�ral' (CUF)
	private String boolExigenceConditionnelle_1;

	// CUF Profil
	private String profil_2;

	// Calcul�e: prefix de CUF section => premier elt avant '_' (ex INS_Gestion de
	// l'ins => INS)
	private String id_section_3;

	// Calcul�e: fin du CUF section => chaine de caract�re apr�s le premier '_' (ex
	// INS_Gestion de l'ins => Gestion de l'ins)
	private String section_4;

	// CUF bloc
	private String bloc_5;

	// CUF fonction
	private String fonction_6;

	// calcul�e: si cat�gorie = Exigence (DOIT)=> valeur EXIGENCE, si cat�gorie =
	// Pr�conisation (PEUT) valeur=PRECONISATION
	private String natureExigence_7;

	// r�f�rence
	private String numeroExigence_8;

	// description texte RTE (html � parser en BDD TODO � remplacer par CEllRich?
	private String enonceExigence_9;

	// R�f�rence du cas de test TODO => liste...de CT contenant une liste de pas de
	// test
	private String numeroScenario_10;

}
