package net.sprakle.homeAutomation.behaviour.triggers;

import net.sprakle.homeAutomation.behaviour.XMLKeys;
import net.sprakle.homeAutomation.behaviour.triggers.objectDatabaseRead.ObjectDatabaseRead;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class TriggerFactory {

	public static Trigger makeTrigger(Logger logger, Element e, ObjectDatabase od) {
		String path = e.getUniquePath();

		String triggerType = e.attributeValue(XMLKeys.TYPE);
		if (triggerType == null)
			logger.log("Unable to read trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);

		for (Triggers t : Triggers.values()) {
			if (t.getElementString().equals(triggerType)) {
				Trigger trigger = t.getTrigger(logger, e, od);
				return trigger;
			}
		}

		return null;
	}

	/*
	 * Add new Triggers here
	 */
	private static enum Triggers {
		OBJECT_DATABASE_READ {

			@Override
			public String getElementString() {
				return "object_database_read";
			}

			@Override
			public Trigger getTrigger(Logger logger, Element element, ObjectDatabase od) {
				return new ObjectDatabaseRead(logger, element, od);
			}

		};

		public abstract String getElementString();
		public abstract Trigger getTrigger(Logger logger, Element element, ObjectDatabase od);
	}
}
