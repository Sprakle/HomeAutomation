package net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands;

import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.Command;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class SetVolume extends Command {

	private double set;

	public SetVolume(Logger logger, MediaCentre mc, Element element) {
		super(logger, mc, element);

		String setString = element.elementText("set");

		if (setString == null || !setString.matches("(0(\\.\\d*)?)|1")) {
			logger.log("Invalid volume in set_volume behaviour: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}

		set = Double.parseDouble(setString);
	}

	@Override
	public void execute() {
		mc.setVolume(set);
	}

}
