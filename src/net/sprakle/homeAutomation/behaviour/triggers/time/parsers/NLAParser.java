package net.sprakle.homeAutomation.behaviour.triggers.time.parsers;

import java.util.Date;

import net.sprakle.homeAutomation.behaviour.triggers.time.TimeParser;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.DateParser;

import org.dom4j.Element;

public class NLAParser implements TimeParser {

	private final Logger logger;

	private Date triggerDate;
	private String rawDate;

	// used to see if the current time has matched since isCurrent() was called last
	private Date prevDate;

	public NLAParser(Logger logger, Tagger tagger, Element element) {
		this.logger = logger;


		rawDate = element.getText();
		Phrase phrase = new Phrase(logger, tagger, rawDate);

		triggerDate = DateParser.parseDate(logger, phrase, false);

		prevDate = new Date();
	}

	@Override
	public boolean isCurrent() {

		// must be parsed every time if must be in future
		Date currentDate = new Date();

		if (triggerDate == null) {
			logger.log("Unable to NLA parse date in time trigger: " + rawDate, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return false;
		}

		if (triggerDate.after(prevDate) && triggerDate.before(currentDate)) {
			prevDate = new Date();
			return true;
		} else {
			prevDate = new Date();
			return false;
		}
	}
}
