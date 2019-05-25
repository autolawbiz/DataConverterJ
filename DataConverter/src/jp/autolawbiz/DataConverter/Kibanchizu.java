package jp.autolawbiz.DataConverter;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Kibanchizu {

	public static void ToGeoJSON(String inputPath, String outputPath) {

		SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(new File(inputPath), new Xmlfile());
        } catch (Exception e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
	}
}
