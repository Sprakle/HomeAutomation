package net.sprakle.homeAutomation.behaviour.triggers.time.parsers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import net.sprakle.homeAutomation.behaviour.triggers.time.TimeParser;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class CronParser implements TimeParser {


	private boolean triggered;

	public CronParser(Logger logger, Element element) {
		String cronPattern = element.getText();

		if (! SchedulingPattern.validate(cronPattern)) {
			logger.log("Invalid cron job pattern: " + cronPattern, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}

		// set triggered to true every time the cron job triggers
		Scheduler s = new Scheduler();
		s.schedule(cronPattern, new Runnable() {
			public void run() {
				triggered = true;
			}
		});

		s.start();
	}


	@Override
	public boolean isCurrent() {
		boolean hasBeenTriggered = triggered;

		triggered = false;

		return hasBeenTriggered;
	}
}