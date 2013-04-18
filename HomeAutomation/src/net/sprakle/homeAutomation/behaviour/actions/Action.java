package net.sprakle.homeAutomation.behaviour.actions;

import org.dom4j.Element;

public abstract class Action {

	protected Element element;

	protected Action(Element element) {
		this.element = element;
	}

	public abstract void execute();
}
