package net.sprakle.homeAutomation.behaviour.actions;

import net.sprakle.homeAutomation.behaviour.actions.emulateUserInput.EmulateUserInput;
import net.sprakle.homeAutomation.behaviour.actions.mediaCentreCommand.MediaCentreCommand;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class ActionFactory {

	public static Action makeAction(Logger logger, Element e, ExternalSoftware exs) {
		String path = e.getUniquePath();

		String actionString = e.attributeValue("type");
		if (actionString == null)
			logger.log("Unable to read action: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);

		ActionDependencies td = new ActionDependencies(logger, e, exs);

		for (Actions t : Actions.values())
			if (t.getElementString().equals(actionString))
				return t.getAction(td);

		return null;
	}

	/*
	 * Add new Actions here
	 */
	private static enum Actions {
		MEDIA_CENTRE_COMMAND {

			@Override
			public String getElementString() {
				return "media_centre_command";
			}

			@Override
			public Action getAction(ActionDependencies td) {
				return new MediaCentreCommand(td.logger, td.element, td.exs);
			}

		},

		EMULATE_USER_INPUT {

			@Override
			public String getElementString() {
				return "emulate_user_input";
			}

			@Override
			public Action getAction(ActionDependencies td) {
				return new EmulateUserInput(td.element);
			}
		};

		public abstract String getElementString();
		public abstract Action getAction(ActionDependencies td);
	}

	/*
	 * Add new action dependencies here
	 */
	private static class ActionDependencies {
		public Logger logger;
		public Element element;
		public ExternalSoftware exs;

		public ActionDependencies(Logger logger, Element element, ExternalSoftware exs) {
			this.logger = logger;
			this.element = element;
			this.exs = exs;
		}
	}
}