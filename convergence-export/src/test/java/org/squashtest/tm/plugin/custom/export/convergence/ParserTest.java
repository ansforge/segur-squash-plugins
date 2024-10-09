package org.squashtest.tm.plugin.custom.export.convergence;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.HtmlUtils;

public class ParserTest {
    
	@Test
	public void formatTest() {
		String htmlFromSquash = "<p>'L'&eacute;diteur DOIT ex&eacute;cuter le sc&eacute;nario de conformit&eacute; suivant : <br /> </p> "
				+ "<ol> -"
				+ " <li> int&eacute;grer un fichier LOINC_4, un fichier LOINC_5 et un fichier LOINC_6 avec respectivement un code chapitre et un code sous chapitre absent du jeu de valeur circuit de la biologie, un code LOINC d'examen prescriptible absent du jeu de valeurs circuit de la biologie, un code LOINC d'analyse absent du jeu de valeurs circuit de la biologie.</li> "
				+ " <li> Int&eacute;grer les fichiers LOINC_4, LOINC_5 et LOINC_6.</li> "
				+ " <li> G&eacute;n&eacute;rer le rapport d'import du fichier LOINC_4, du fichier LOINC_5 et du fichier LOINC_6.</li> "
				//+ "<p></p>"
				+ "</ol>";
		
		String result = Parser.convertHTMLtoString(htmlFromSquash);
		System.out.println(result);
		assertFalse(result.contains("4."));
		}
	
	@Disabled
	public void escapeUtf8() {
		String toEscape = "<p>é ç à. => 1.2 > 5 ! </p> <ol><li>point 1</li></ol>"; 
		String result = StringEscapeUtils.escapeHtml4(toEscape);		
		System.out.println("avec StringEscapeUtils: " + result);	
		String escapedOutput = HtmlUtils.htmlEscape(toEscape);
		System.out.println("avec Spring: " + escapedOutput);
		result = StringEscapeUtils.unescapeXml(StringEscapeUtils.escapeHtml4(toEscape));
		System.out.println("avec StringEscapeUtils en ignorant tags xml: " + result);
	}
	
	@Test
	public void testSanitizeWhiteSpaces() {
		String html = "<span style=\"font-size:11.0pt\"/><p class=\"mb-1\">&nbsp;<BR/><BR/><BR/></p><p class=\"mb-1\">&nbsp;espace&nbsp;insécable&nbsp;avant&nbsp;le&nbsp;texte&nbsp;</p><ul><li>item</li></ul><BR/>";
		String expected = "<p class=\"mb-1\">&nbsp;espace&nbsp;ins&eacute;cable&nbsp;avant&nbsp;le&nbsp;texte</p>\n"
				+ "<ul class=\"mb-1\">\n"
				+ " <li>item</li>\n"
				+ "</ul>\n"
				+ "<br />";
		assertEquals(expected,Parser.sanitize(html));
	}

	@Test
	public void testSanitizeSuppressNewLine() {
		String html = "<p>Le système DOIT impl&eacute;menter le &quot;Volet de Transmission d&rsquo;un document CDA-R2 en HL7v2&quot; [CISIS3] permettant de cr&eacute;er et transmettre l&#39;archive IHE_XDM sur la base d&#39;un message HL7 V2 ORU/MDM provenant des syst&egrave;mes cr&eacute;ateurs de documents (DPI/RIS/SGL&hellip;).</p>\r\n";
		String expected = "<p class=\"mb-1\">Le syst&egrave;me DOIT impl&eacute;menter le &quot;Volet de Transmission d’un document CDA-R2 en HL7v2&quot; [CISIS3] permettant de cr&eacute;er et transmettre l'archive IHE_XDM sur la base d'un message HL7 V2 ORU/MDM provenant des syst&egrave;mes cr&eacute;ateurs de documents (DPI/RIS/SGL…).</p>";
		assertEquals(expected,Parser.sanitize(html));
	}
	
	@Test
	public void testSuppressDoubleBR() {
		String source = "<p>V&eacute;rifier que le syst&egrave;me est conforme &agrave; la sp&eacute;cification &laquo; Volet de Transmission de document(s) CDA en HL7v2 &raquo; [CISIS3]<br />\r\n"
				+ "<br />\r\n"
				+ "Etapes du sc&eacute;nario :</p>\r\n"
				+ "\r\n"
				+ "<ol>\r\n"
				+ "	<li>D&eacute;montrer la capacit&eacute; du syst&egrave;me &agrave; r&eacute;ceptionner les deux types de message (ORU et MDM), contenant une demande de traitement sur un document (demande d&#39;int&eacute;gration, de remplacement, de suppression) sur le syst&egrave;me cible.</li>\r\n"
				+ "	<li>Montrer la g&eacute;n&eacute;ration de l&#39;acquittement technique du message HL7</li>\r\n"
				+ "	<li>Montrer la cr&eacute;ation de l&rsquo;archive IHE_XDM correspondante</li>\r\n"
				+ "</ol>\r\n";
		
		String expected = "<p class=\"mb-1\">V&eacute;rifier que le syst&egrave;me est conforme &agrave; la sp&eacute;cification &laquo; Volet de Transmission de document(s) CDA en HL7v2 &raquo; [CISIS3]<br /> Etapes du sc&eacute;nario :</p> \n"
				+ "<ol class=\"mb-1\"> \n"
				+ " <li>D&eacute;montrer la capacit&eacute; du syst&egrave;me &agrave; r&eacute;ceptionner les deux types de message (ORU et MDM), contenant une demande de traitement sur un document (demande d'int&eacute;gration, de remplacement, de suppression) sur le syst&egrave;me cible.</li> \n"
				+ " <li>Montrer la g&eacute;n&eacute;ration de l'acquittement technique du message HL7</li> \n"
				+ " <li>Montrer la cr&eacute;ation de l’archive IHE_XDM correspondante</li> \n"
				+ "</ol>";
		assertEquals(expected,Parser.sanitize(source));
		
	}
	
