package net.sprakle.homeAutomation.behaviour.triggers.time.parsers;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sprakle.homeAutomation.behaviour.triggers.time.TimeParser;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class StandardParser implements TimeParser {

	private final Logger logger;

	// units used in SimpleDateFormat
	private final String[] unitFormats = { "yyyy", "MM", "dd", "HH", "mm", "ss" };

	private SimpleDateFormat formatter;

	// this is a relative time, and cannot be directly compared to the current date. This is because
	// the SimpleDateFormatter is not always given all units, and will then resort to defaults
	private Date triggerDate;

	private Date prevDate;

	public StandardParser(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void create(Element element) {
		String rawDate = element.getText();

		// get biggest defined unit by counting tacks (-)
		String triggerStrings[] = rawDate.split("-");

		// create simple format by adding from the units[] list
		String pattern = "";
		for (int i = triggerStrings.length; i > 0; i--) {
			pattern += unitFormats[unitFormats.length - i] + "-";
		}

		// remove extra tack from end
		pattern = pattern.substring(0, pattern.length() - 1);

		formatter = new SimpleDateFormat(pattern);
		triggerDate = parse(formatter, rawDate);
		if (triggerDate == null)
			return;

		String prevDateString = formatter.format(new Date(new Date().getTime() + (long) 1.8e6));
		prevDate = parse(formatter, prevDateString);
	}

	@Override
	public boolean isCurrent() {

		String currentDateString = formatter.format(new Date(new Date().getTime() + (long) 1.8e6));
		Date currentDate = parse(formatter, currentDateString);

		System.out.println("Trigger Before: " + triggerDate + "\nprevious: " + prevDate + "\ncurrent: " + currentDate);

		// FIXME: cannot understand times with a unit close to 0

		System.out.println("Trigger After: " + triggerDate + "\nprevious: " + prevDate + "\ncurrent: " + currentDate);

		if (triggerDate.after(prevDate) && triggerDate.before(currentDate)) {
			String prevDateString = formatter.format(new Date(new Date().getTime() + (long) 1.8e6));
			prevDate = parse(formatter, prevDateString);
			return true;
		} else {
			String prevDateString = formatter.format(new Date(new Date().getTime() + (long) 1.8e6));
			prevDate = parse(formatter, prevDateString);
			return false;
		}
	}

	@Override
	public boolean canParse(String parseMode) {
		return parseMode.equals("standard");
	}

	/**
	 * Not yet implemented
	 * 
	 * @param formatter
	 * @param toParse
	 * @return
	 */
	@SuppressWarnings({"SameReturnValue", "UnusedParameters"})
    private Date parse(SimpleDateFormat formatter, String toParse) {
		logger.log("parse is not yet implemented due to file coruption", LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		return null;
	}
}