package jp.autolawbiz.DataConverter;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class Xmlfile extends DefaultHandler {

	public void startDocument() {
        System.out.println("読み込み開始");
	}

	public void endDocument() {
        System.out.println("読み込み終了");
    }

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
        System.out.println("Node: " + qName + " 開始");
        if(qName.equals("info")) {
            System.out.println(attributes.getQName(0) + ": " + attributes.getValue(0));
        }
	}

	public void endElement(String uri, String localName, String qName) {
        System.out.println("Node: " + qName + " 終了");
	}

	public void characters(char[] ch, int start, int length) {
        System.out.println(new String(ch, start, length));
	}
}
