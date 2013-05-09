package net.sprakle.homeAutomation.behaviour.triggers.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sprakle.homeAutomation.behaviour.triggers.Trigger;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.DateParser;

import org.dom4j.Element;

/**
 * Triggers once the current system time has surpassed the given time
 * 
 * @author ben
 * 
 */
public class Time implements Trigger {

	private Logger logger;

	private Date date;

	public Time(Logger logger, Element element) {
		this.logger = logger;

		Tagger tagger = new Tagger(logger);

		String path = element.getUniquePath();

		String parseMode = element.elementText("parse_mode");
		String rawDate = element.elementText("time");

		if (parseMode == null && rawDate == null) {
			logger.log("Missing mandatory element in Time Trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}

		switch (parseMode) {
			case "standard":
				date = parseStandard(rawDate);
				break;

			case "natural_language":
				String boolString = element.elementText("must_be_in_future");
				boolean mustBeInFuture = Boolean.parseBoolean(boolString);
				date = parseNLA(rawDate, tagger, mustBeInFuture);
				break;

			case "milliseconds":
				date = parseMillis(rawDate);
				break;

			default:
				logger.log("Invalid parse mode in time trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
				date = null;
				return;
		}
	}

	@Override
	public boolean check() {
		Date current = new Date();
		return current.after(date);
	}

	private Date parseStandard(String rawDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = null;

		try {
			date = formatter.parse(rawDate);
		} catch (ParseException e) {
			logger.log("Invalid formatting of time trigger time: " + rawDate, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}

		System.out.println(date);
		return date;
	}

	private Date parseNLA(String rawDate, Tagger tagger, boolean mustBeInFuture) {
		Phrase phrase = new Phrase(logger, tagger, rawDate);

		Date date = DateParser.parseDate(logger, phrase, mustBeInFuture);

		if (date == null)
			logger.log("Unable to NLA parse date in time trigger: " + rawDate, LogSource.ERROR, LogSource.BEHAVIOUR, 1);

		return date;
	}

	private Date parseMillis(String rawDate) {
		if (!rawDate.matches("\\d+")) {
			logger.log("Non long value in time trigger: " + rawDate, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return null;
		}

		long dateLong = Long.parseLong(rawDate);
		Date date = new Date(dateLong);
		return date;
	}
}
