package net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand;

import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

// TODO: consider making this an interface
public abstract class Command {

	@SuppressWarnings("FieldCanBeLocal")
    private final Logger logger;
	protected final MediaCentre mc;

	protected final String path;

	protected Command(Logger logger, MediaCentre mc, Element element) {
		super();

		this.logger = logger;
		this.mc = mc;

		path = element.getUniquePath();
	}

	public abstract void execute();
}
