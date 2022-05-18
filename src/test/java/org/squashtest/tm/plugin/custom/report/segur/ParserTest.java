/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;


public class ParserTest {

	
	@Test
	public void investigationConvertEscapeCharacterTest() {

		String html = "<p>Le syst&egrave;me DOIT permettre de saisir et modifier les activit&eacute;s/interventions de l&#39;usager.</p>";
		String utf8Text = "Le système DOIT permettre de saisir et modifier les activités/interventions de l'usager.";
		// Document doc = Jsoup.parse(html);
		Document doc = Jsoup.parseBodyFragment(html);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		assertEquals(doc.text(), utf8Text);
	}

	@Test
	public void investigationConvertMoreComplexeHtmlTest() {
		// DSR_DUI_MS1 Profil Général\A_PHI_H/CIE coordination et planification des
		// activités. Exigence "A_PIL_H/CIE.01A"
		String html = "<p>Le syst&egrave;me DOIT permettre de saisir et modifier les activit&eacute;s/interventions de l&#39;usager.<br />\r\n"
				+ "<br />\r\n" + "R&egrave;gle 1<br />\r\n"
				+ "Le syst&egrave;me doit permettre au professionnel autoris&eacute; de planifier les activit&eacute;s/interventions collectives et individuelles de l&#39;usager associ&eacute;es &agrave; la vie de l&#39;&eacute;tablissement ou du service, tout au long du parcours avec par exemple les informations suivantes :<br />\r\n"
				+ "-Libell&eacute; de l&#39;activit&eacute;/intervention<br />\r\n"
				+ "-Ressources (param&eacute;trables),<br />\r\n" + "-Lieu,<br />\r\n" + "-Date,<br />\r\n"
				+ "-Temporalit&eacute; (dont fonction de r&eacute;p&eacute;tition),<br />\r\n"
				+ "-Type et sous type d&#39;activit&eacute;/intervention (atelier, groupe de parole, entretien, consultation, r&eacute;union, ...),<br />\r\n"
				+ "-Description/Commentaire,<br />\r\n"
				+ "-Acteurs concern&eacute;s (un ou plusieurs professionnels)<br />\r\n"
				+ "-Si besoin, documents li&eacute;s (compte-rendu, synth&egrave;se, &eacute;valuation)<br />\r\n"
				+ "&nbsp;</p>";

		System.out.println(html);

//		Document doc = Jsoup.parseBodyFragment(html);
//		OutputSettings outputsettings = doc.outputSettings().prettyPrint(false);
//		outputsettings = doc.outputSettings().escapeMode(EscapeMode.xhtml);
//		doc.outputSettings(outputsettings);

		Document doc = Jsoup.parseBodyFragment(html);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);

		// Elements elts = doc.getAllElements();
		Elements elts = doc.getElementsByTag("br");
		for (Element element : elts) {
			element.after(Constantes.CRLF);
		}

		System.out.println(doc);
//		Elements paragraphes = doc.select("p");
//		
//		for (Element element : paragraphes) {
//			element.appendText(Utile.CR);
//		}

//		paragraphes.forEach(paragraphe -> paragraphe.appendText(Utile.CR));
//		paragraphes = doc.select("br");
//		paragraphes.forEach(paragraphe -> paragraphe.appendText(Utile.CR));
//		paragraphes = doc.select("p");
//		paragraphes.forEach(paragraphe -> paragraphe.appendText(Utile.CR));		
//		
		//
		// doc.outputSettings().prettyPrint(false);

		// String strWithNewLines = Jsoup.clean(strHTML, "", Whitelist.none(),
		// outputSettings);

//		String formattedText = doc.text();
//		formattedText.replaceAll("\r\n",Utile.CR);
//		formattedText.replaceAll("\n",Utile.CR);

		System.out.println("********* \n resultat: \n" + doc.text());

		OutputSettings outputSettings = new Document.OutputSettings();
		outputSettings.prettyPrint(false);
		outputSettings.escapeMode(EscapeMode.xhtml);
		System.out.println(
				"********* \n resultat 2 : \n" + Jsoup.clean(doc.toString(), "", Whitelist.none(), outputSettings));

		doc.outputSettings(outputSettings);
		System.out.println("********* \n resultat 3 : \n" + doc.text());

//System.out.println(formattedText);    

	}

	@Test
		public void convertHtmlContenantUlList() {
			
			// v.reference='A_PIL_H/CIE.06B' liste
			String html = "<p>Le syst&egrave;me DOIT permettre de saisir, modifier et verouiller les transmissions internes pour la structure.<br /> <br /> R&egrave;gle 2<br /> Le syst&egrave;me doit permettre la saisie de ces transmission avec &agrave; minima, les informations suivantes :<br /> </p>\r\n"
					+ "<ul> \r\n" + " <li>Date et heure de la transmission</li> \r\n"
					+ " <li>Date et heure de l'&eacute;v&eacute;n&eacute;ment</li> \r\n" + " <li>Auteur</li> \r\n"
					+ " <li>Th&eacute;matique</li> \r\n" + "</ul>\r\n" + "<p></p>";

			String expected = "Le système DOIT permettre de saisir, modifier et verouiller les transmissions internes pour la structure. Règle 2 Le système doit permettre la saisie de ces transmission avec à minima, les informations suivantes :\r\n"
					+ " 	 - Date et heure de la transmission\r\n"
					+ "	 - Date et heure de l'événément\r\n"
					+ "	 - Auteur\r\n"
					+ "	 - Thématique\r\n"
					+ " \r\n";

			String out = Parser.convertHTMLtoString(html);
			assertEquals(out, expected);
	}


	@Test
	public void convertSimpleULListToJavaTest() {
	String html ="<ul> \r\n"
			+ "	 <li>Date et heure de la transmission</li> \r\n"
			+ "	 <li>Date et heure de l'&eacute;v&eacute;n&eacute;ment</li> \r\n"
			+ "	 <li>Auteur</li> \r\n"
			+ "	 <li>Th&eacute;matique</li> \r\n"
			+ "	</ul>";
	
	String expectedResult ="	 - Date et heure de la transmission\r\n"
			+ "	 - Date et heure de l'événément\r\n"
			+ "	 - Auteur\r\n"
			+ "	 - Thématique\r\n";
	
	String result = Parser.convertHtmlBulletedListToString(html);
	System.out.println(result);
	assertEquals(result, expectedResult);
	}
	
	@Test
	public void convertSimpleHtmlOrderedListToString() {
		String html = "<ol type=\"I\">  \r\n"
				+ "	 <li>ligne1</li>  \r\n"
				+ "	 <li>ligne2 ffffff</li>  \r\n"
				+ "	 <li>ligne 3 </li>  \r\n"
				+ "	 <li>dernière ligne</li>  \r\n"
				+ "	</ol>";
		
		String expectedResult ="\t1. ligne1\r\n"
				+ "\t2. ligne2 ffffff\r\n"
				+ "\t3. ligne 3\r\n"
				+ "\t4. dernière ligne\r\n";
		
		String result = Parser.convertHtmlOrderedListToString(html);
		System.out.println(result);
		assertEquals(result, expectedResult);
	}
}
