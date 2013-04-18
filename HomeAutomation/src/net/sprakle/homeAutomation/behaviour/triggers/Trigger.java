package net.sprakle.homeAutomation.behaviour.triggers;

import org.dom4j.Element;

public abstract class Trigger {

	protected Element element;

	protected Trigger(Element element) {
		this.element = element;
	}

	public abstract boolean check();
}
