package net.sprakle.homeAutomation.behaviour.triggers.time;

import org.dom4j.Element;

public interface TimeParser {

	/**
	 * If the current has matched the parser's set date since isCurrent() was
	 * called last
	 * 
	 * @return
	 */
	public boolean isCurrent();
}