	@Test
	public void testHref() {
		String source = "<p>Pr&eacute;requis</p>\r\n"
				+ "\r\n"
				+ "<ul>\r\n"
				+ "	<li>Message HL7v2 ORU K, anonyme, fourni par l&#39;ANS au sein du GitHub ANS [<a href=\"https://github.com/ansforge/DRIM-M_DRIMbox\" target=\"_blank\">https://github.com/ansforge/DRIM-M_DRIMbox</a>].</li> \n"
				+ "</ol>\r\n";
		String expected = "<p class=\"mb-1\">Pr&eacute;requis</p> \n"
				+ "<ul class=\"mb-1\"> \n"
				+ " <li>Message HL7v2 ORU K, anonyme, fourni par l'ANS au sein du GitHub ANS [<a href=\"https://github.com/ansforge/DRIM-M_DRIMbox\" target=\"_blank\">https://github.com/ansforge/DRIM-M_DRIMbox</a>].</li> \n"
				+ "</ul>";
		assertEquals(expected,Parser.sanitize(source));
	}
	
	@Test
	public void testSpanWithTextSuppress() {
		String source = "<p>Sc&eacute;nario - S&#39;assurer que les champs li&eacute;s &agrave; l&#39;identit&eacute; respectent les r&egrave;gles de nommage des identit&eacute;s<br />\r\n"
				+ "<ul>\r\n"
				+ "	<li>de minuscule (<span style=\"font-size:14px\"><span style=\"font-family:&quot;Aptos&quot;,sans-serif\"><span style=\"color:black\">&agrave; noter : la saisie doit se faire en majuscules en base, mais il reste possible d&#39;afficher &agrave; l&#39;utilisateur des minuscules pour le premier pr&eacute;nom de naissance et le pr&eacute;nom utilis&eacute;</span></span></span>)</li>\r\n"
				+ "	<li>d&#39;accents</li>\r\n"
				+ "	<li>de signes diacritiques</li>\r\n"
				+ "</ul>\r\n";
		String expected = "<p class=\"mb-1\">Sc&eacute;nario - S'assurer que les champs li&eacute;s &agrave; l'identit&eacute; respectent les r&egrave;gles de nommage des identit&eacute;s</p>\n"
				+ "<ul class=\"mb-1\"> \n"
				+ " <li>de minuscule (&agrave; noter : la saisie doit se faire en majuscules en base, mais il reste possible d'afficher &agrave; l'utilisateur des minuscules pour le premier pr&eacute;nom de naissance et le pr&eacute;nom utilis&eacute;)</li> \n"
				+ " <li>d'accents</li> \n"
				+ " <li>de signes diacritiques</li> \n"
				+ "</ul>";
		assertEquals(expected,Parser.sanitize(source));
	}
	
	@Test
	public void testSanitizeSuppressBREndOfParagraph() {
		String html = "<p>Le syst&egrave;me DOIT impl&eacute;menter le &quot;Volet de Transmission d&rsquo;un document CDA-R2 en HL7v2&quot; [CISIS3] permettant de cr&eacute;er et transmettre l&#39;archive IHE_XDM sur la base d&#39;un message HL7 V2 ORU/MDM provenant des syst&egrave;mes cr&eacute;ateurs de documents (DPI/RIS/SGL&hellip;).<br/></p>\r\n";
		String expected = "<p class=\"mb-1\">Le syst&egrave;me DOIT impl&eacute;menter le &quot;Volet de Transmission d’un document CDA-R2 en HL7v2&quot; [CISIS3] permettant de cr&eacute;er et transmettre l'archive IHE_XDM sur la base d'un message HL7 V2 ORU/MDM provenant des syst&egrave;mes cr&eacute;ateurs de documents (DPI/RIS/SGL…).</p>";
		assertEquals(expected,Parser.sanitize(html));
	}
	
	@Test
	public void testSanitizeSuppressEmptyParagraph() {
		String html = "<p class=\" mb-1\"></p>";
		String expected = "";
		assertEquals(expected,Parser.sanitize(html));
	}
	
	@Test
	public void testSanitizeSuppressParagraphWithNbsp() {
		String html = "<p class=\"mb-1\">&nbsp;</p>";
		String expected = "";
		assertEquals(expected,Parser.sanitize(html));
	}
	
	@Test
	public void testSanitizeNbspBeforeEndOfParagraph() {
		String html = "<p class=\"mb-1\">A conserver&nbsp;</p>";
		String expected = "<p class=\"mb-1\">A conserver</p>";
		assertEquals(expected,Parser.sanitize(html));
	}
	@Test
	public void testSanitizeSuppressBRAtStartOfParagraph() {
		String html = "<p class=\"mb-1\"><br/>A conserver</p>";
		String expected = "<p class=\"mb-1\">A conserver</p>";
		assertEquals(expected,Parser.sanitize(html));
	}
}
