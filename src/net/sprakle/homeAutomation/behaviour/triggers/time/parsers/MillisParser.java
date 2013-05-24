package net.sprakle.homeAutomation.behaviour.triggers.time.parsers;

import java.util.Date;

import net.sprakle.homeAutomation.behaviour.triggers.time.TimeParser;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class MillisParser implements TimeParser {

	private Date triggerDate;

	// used to see if the current time has matched since isCurrent() was called last
	private Date prevDate;

	public MillisParser(Logger logger, Element element) {

		String rawDate = element.getText();
		if (!rawDate.matches("\\d+")) {
			logger.log("Non long value in time trigger: " + rawDate, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			triggerDate = null;
			return;
		}

		long dateLong = Long.parseLong(rawDate);
		triggerDate = new Date(dateLong);

		prevDate = new Date();
	}

	@Override
	public boolean isCurrent() {
		Date currentDate = new Date();

		if (triggerDate.after(prevDate) && triggerDate.before(currentDate)) {
			prevDate = new Date();
			return true;
		} else {
			prevDate = new Date();
			return false;
		}
	}
}
