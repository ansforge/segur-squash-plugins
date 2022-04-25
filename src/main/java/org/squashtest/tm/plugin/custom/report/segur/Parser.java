package org.squashtest.tm.plugin.custom.report.segur;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import org.squashtest.tm.plugin.custom.report.segur.Constantes;

public class Parser {

	private Parser() {};
	
	public static String CR_CURRENT_SYSTEM = System.getProperty("line.separator");
	
	// IN: html fragment between <ul> tag 
	public static String convertHtmlBulletedListToString(String ulHTMlString) {
		Document doc = Jsoup.parseBodyFragment(ulHTMlString);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		Elements lis = doc.select("li");
		List<String> liste = new ArrayList<String>();
		lis.stream().forEach(li -> liste.add(Constantes.PREFIX_ELEMENT_LSITE_A_PUCES + li.text() + Constantes.CRLF));		
		return liste.stream().collect(Collectors.joining(""));		
	}
	
	public static String convertHtmlOrderedListToString(String ulHTMlString) {
		int  prefix = 1;
		String pt = ". ";
		
		Document doc = Jsoup.parseBodyFragment(ulHTMlString);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		Elements lis = doc.select("li");
		List<String> liste = new ArrayList<String>();
		List<String> listeResult = new ArrayList<String>();
		lis.stream().forEach(li -> liste.add(li.text() + Constantes.CRLF));
		for (String ligne : liste) {
			listeResult.add(prefix + pt + ligne) ;
			prefix++;
		}
		
		return listeResult.stream().collect(Collectors.joining(""));	
	}

	public static String parsedDescription(String description) {
		
		return null;
	}
	
}
