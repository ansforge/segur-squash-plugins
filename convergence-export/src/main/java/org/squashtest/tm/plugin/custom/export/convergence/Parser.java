/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/**
 * The Class Parser.
 */
public class Parser {

	private Parser() {
	};
	/**
	 * Convert HTML to string.
	 *
	 * @param html the html
	 * @return the string
	 */
	public static String convertHTMLtoString(String html) {
/*
		String tmp = "";
		if (html == null || html.isEmpty()) {
			return tmp;
		}
		//On commence par supprimer tous les retours chariot en trop.
		String sanitizedHtml = html.replaceAll("\\r", "")
		.replaceAll("\\n", "")
		.replaceAll("\\t", "");
		Document doc = Jsoup.parse(sanitizedHtml);
		Document.OutputSettings outputSettings = new Document.OutputSettings();
		outputSettings.escapeMode(EscapeMode.xhtml);
		outputSettings.prettyPrint(false);
		doc.outputSettings(outputSettings);
		// traitement des listes ordonnées
		Elements ol = doc.select("ol");
		for (Element orderedLists : ol) {
			Elements items = orderedLists.children();
			int number;
			// Traitement des listes qui ne commencent pas à 1
			if(orderedLists.hasAttr("start")) {
				number = Integer.parseInt(orderedLists.attr("start"));
			}else {
				number = 1;
			}
			for (Element item : items) {
				item.before("\\n" + number + ".");
				number++;
			}
		}
		// traitement des listes simples
		Elements ul = doc.select("ul");
		for (Element orderedLists : ul) {
			Elements items = orderedLists.children();
			for (Element item : items) {
				item.before("\\n" + Constantes.PREFIX_ELEMENT_LISTE_A_PUCES);
			}
		}
		// traitement des paragraphes et retours à la ligne
		doc.select("br").before("\\n");
		doc.select("p").before("\\n");
		doc.select("p").after("\\n");
		doc.select("ol").after("\\n\\n");
		doc.select("ul").after("\\n\\n");
		String str = doc.html().replaceAll("\\\\n", "\n");
		return Jsoup.clean(str, "", Whitelist.none(), outputSettings)
				.replaceAll("&apos;", "'")
				.replaceAll("&quot;", "\"")
				.replaceAll("&gt;", ">")
				.replaceAll("&lt;", "<")
				.replaceAll("&amp;", "&");
				*/
		return html;
	}

}
