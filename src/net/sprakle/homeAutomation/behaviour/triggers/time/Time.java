package net.sprakle.homeAutomation.behaviour.triggers.time;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sprakle.homeAutomation.behaviour.triggers.Trigger;
import net.sprakle.homeAutomation.behaviour.triggers.time.parsers.CronParser;
import net.sprakle.homeAutomation.behaviour.triggers.time.parsers.MillisParser;
import net.sprakle.homeAutomation.behaviour.triggers.time.parsers.NLAParser;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

/**
 * Triggers once the current system time has surpassed the given time
 * 
 * @author ben
 * 
 */
public class Time implements Trigger {

	private final Logger logger;

	private final TimeParser parser;
	private List<Restriction> restrictions;

	public Time(Logger logger, Element element) {

		this.logger = logger;
		parser = selectParser(element);

		if (parser == null) {
			logger.log("No time parsers available for given parse mode", LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}

		restrictions = makeRestrictions(element);
	}

	@Override
	public boolean check() {
		boolean parserCheck = parser.isCurrent();
		boolean restrictionCheck = checkRestrictions();

		return parserCheck && restrictionCheck;
	}

	// all restrictions must pass
	private boolean checkRestrictions() {
		Date date = new Date();
		for (Restriction restriction : restrictions) {
			if (!restriction.passes(date))
				return false;
		}

		return true;
	}

	private TimeParser selectParser(Element element) {
		Tagger tagger = new Tagger(logger);
		String path = element.getUniquePath();

		String parseMode = element.elementText("parse_mode");
		String rawDate = element.elementText("time");

		if (parseMode == null || rawDate == null) {
			logger.log("Missing mandatory element in Time Trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return null;
		}

		Element timeElement = element.element("time");
		if (timeElement == null) {
			logger.log("Time element required on Time Trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return null;
		}

		// add new parsers here
		switch(parseMode) {
			case "cron":
				return new CronParser(logger, timeElement);

			case "milliseconds":
				return new MillisParser(logger, timeElement);

			case "natural_language":
				return new NLAParser(logger, tagger, timeElement);

			default:
				logger.log("No time parser able to parse '" + parseMode + "'", LogSource.ERROR, LogSource.BEHAVIOUR, 1);
				return null;
		}
	}

	/**
	 * Not yet implemented
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Restriction> makeRestrictions(Element element) {
		List<Restriction> restrictions = new ArrayList<>();

		// iterate through child elements of root with element name "foo"
		for ( Iterator i = element.elementIterator("restriction"); i.hasNext(); ) {
			Element rElement = (Element) i.next();

			Restriction restriction = new Restriction(logger, rElement);
			restrictions.add(restriction);
		}

		return restrictions;
	}
}