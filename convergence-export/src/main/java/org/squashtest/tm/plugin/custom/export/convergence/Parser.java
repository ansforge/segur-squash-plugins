/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.export.convergence;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class Parser.
 */
@Slf4j
public class Parser {

	private static final String MB_1_CLASS = "mb-1";
	private static final String P_ENTITY_TAG = "p";
	private static final String BR_ENTITY_TAG = "br";
	private static final int NBSP_CODE = 160;

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
		doc.select(BR_ENTITY_TAG).before("\\n");
		doc.select(P_ENTITY_TAG).before("\\n");
		doc.select(P_ENTITY_TAG).after("\\n");
		doc.select("ol").after("\\n\\n");
		doc.select("ul").after("\\n\\n");
		String str = doc.html().replaceAll("\\\\n", "\n");
		return Jsoup.clean(str, "", Safelist.none(), outputSettings).replaceAll("&apos;", "'")
				.replaceAll("&quot;", "\"").replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&amp;", "&");
	}

	public static String sanitize(String html) {
		String tmp = "";
		if (html == null || html.isEmpty()) {
			return tmp;
		}
		Document.OutputSettings outputSettings = new Document.OutputSettings();
		outputSettings.prettyPrint(true);
		outputSettings.syntax(Document.OutputSettings.Syntax.xml); 
		//outputSettings.escapeMode(Entities.EscapeMode.xhtml);
		outputSettings.charset("UTF-8"); 
		Document document = Jsoup.parseBodyFragment(html);
		System.out.println(document.body().html());
		document.outputSettings(outputSettings);
		List<Element> elementsToremove = new ArrayList<>();
		Elements elements = document.body().getAllElements();
		for (Element element : elements) {
			if (element.tagName().equalsIgnoreCase(P_ENTITY_TAG)) {
				// Ajouter la classe {class="mb -1"} au niveau de toutes les balises <p>
				if (!element.hasClass(MB_1_CLASS)) {
					element.addClass(MB_1_CLASS);
				}
				if (isOnlyWhitespaces(element.text()) || !element.hasText()) {
					// Supprimer les paragraphes vides, exemple : <p class="mb-1">&nbsp;</p>
					if (!elementsToremove.contains(element)) {
						elementsToremove.add(element);
					}
				}
				element.html(trim(element.html()));
				// Ajouter la classe {class="mb -1"} au niveau de toutes les balises <ul> et <ol>
			} else if (element.tagName().equalsIgnoreCase("ol") || element.tagName().equalsIgnoreCase("ul")) {
				if (!element.hasClass(MB_1_CLASS)) {
					element.addClass(MB_1_CLASS);
				}
				// Supprimer les balises <span></span>,
			} else if (element.tagName().equalsIgnoreCase("span")) {
				if (!element.hasText()) {
					// S'il est vide, on le supprime
					if (!elementsToremove.contains(element)) {
						elementsToremove.add(element);

					}
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
		System.out.println(document.body().html());
		return document.body().html()
				//On supprime les balises span sans toucher au texte
				.replaceAll("<span[^>]*>", "")
				.replaceAll("</span>", "")
				.replaceAll("&NewLine;", "")
				.replaceAll("&Tab;", "")
				.replaceAll("<br\\s?/>\\s*</p>", "</p>")
				.replaceAll("<br\\s?/>\\s*<br\\s?/>", "<br/>")
				.replaceAll("<br\\s?/>\\s*<p(.*)>", "<p$1>")
				.replaceAll("<br\\s?/>\\s*</p>", "</p>")
				.replaceAll("<p(.*)>\\s*<br\\s?/>", "<p$1>")
				.replaceAll("</p>\\s*<br\\s?/>", "</p>")
				.replaceAll("&nbsp;</p>", "</p>")
				.replaceAll("<p class=\"mb-1\"></p>", "")
				.replaceAll("é", "&eacute;")
				.replaceAll("è", "&egrave;")
				.replaceAll("à", "&agrave;")
				.replaceAll("ù", "&ugrave;")
				.replaceAll("ô", "&ocirc;")
				.replaceAll("ê", "&ecirc;")
				.replaceAll("â", "&acirc;")
				.replaceAll("î", "&icirc;")
				.replaceAll("ç", "&ccedil;")
				.replaceAll("°", "&deg;");

				//.replaceAll("<br\\s?/>\\s*<br\\s?/>", ;"<br/>");
				
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
			if (NBSP_CODE != (codePoint)) {
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

		while ((st < len) && (val[st] == NBSP_CODE)) {
			st++;
		}
		while ((st < len) && (val[len - 1] == NBSP_CODE)) {
			len--;
		}
		return ((st > 0) || (len < val.length)) ? string.substring(st, len) : string;
	}
}
