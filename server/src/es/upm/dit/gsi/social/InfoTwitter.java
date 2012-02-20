package es.upm.dit.gsi.social;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class InfoTwitter {
	
	public static int getPopularityTwitter (String title) {
		InfoTwitter lector = new InfoTwitter();
		lector.lee("http://search.twitter.com/search.atom?rpp=100&q=" +  title);
		return ParseHandler.getCount();
	}
	
	public static void main(String[] args) {
		System.out.println(getPopularityTwitter("Benedicto XVI confirma que viajará en el mes de marzo a México y Cuba"));
	}

	public void lee(String url) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(url, new ParseHandler());
		} catch (ParserConfigurationException e) {
				System.err.println("error de  parseo");
		} catch (SAXException e2) {
				System.err.println("error de  sax : " + e2.getStackTrace());
		} catch (IOException e3) {
				System.err.println("error de  io : " + e3.getMessage());
		}
	}
}
