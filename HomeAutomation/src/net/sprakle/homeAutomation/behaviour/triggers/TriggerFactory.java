package net.sprakle.homeAutomation.behaviour.triggers;

import net.sprakle.homeAutomation.behaviour.XMLKeys;
import net.sprakle.homeAutomation.behaviour.triggers.objectDatabaseRead.ObjectDatabaseRead;
import net.sprakle.homeAutomation.behaviour.triggers.time.Time;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class TriggerFactory {

	public static Trigger makeTrigger(Logger logger, Element element, ObjectDatabase od) {
		String path = element.getUniquePath();

		String triggerType = element.attributeValue(XMLKeys.TYPE);
		if (triggerType == null)
			logger.log("Unable to read trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);

		// add new triggers here
		switch (triggerType) {
			case "object_database_read":
				return new ObjectDatabaseRead(logger, element, od);

			case "time":
				return new Time(logger, element);
		}

		return null;
	}
}
