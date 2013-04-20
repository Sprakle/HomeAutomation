package net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand;

import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public abstract class Command {

	protected Logger logger;
	protected MediaCentre mc;

	protected String path;

	public Command(Logger logger, MediaCentre mc, Element element) {
		super();

		this.logger = logger;
		this.mc = mc;

		path = element.getUniquePath();
	}

	public abstract void execute();
}
