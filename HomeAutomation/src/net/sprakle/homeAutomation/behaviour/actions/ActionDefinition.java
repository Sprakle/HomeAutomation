package net.sprakle.homeAutomation.behaviour.actions;

import java.util.HashMap;
import java.util.Map.Entry;

import net.sprakle.homeAutomation.behaviour.XMLKeys;

import org.dom4j.Element;

/**
 * Used to define an action not based off an XML element
 * 
 * @author ben
 * 
 */
public class ActionDefinition {
	public String type;
	public HashMap<String, String> elements;

	public ActionDefinition(String type) {
		this.type = type;

		elements = new HashMap<String, String>();
	}

	public void copyDataToElement(Element element) {

		// header information
		element.addAttribute(XMLKeys.TYPE, type);

		// elements
		for (Entry<String, String> entry : elements.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();

			Element e = element.addElement(name);
			e.setText(value);
		}
	}
}
