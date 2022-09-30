/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.model;

import org.squashtest.tm.plugin.custom.report.segur.Constantes;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class ExcelRow.
 */
@Getter
@Setter
public class ExcelRow implements Comparable<ExcelRow>{

	/* ********************************************** */
	// Données pour la partie exigence du template excel
	/* ********************************************** */
	// non excel mais nécessaire pour le binding
	private Long resId;
	
	/**
	 * Id pour créer Hyperlien dans Excel
	 */
	private Long reqId;

	// l'exigence est rattachée au profil 'Général' (CUF)
	private String boolExigenceConditionnelle_1 = Constantes.NON_RENSEIGNE;

	// CUF Profil
	private String profil_2 = "";

	// CUF section => premier elt avant '_' (ex INS_Gestion de
	// l'ins => INS)
	private String id_section_3 = "";

	// CUF section => chaine de caractère après le premier '_' (ex
	// INS_Gestion de l'ins => Gestion de l'ins)
	private String section_4 = "";

	// CUF bloc
	private String bloc_5 = "";

	// CUF fonction
	private String fonction_6 = "";

	// si catégorie = Exigence (DOIT)=> valeur EXIGENCE, si catégorie =
	// Préconisation (PEUT) valeur=PRECONISATION
	private String natureExigence_7 = "";

	// Référence
	private String numeroExigence_8 = "";

	// description texte RTE
	private String enonceExigence_9 = "";

	// pour les colonnes de prépublication
	private String reqStatus;
	private String reference;
	private String noteInterne = "";
	private String segurRem = "";
	
	
	private String referenceSocle = "";
	private Long socleResId = 0L;
	/**
	 * IdSocle pour créer Hyperlien dans Excel
	 */
	private Long socleReqId = 0L;
	
	/**
	 * Clef de tri composée de la référence (socle en premier) concaténée à la réference du cas de test
	 */
	private String sortingKey;
	
	public void setSortingKey(String testRef) {
		
		switch (id_section_3) {
		case "INS":
			sortingKey = "0".concat(reference);
			break;

		case "DMP":
			sortingKey = "1".concat(reference);
			break;

		case "MSS":
			sortingKey = "2".concat(reference);
			break;

		case "PSC":
			sortingKey = "3".concat(reference);
			break;

		case "ANN":
			sortingKey = "4".concat(reference);
			break;

		case "TBB":
			sortingKey = "5".concat(reference);
			break;

		case "HOP":
			sortingKey = "6".concat(reference);
			break;

		case "DOC":
			sortingKey = "7".concat(reference);
			break;

		case "PORT":
			sortingKey = "8".concat(reference);
			break;
			
		case "EPR":
			sortingKey = "9".concat(reference);
			break;
			
		default:
			sortingKey = reference;
			break;
		}
		
		sortingKey = sortingKey.concat(testRef);
	}

	@Override
	public int compareTo(ExcelRow o) {
		if (sortingKey == null || o.getSortingKey() == null) {
			return 0;
		}
		return sortingKey.compareTo(o.getSortingKey());
	}
}
