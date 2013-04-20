package net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.commands;

import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.Command;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class ChangeVolume extends Command {

	private double change;

	public ChangeVolume(Logger logger, MediaCentre mc, Element element) {
		super(logger, mc, element);

		String changeString = element.elementText("change");

		if (changeString == null || !changeString.matches("-?(0(\\.\\d*)?)|1")) {
			logger.log("Invalid volume change in change_volume behaviour: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}

		change = Double.parseDouble(changeString);
	}

	@Override
	public void execute() {
		mc.changeVolume(change);
	}

}
