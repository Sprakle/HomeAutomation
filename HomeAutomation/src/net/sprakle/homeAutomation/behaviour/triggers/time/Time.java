package net.sprakle.homeAutomation.behaviour.triggers.time;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sprakle.homeAutomation.behaviour.triggers.Trigger;
import net.sprakle.homeAutomation.behaviour.triggers.time.parsers.MillisParser;
import net.sprakle.homeAutomation.behaviour.triggers.time.parsers.NLAParser;
import net.sprakle.homeAutomation.behaviour.triggers.time.parsers.StandardParser;
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

	private Logger logger;

	private TimeParser parser;
	private List<Restriction> restrictions;

	public Time(Logger logger, Element element) {
		logger.log("Do not use the Time behaviour until it's critical bug has been fixed", LogSource.ERROR, LogSource.BEHAVIOUR, 1);

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
		System.out.println("Checking:");
		boolean parserCheck = parser.isCurrent();
		boolean restrictionCheck = checkRestrictions();

		System.out.println("  Parser: " + parserCheck);
		System.out.println("  Restrictions: " + restrictionCheck);

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

		List<TimeParser> parsers = makeTimeParsers(tagger, timeElement);
		for (TimeParser parser : parsers) {
			if (parser.canParse(parseMode)) {
				parser.create(timeElement);
				return parser;
			}
		}

		return null;
	}

	private List<TimeParser> makeTimeParsers(Tagger tagger, Element element) {
		List<TimeParser> parsers = new ArrayList<TimeParser>();

		// add new parsers here
		parsers.add(new StandardParser(logger));
		parsers.add(new MillisParser(logger));
		parsers.add(new NLAParser(logger, tagger));

		return parsers;
	}

	/**
	 * Not yet implemented
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Restriction> makeRestrictions(Element element) {
		List<Restriction> restrictions = new ArrayList<Restriction>();

		// iterate through child elements of root with element name "foo"
		for (Iterator<Element> i = element.elementIterator("restriction"); i.hasNext(); i.next()) {

		}

		logger.log("makeRestrictions() is not yet implemented due to file coruption", LogSource.ERROR, LogSource.BEHAVIOUR, 1);

		return restrictions;
	}
}