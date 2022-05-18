/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.model;

import org.squashtest.tm.plugin.custom.report.segur.Constantes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelRow {

	/* ********************************************** */
	// Données pour la partie exigence du template excel
	/* ********************************************** */
	//non excel mais nécessaire pour le binding
	private Long resId;
	
	// Donn�e calcul�e NON l'exigence est rattach�e au profil 'Général' (CUF)
	private String boolExigenceConditionnelle_1 = Constantes.NON_RENSEIGNE;

	// CUF Profil
	private String profil_2 = "";

	// CUF section => premier elt avant '_' (ex INS_Gestion de
	// l'ins => INS)
	private String id_section_3 = "";

	// CUF section => chaine de caractère après le premier '_' (ex
	// INS_Gestion de l'ins => Gestion de l'ins)
	private String section_4 ="";

	// CUF bloc
	private String bloc_5 ="";

	// CUF fonction
	private String fonction_6 ="";

	// si catégorie = Exigence (DOIT)=> valeur EXIGENCE, si catégorie =
	// Préconisation (PEUT) valeur=PRECONISATION
	private String natureExigence_7 ="";

	// Référence
	private String numeroExigence_8 ="";

	// description texte RTE
	private String enonceExigence_9 = "";

    //pour les colonnes de prépublication
	private String reqStatus;
	private String reference;
	private String referenceSocle;
	//private Long socleResId;
}
