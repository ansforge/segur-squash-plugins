/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;


/**
 * The Class Parser.
 */
public class Parser {

	private Parser() {
	};

	/**
	 * Convert html bulleted list to string.
	 *
	 * @param ulHTMlString the ul HT ml string
	 * @return the string
	 */
	// IN: html fragment between <ul> tag
	public static String convertHtmlBulletedListToString(String ulHTMlString) {
		Document doc = Jsoup.parseBodyFragment(ulHTMlString);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		Elements lis = doc.select("li");
		List<String> liste = new ArrayList<String>();
		lis.stream().forEach(li -> liste
				.add(Constantes.PREFIX_ELEMENT_LSITE_A_PUCES + li.text() + Constantes.CRLF + Constantes.CRLF));
		return liste.stream().collect(Collectors.joining(""));
	}

	/**
	 * Convert html ordered list to string.
	 *
	 * @param ulHTMlString the ul HT ml string
	 * @return the string
	 */
	// IN: html fragment between <ol> tag
	public static String convertHtmlOrderedListToString(String ulHTMlString) {
		int prefix = 1;
		String pt = ". ";
		Document doc = Jsoup.parseBodyFragment(ulHTMlString);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		Elements lis = doc.select("li");
		List<String> liste = new ArrayList<String>();
		List<String> listeResult = new ArrayList<String>();
		lis.stream().forEach(li -> liste.add(li.text() + Constantes.CRLF + Constantes.CRLF));
		for (String ligne : liste) {
			listeResult.add(Constantes.PREFIX_ELEMENT_LSITE_A_PUCES + prefix + pt + ligne);
			prefix++;
		}
		return listeResult.stream().collect(Collectors.joining(""));
	}

	/**
	 * Convert HTM lto string.
	 *
	 * @param html the html
	 * @return the string
	 */
	public static String convertHTMLtoString(String html) {
		String tmp = "";
		if (html == null || html.isEmpty()) {
			return tmp;
		}
		;
		Document doc = Jsoup.parseBodyFragment(html);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		StringBuilder out = new StringBuilder();

		for (Node node : doc.body().childNodes()) {
			tmp = node.toString();
			tmp = tmp.replace("<br />", Constantes.CRLF);
			switch (node.nodeName()) {
			case "p":
				out.append(Parser.htmlParagrapheToText(tmp));
				break;
			case "ul":
				out.append(Parser.convertHtmlBulletedListToString(tmp));
				break;
			case "ol":
				out.append(Parser.convertHtmlOrderedListToString(tmp));
				break;
			default:
				// si on ne sait pas => ne rien faire ...
				out.append(tmp);
				break;
			}
		}
		return out.toString().replaceAll("&apos;", "'");
		
	}

	/**
	 * Html paragraphe to text.
	 *
	 * @param html the html
	 * @return the string
	 */
	public static String htmlParagrapheToText(String html) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		Jsoup.parse(html).body().select("p").stream().map(Element::text).forEach(pw::println);
		return sw.toString();
	}

}
