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
	public void testSanitize() {
		String html = "<span style=\"font-size:11.0pt\"/><p class=\"mb-1\">&nbsp;<BR/><BR/><BR/></p><p class=\"mb-1\">&nbsp;espace&nbsp;insécable&nbsp;avant&nbsp;le&nbsp;texte&nbsp;</p><BR/>";
		assertEquals("<p class=\"mb-1\">espace&nbsp;ins&eacute;cable&nbsp;avant&nbsp;le&nbsp;texte</p>",Parser.sanitize(html));
	}
}
