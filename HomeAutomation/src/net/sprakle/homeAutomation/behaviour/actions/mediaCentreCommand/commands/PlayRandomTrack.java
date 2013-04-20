package net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands;

import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.Command;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class PlayRandomTrack extends Command {

	private String artist;

	public PlayRandomTrack(Logger logger, MediaCentre mc, Element element) {
		super(logger, mc, element);

		// if artist is not null, a random track by the given artist will be played
		artist = element.elementText("artist");
	}

	@Override
	public void execute() {
		mc.playRandomTrack(artist);
	}
}
