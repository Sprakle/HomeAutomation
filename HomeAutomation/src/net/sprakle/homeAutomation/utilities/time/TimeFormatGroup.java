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

	private Logger logger;

	private List<TimeFormat> formats;
	private int calendarUnit;

	public TimeFormatGroup(Logger logger, int calendarUnit, List<TimeFormat> formats) {
		this.logger = logger;
		this.calendarUnit = calendarUnit;

		this.formats = formats;
	}

	public TimeFormat selectFormat(Phrase phrase) {
		List<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		for (TimeFormat tf : formats) {
			PhraseOutline outline = tf.getOutline();
			outline.negateUnxepectedTagPenalty();
			outlines.add(outline);
		}

		PhraseOutline match = phrase.matchOutlines(logger, outlines);

		for (TimeFormat tf : formats) {
			if (tf.getOutline() == match)
				return tf;
		}

		return null;
	}

	public int parseTime(Phrase phrase) {
		TimeFormat format = selectFormat(phrase);

		if (format == null)
			return DateParser.getCurrent(calendarUnit);
		else
			return format.getTime(phrase);
	}

}
