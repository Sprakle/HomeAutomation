package net.sprakle.homeAutomation.behaviour.triggers.time;

import org.dom4j.Element;

public interface TimeParser {
	public boolean canParse(String parseMode);

	/**
	 * Used to assign an element to a parser after ensuring it can handle it
	 * 
	 * @param element
	 */
	public void create(Element element);

	/**
	 * If the current has matched the parser's set date since isCurrent() was
	 * called last
	 * 
	 * @return
	 */
	public boolean isCurrent();
}
