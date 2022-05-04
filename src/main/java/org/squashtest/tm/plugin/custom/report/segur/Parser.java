package org.squashtest.tm.plugin.custom.report.segur;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

public class Parser {

	private Parser() {
	};

	// IN: html fragment between <ul> tag
	public static String convertHtmlBulletedListToString(String ulHTMlString) {
		Document doc = Jsoup.parseBodyFragment(ulHTMlString);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		Elements lis = doc.select("li");
		List<String> liste = new ArrayList<String>();
		lis.stream().forEach(li -> liste.add(Constantes.PREFIX_ELEMENT_LSITE_A_PUCES + li.text() + Constantes.CRLF));
		return liste.stream().collect(Collectors.joining(""));
	}

	 //IN: html fragment between <ol> tag
	public static String convertHtmlOrderedListToString(String ulHTMlString) {
		int prefix = 1;
		String pt = ". ";
		Document doc = Jsoup.parseBodyFragment(ulHTMlString);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		Elements lis = doc.select("li");
		List<String> liste = new ArrayList<String>();
		List<String> listeResult = new ArrayList<String>();
		lis.stream().forEach(li -> liste.add(li.text() + Constantes.CRLF));
		for (String ligne : liste) {
			listeResult.add("\t" + prefix + pt + ligne);
			prefix++;
		}
		return listeResult.stream().collect(Collectors.joining(""));
	}

	public static String convertHTMLtoString(String html) {
		Document doc = Jsoup.parseBodyFragment(html);
		List<String> htmlFragment = new LinkedList<String>();
		StringBuilder out = new StringBuilder();
		String tmp = "";
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
		return out.toString();
	}


	public static String htmlParagrapheToText(String html) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		Jsoup.parse(html).body().select("p").stream().map(Element::text).forEach(pw::println);
		return sw.toString();
	}

//	// A supprimer utiliser juste dans un test ...
//	public static List<String> todeleteParseHtml(String str) {
//		System.out.println("ICI parse du html");
//		org.jsoup.nodes.Document doc = Jsoup.parse(str);
//
//		final List<String> wordList = new ArrayList<String>();
//
//		doc.body().traverse(new NodeVisitor() {
//
//			@Override
//			public void head(Node arg0, int arg1) {
//				if (arg1 == 1) {
//					// String value = Jsoup.parse(arg0.outerHtml()).text();
//					String value = arg0.outerHtml();
//					if (!wordList.contains(value))
//						wordList.add(arg0.outerHtml());
//
//				}
//
//			}
//
//			@Override
//			public void tail(Node arg0, int arg1) {
//
//			}
//		});
//
//		System.out.println("ICI bnre elt de la liste: " + wordList.size());
//		for (String word : wordList) {
//			System.out.println(" -------------");
//			System.out.println(word);
//			System.out.println(" -------------");
//		}
//
//		return wordList;
//	}
}
