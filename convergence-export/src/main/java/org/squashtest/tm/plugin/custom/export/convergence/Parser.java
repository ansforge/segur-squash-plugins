/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class Parser.
 */
@Slf4j
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

		String tmp = "";
		if (html == null || html.isEmpty()) {
			return tmp;
		} // On commence par supprimer tous les retours chariot en trop.
		String sanitizedHtml = html.replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\t", "");
		Document doc = Jsoup.parse(sanitizedHtml);
		Document.OutputSettings outputSettings = new Document.OutputSettings();
		outputSettings.escapeMode(EscapeMode.xhtml);
		outputSettings.prettyPrint(false);
		doc.outputSettings(outputSettings); // traitement des listes ordonnées
		Elements ol = doc.select("ol");
		for (Element orderedLists : ol) {
			Elements items = orderedLists.children();
			int number; // Traitement des listes qui ne commencent pas à 1
			if (orderedLists.hasAttr("start")) {
				number = Integer.parseInt(orderedLists.attr("start"));
			} else {
				number = 1;
			}
			for (Element item : items) {
				item.before("\\n" + number + ".");
				number++;
			}
		} // traitement des listes simples
		Elements ul = doc.select("ul");
		for (Element orderedLists : ul) {
			Elements items = orderedLists.children();
			for (Element item : items) {
				item.before("\\n" + Constantes.PREFIX_ELEMENT_LISTE_A_PUCES);
			}
		} // traitement des paragraphes et retours à la ligne
		doc.select("br").before("\\n");
		doc.select("p").before("\\n");
		doc.select("p").after("\\n");
		doc.select("ol").after("\\n\\n");
		doc.select("ul").after("\\n\\n");
		String str = doc.html().replaceAll("\\\\n", "\n");
		return Jsoup.clean(str, "", Whitelist.none(), outputSettings).replaceAll("&apos;", "'")
				.replaceAll("&quot;", "\"").replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&amp;", "&");
	}

	public static String sanitize(String html) {
		String tmp = "";
		if (html == null || html.isEmpty()) {
			return tmp;
		}

		Document document = Jsoup.parseBodyFragment(html);
		Document.OutputSettings outputSettings = new Document.OutputSettings();
		outputSettings.prettyPrint(false);
		outputSettings.escapeMode(EscapeMode.extended);
		document.outputSettings(outputSettings);
		List<Element> elementsToremove = new ArrayList<>();
		Elements elements = document.body().getAllElements();
		for (Element element : elements) {
			if (element.tagName().equalsIgnoreCase("br")) {
				Element firstSiblingElement = element.firstElementSibling();
				if (firstSiblingElement != null && firstSiblingElement.tagName().equalsIgnoreCase("br")) {
					// Si deux BR de suite on supprime le BR suivant
					if (!elementsToremove.contains(firstSiblingElement)) {
						elementsToremove.add(firstSiblingElement);
					}
				}
				if (element.parent().tagName().equalsIgnoreCase("p")) {
					if (!elementsToremove.contains(element)) {
						elementsToremove.add(element);
					}
				}
			} else if (element.tagName().equalsIgnoreCase("p")) {
				Element nextSiblingElement = element.nextElementSibling();
				if (nextSiblingElement != null && nextSiblingElement.tagName().equalsIgnoreCase("br")) {
					// Si  BR à la suite on supprime le BR
					if (!elementsToremove.contains(nextSiblingElement)) {
						elementsToremove.add(nextSiblingElement);
					}
				}
				// Ajouter la classe {class="mb -1"} au niveau de toutes les balises <p>
				if (!element.hasClass("mb-1")) {
					element.addClass("mb-1");
				}
				if (isOnlyWhitespaces(element.text())) {
					// Supprimer les paragraphes vides, exemple : <p class="mb-1">&nbsp;</p>
					if (!elementsToremove.contains(element)) {
						elementsToremove.add(element);
					}
				}
				element.text(trim(element.text()));
				// Ajouter la classe {class="mb -1"} au niveau de toutes les balises <ul> et
				// <ol>
			} else if (element.tagName().equalsIgnoreCase("ol") || element.tagName().equalsIgnoreCase("ul")) {
				if (!element.hasClass("mb-1")) {
					element.addClass("mb-1");
				}
				// Supprimer les balises <span></span>,
			} else if (element.tagName().equalsIgnoreCase("span")) {
				if (!elementsToremove.contains(element)) {
					elementsToremove.add(element);
				}
			}
		}
		for (Element element : elementsToremove) {
			try {
				element.remove();
			} catch (IndexOutOfBoundsException e) {
				log.info(element.tagName() + " déjà supprimé");
			}
		}
		return document.body().html();
	}

	public static boolean isOnlyWhitespaces(String str) {
		// Check whether the string is null or empty
		if (str == null || str.isEmpty()) {
			return false;
		}
		// The for loop iterate through each character of the string
		for (int i = 0; i < str.length(); i++) {
			int codePoint = Character.codePointAt(str.toCharArray(), i);
			// Check whether the character does not satisfy for NO-BREAK SPACE
			if (160 != (codePoint)) {
				return false;
			}
		}
		// Return true if the character satisfies for whitespace
		return true;
	}
	
    public static String trim(String string) {
    	char[] val = string.toCharArray();
        int len = val.length;
        int st = 0;

        while ((st < len) && (val[st] == 160)) {
            st++;
        }
        while ((st < len) && (val[len - 1] == 160)) {
            len--;
        }
        return ((st > 0) || (len < val.length)) ? string.substring(st, len) : string;
    }
}
