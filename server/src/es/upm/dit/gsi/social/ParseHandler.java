package es.upm.dit.gsi.social;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import es.upm.dit.gsi.logger.Logger;

public class ParseHandler extends DefaultHandler {

	private static int count = 0;
	private static final Logger LOGGER = Logger.getLogger("methods.Handler");
	
	@Override
	public void startDocument() throws SAXException {
		count = 0;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("entry")) {
			count++;
		}
	}

	public void endDocument() throws SAXException {
		LOGGER.info("Count Twitter: " + count);
	}
	
	public static int getCount() {
		return count;
	}

}