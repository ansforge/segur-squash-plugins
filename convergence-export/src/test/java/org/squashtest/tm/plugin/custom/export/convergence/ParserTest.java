package org.squashtest.tm.plugin.custom.export.convergence;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.HtmlUtils;

public class ParserTest {
    
	//@Test
	public void formatTest() {
		String htmlFromSquash = "<p>'L'&eacute;diteur DOIT ex&eacute;cuter le sc&eacute;nario de conformit&eacute; suivant : <br /> </p> \r\n"
				+ "<ol> -\r\n"
				+ " <li> int&eacute;grer un fichier LOINC_4, un fichier LOINC_5 et un fichier LOINC_6 avec respectivement un code chapitre et un code sous chapitre absent du jeu de valeur circuit de la biologie, un code LOINC d'examen prescriptible absent du jeu de valeurs circuit de la biologie, un code LOINC d'analyse absent du jeu de valeurs circuit de la biologie.</li> \r\n"
				+ " <li> Int&eacute;grer les fichiers LOINC_4, LOINC_5 et LOINC_6.</li> \r\n"
				+ " <li> G&eacute;n&eacute;rer le rapport d'import du fichier LOINC_4, du fichier LOINC_5 et du fichier LOINC_6.</li> \r\n"
				//+ "<p></p>\r\n"
				+ "</ol>";
		
		String result = Parser.convertHTMLtoString(htmlFromSquash);
		System.out.println(result);
		assertFalse(result.contains("4."));
		}
	
	@Test
	public void escapeUtf8() {
		String toEscape = "<p>é ç à. => 1.2 > 5 ! </p> <ol><li>point 1</li></ol>"; 
		String result = StringEscapeUtils.escapeHtml4(toEscape);		
		System.out.println("avec StringEscapeUtils: " + result);	
		String escapedOutput = HtmlUtils.htmlEscape(toEscape);
		System.out.println("avec Spring: " + escapedOutput);
		result = StringEscapeUtils.unescapeXml(StringEscapeUtils.escapeHtml4(toEscape));
		System.out.println("avec StringEscapeUtils en ignorant tags xml: " + result);
	}
}
