package net.sprakle.homeAutomation.behaviour.triggers;

import net.sprakle.homeAutomation.behaviour.triggers.objectDatabaseRead.ObjectDatabaseRead;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class TriggerFactory {

	public static Trigger makeTrigger(Logger logger, Element e, ObjectDatabase od) {
		String path = e.getUniquePath();

		String triggerString = e.attributeValue("type");
		if (triggerString == null)
			logger.log("Unable to read trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);

		TriggerDependencies td = new TriggerDependencies(logger, e, od);

		for (Triggers t : Triggers.values())
			if (t.getElementString().equals(triggerString))
				return t.getTrigger(td);

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
			public Trigger getTrigger(TriggerDependencies td) {
				return new ObjectDatabaseRead(td.logger, td.element, td.objectDatabase);
			}

		};

		public abstract String getElementString();
		public abstract Trigger getTrigger(TriggerDependencies td);
	}

	/*
	 * Add new trigger dependencies here
	 */
	private static class TriggerDependencies {
		public Logger logger;
		public Element element;
		public ObjectDatabase objectDatabase;

		public TriggerDependencies(Logger logger, Element element, ObjectDatabase od) {
			super();
			this.logger = logger;
			this.element = element;
			this.objectDatabase = od;
		}
	}
}
