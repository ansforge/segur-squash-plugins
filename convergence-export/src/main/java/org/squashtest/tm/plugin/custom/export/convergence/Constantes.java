/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence;


/**
 * The Class Constantes.
 */
public class Constantes {

	/** The Constant SECTION_SEPARATOR. */
	public final static String SECTION_SEPARATOR = "_";

	/** The Constant PROFIL. */
	// valeurs codes et labels des CUFs
	public final static String PROFIL = "PROFIL"; // CODE du CUF dans squash

	// valeurs codes et labels des CUFs
	public final static String PROFIL_HISTO = "PROFIL_HISTO"; // CODE du CUF dans squash
	
	/** The Constant SECTION. */
	public final static String SECTION = "SECTION";
	
	/** The Constant FONCTION. */
	public final static String FONCTION = "FONCTION";
	
	/** The Constant BLOC. */
	public final static String BLOC = "BLOC";

	/** The Constant REF_PREUVE. */
	public final static String REF_PREUVE = "REF_PREUVE";
	
	/** The Constant NATPREUVE. */
	public final static String NATPREUVE = "NATPREUVE";
	
	/** The Constant VERIF_PREUVE. */
	public final static String VERIF_PREUVE = "VERIF_PREUVE";
	
	/** The Constant NOTE_INTERNE. */
	public final static String NOTE_INTERNE = "NOTE_INTERNE";
	
	/** The Constant SEGUR_REM. */
	public final static String SEGUR_REM = "SEGUR_REM";

	/** The Constant COMENTAIRE. */
	public final static String COMMENTAIRE = "COMMENTAIRE";

	/** The Constant STATUT_PUBLICATION. */
	public final static String STATUT_PUBLICATION = "STATUT_PUBLICATION";

	/** The Constant PERIMETRE. */
	public final static String PERIMETRE = "PERIMETRE";

	/** The Constant PROFIL_GENERAL. */
	public final static String PROFIL_GENERAL = "Général";


	/** The Constant CUF_TYPE_OBJECT_REQ. */
	// CUFs
	public final static String CUF_TYPE_OBJECT_REQ = "REQUIREMENT_VERSION";
	
	/** The Constant CUF_TYPE_OBJECT_TEST_CASE. */
	public final static String CUF_TYPE_OBJECT_TEST_CASE = "TEST_CASE";
	
	/** The Constant CUF_TYPE_OBJECT_TEST_STEP. */
	public final static String CUF_TYPE_OBJECT_TEST_STEP = "TEST_STEP";
	
	/** The Constant CUF_FIELD_TYPE_MSF. */
	public final static String CUF_FIELD_TYPE_MSF = "MSF";
	
	/** The Constant CUF_FIELD_TYPE_CF. */
	public final static String CUF_FIELD_TYPE_CF = "CF";
	
	/** The Constant CUF_FIELD_TYPE_RTF. */
	public final static String CUF_FIELD_TYPE_RTF = "RTF";

	/** The Constant MILESTONE_LOCKED. */
	// valeurs BDD
	public final static String MILESTONE_LOCKED = "LOCKED";
	
	/** The Constant FOLDER_CT_METIER. */
	public final static String FOLDER_CT_METIER = "_METIER";
	
	/** The Constant STATUS_APPROVED. */
	public final static String STATUS_APPROVED = "APPROVED";

	/** The Constant OUI. */
	// valeurs affich�es dans Excel
	public final static String OUI = "OUI";
	
	/** The Constant NON. */
	public final static String NON = "NON";
	
	/** The Constant NON_RENSEIGNE. */
	public final static String NON_RENSEIGNE = "---";

	/** The Constant CATEGORIE_EXIGENCE. */
	public final static String CATEGORIE_EXIGENCE = "EXIGENCE";
	
	/** The Constant CATEGORIE_PRECONISATION. */
	public final static String CATEGORIE_PRECONISATION = "PRECONISATION";

	
	/** The Constant CRITICITE. */
	public final static String CRITICITE = "CRITICITE";
	public final static String CRITICALITY_MINOR = "MINOR";
	public final static String CRITICALITY_MAJOR = "MAJOR";
	public final static String CRITICALITY_CRITICAL = "CRITICAL";
	public final static String CRITICALITY_UNDEFINED = "UNDEFINED";

	/** The Constant CRLF. */
	public final static String LINE_SEPARATOR = "\n";

	/** The Constant PREFIX_ELEMENT_LSITE_A_PUCES. */
	public final static String PREFIX_ELEMENT_LISTE_A_PUCES = "* ";

	/** The Constant PREFIX_PROJET_SOCLE. */
	public final static String PREFIX_PROJET_SOCLE = "SC";
	
	/** The Constant PREFIX_PROJET_CHANTIER. */
	public final static String PREFIX_PROJET_CHANTIER = "CH";
	
	/** The Constant PREFIX_PROJET__METIER_SIZE. */
	public final static int PREFIX_PROJET__METIER_SIZE = 3;


	// voir aussi constantes définies dans ExcelWriterUtil => nom du template,
	// numéro des colonnes,...

}
