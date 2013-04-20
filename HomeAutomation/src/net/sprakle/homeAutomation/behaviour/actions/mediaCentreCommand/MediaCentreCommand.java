package net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand;

import net.sprakle.homeAutomation.behaviour.actions.Action;
import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands.ChangeVolume;
import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands.EnqueueTrack;
import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands.PlayRandomTrack;
import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands.PlayTrack;
import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands.Playback;
import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands.SetVolume;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class MediaCentreCommand extends Action {

	private Logger logger;
	private Command command;

	public MediaCentreCommand(Logger logger, Element element, ExternalSoftware exs) {
		super(element);

		this.logger = logger;

		MediaCentre mc = (MediaCentre) exs.getSoftware(SoftwareName.MEDIA_CENTRE);
		command = makeCommand(mc, element);
	}

	@Override
	public void execute() {
		command.execute();
	}

	private Command makeCommand(MediaCentre mc, Element e) {
		String path = e.getUniquePath();

		String commandString = e.elementText("command");
		if (commandString == null) {
			logger.log("Invalid media centre action: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return null;
		}

		switch (commandString) {
			case "play_track":
				return new PlayTrack(logger, mc, e);

			case "enqueue_track":
				return new EnqueueTrack(logger, mc, e);

			case "play_random_track":
				return new PlayRandomTrack(logger, mc, e);

			case "set_volume":
				return new SetVolume(logger, mc, e);

			case "change_volume":
				return new ChangeVolume(logger, mc, e);

			case "playback":
				return new Playback(logger, mc, e);

			default:
				logger.log("Invalid media centre action: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
				return null;
		}
	}
}