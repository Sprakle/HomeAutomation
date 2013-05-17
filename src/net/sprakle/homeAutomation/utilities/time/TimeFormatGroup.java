package net.sprakle.homeAutomation.utilities.time;

import java.util.ArrayList;
import java.util.List;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.utilities.logger.Logger;

/**
 * A time group is a group of time formats that can parse a specific unit of
 * time, such as seconds, minutes, hours, etc
 * 
 * @author ben
 * 
 */
public class TimeFormatGroup {

	private final Logger logger;

	private final List<TimeFormat> formats;
	private final int calendarUnit;

	private final boolean overriding;

	/**
	 * Collects a list of TimeFormats for a specific unit of time, such as
	 * minutes. When parseTime() is called, returns the result of the best
	 * format for a phrase
	 * 
	 * @param logger
	 * @param calendarUnit
	 *            the unit in Java.util.Calendar this this group applies to
	 * @param formats
	 *            Formats of natural language that can be parsed
	 * @param overriding
	 *            If this unit overrides others (such as Calendar.DATE), it will
	 *            only be considered if the group is able to parse a phrase.
	 *            Otherwise the current time of this unit will be used if unable
	 *            to parse
	 */
	public TimeFormatGroup(Logger logger, int calendarUnit, List<TimeFormat> formats, boolean overriding) {
		this.logger = logger;
		this.calendarUnit = calendarUnit;

		this.formats = formats;

		this.overriding = overriding;
	}

	private TimeFormat selectFormat(Phrase phrase) {
		List<PhraseOutline> outlines = new ArrayList<>();

		for (TimeFormat tf : formats) {
			PhraseOutline outline = tf.getOutline();
			outline.negateUnxepectedTagPenalty();
			outlines.add(outline);
		}

		PhraseOutline match = phrase.matchOutlines(outlines);

		for (TimeFormat tf : formats) {
			if (tf.getOutline() == match)
				return tf;
		}

		return null;
	}

	public boolean canParse(Phrase phrase) {
		return selectFormat(phrase) != null;
	}

	public int parseTime(Phrase phrase) {
		TimeFormat format = selectFormat(phrase);
		return format.getTime(phrase);
	}

	public int getCurrent() {
		return DateParser.getCurrent(calendarUnit);
	}

	public int getCalendarUnit() {
		return calendarUnit;
	}

	public boolean isOverriding() {
		return overriding;
	}
}