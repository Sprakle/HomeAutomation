package net.sprakle.homeAutomation.main;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Config {

    public static String getString(String xPath) {
		String original = getOriginal(xPath);
		return original;
	}

	public static int getInt(String xPath) {
		String original = getOriginal(xPath);

		int i = -1;
		try {
			i = Integer.parseInt(original);
		} catch (NumberFormatException e) {
			System.err.println("Inable to parse integer from given XML path");
			System.exit(1);
		}
		return i;
	}

	public static float getFloat(String xPath) {
		String original = getOriginal(xPath);

		float f = -1;
		try {
			f = Float.parseFloat(original);
		} catch (NumberFormatException e) {
			System.err.println("Inable to parse float from given XML path");
			System.exit(1);
		}
		return f;
	}

	public static boolean getBinary(String xPath) {
		String original = getOriginal(xPath);

		return original.equals("true");
	}

	private static String getOriginal(String xPath) {
		String result = null;

		SAXReader reader = new SAXReader();
		Document document = null;
		try {
            String configPath = "config/config.xml";
            document = reader.read(configPath);
		} catch (DocumentException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Node node = document.selectSingleNode(xPath);

		if (node == null) {
			System.err.println("Unable to find requested xPath in XML file: " + xPath);
			System.exit(1);
		}

		result = node.getStringValue();

		return result;
	}
}
