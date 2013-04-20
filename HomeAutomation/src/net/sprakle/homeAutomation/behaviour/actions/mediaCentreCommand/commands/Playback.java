package net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands;

import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.Command;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class Playback extends Command {

	PlaybackCommand pCommand;

	public Playback(Logger logger, MediaCentre mc, Element element) {
		super(logger, mc, element);

		String pCommandString = element.elementText("playback");

		if (pCommandString == null || !pCommandString.matches("(play)|(pause)|(next)|(back)")) {
			logger.log("Invalid playback command in action: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}

		pCommand = getPCommand(pCommandString);
	}
	@Override
	public void execute() {
		mc.playbackCommand(pCommand);
	}

	private PlaybackCommand getPCommand(String s) {
		switch (s) {
			case "play":
				return PlaybackCommand.PLAY;

			case "pause":
				return PlaybackCommand.PAUSE;

			case "next":
				return PlaybackCommand.NEXT;

			case "back":
				return PlaybackCommand.BACK;

			default:
				return null;
		}
	}
}
