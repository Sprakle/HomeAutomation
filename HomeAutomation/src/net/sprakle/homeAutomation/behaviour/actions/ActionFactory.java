package net.sprakle.homeAutomation.behaviour.actions;

import net.sprakle.homeAutomation.behaviour.XMLKeys;
import net.sprakle.homeAutomation.behaviour.actions.emulateUserInput.EmulateUserInput;
import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.MediaCentreCommand;
import net.sprakle.homeAutomation.behaviour.actions.speak.Speak;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class ActionFactory {

	public static Action makeAction(Logger logger, Element element, ExternalSoftware exs) {
		String path = element.getUniquePath();

		String actionType = element.attributeValue(XMLKeys.TYPE);
		if (actionType == null)
			logger.log("Unable to read action: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);

		// add new actions here
		switch (actionType) {
			case "media_centre_command":
				return new MediaCentreCommand(logger, element, exs);

			case "emulate_user_input":
				return new EmulateUserInput(element);

			case "speak":
				return new Speak(logger, element, exs);
		}

		return null;
	}
}