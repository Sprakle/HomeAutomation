package net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands;

import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.Command;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class EnqueueTrack extends Command {

	private final String title;
	private final String artist;

	public EnqueueTrack(Logger logger, MediaCentre mc, Element element) {
		super(logger, mc, element);

		title = element.elementText("track");
		artist = element.elementText("artist");

		// it's ok for the artist to be null
		if (title == null) {
			logger.log("Invalid enqueue song action in behaviours: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}
	}

	@Override
	public void execute() {
		mc.enqueueTrack(title, artist);
	}
}
