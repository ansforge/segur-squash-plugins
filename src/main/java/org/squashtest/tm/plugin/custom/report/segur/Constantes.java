package org.squashtest.tm.plugin.custom.report.segur;

public class Constantes {

	public final static String SECTION_SEPARATOR = "_";

	// valeurs codes et labels des CUFs
	public final static String PROFIL = "PROFIL"; // CODE du CUF dans squash
	public final static String SECTION = "SECTION";
	public final static String FONCTION = "FONCTION";
	public final static String BLOC = "BLOC";

	public final static String REF_PREUVE = "REF_PREUVE";
	public final static String NATPREUVE = "NATPREUVE";
	public final static String VERIF_PREUVE = "VERIF_PREUVE";

	public final static String PROFIL_GENERAL = "Général";

	// CUFs
	public final static String CUF_TYPE_OBJECT_REQ = "REQUIREMENT_VERSION";
	public final static String CUF_TYPE_OBJECT_TEST_CASE = "TEST_CASE";
	public final static String CUF_TYPE_OBJECT_TEST_STEP = "TEST_STEP";
	public final static String CUF_FIELD_TYPE_MSF = "MSF";
	public final static String CUF_FIELD_TYPE_CF = "CF";
	public final static String CUF_FIELD_TYPE_RTF = "RTF";

	// valeurs BDD
	public final static String MILESTONE_LOCKED = "LOCKED";
	public final static String FOLDER_CT_METIER = "_METIER";
	public final static String STATUS_APPROVED = "APPROVED";

	// valeurs affich�es dans Excel
	public final static String OUI = "OUI";
	public final static String NON = "NON";
	public final static String NON_RENSEIGNE = "---";

	public final static String CATEGORIE_EXIGENCE = "EXIGENCE";
	public final static String CATEGORIE_PRECONISATION = "PRECONISATION";

	public final static String CRLF = "\r\n";
	// public static String CR_CURRENT_SYSTEM =
	// System.getProperty("line.separator");

	public final static String PREFIX_ELEMENT_LSITE_A_PUCES = "\t - ";

	public final static String PREFIX_PROJET_SOCLE = "SC";
	public final static String PREFIX_PROJET_CHANTIER = "CH";
	public final static int PREFIX_PROJET__METIER_SIZE = 3;
	// public final static String PREFIX_PROJET_PROJET = "";

	// voir aussi constantes définies dans ExcelWriterUtil => nom du template,
	// numéro des colonnes,...

}
