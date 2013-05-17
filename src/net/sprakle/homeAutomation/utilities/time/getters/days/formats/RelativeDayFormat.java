package net.sprakle.homeAutomation.utilities.time.getters.days.formats;

import java.util.Calendar;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.DateParser;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

public class RelativeDayFormat implements TimeFormat {

	private final Logger logger;
	private final PhraseOutline outline;

	public RelativeDayFormat(Logger logger) {
		this.logger = logger;

		outline = new PhraseOutline("relative day day format");
		outline.addMandatoryTag(new Tag(TagType.RELATIVE_DAY, null));
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag dayTag = phrase.getTag(new Tag(TagType.RELATIVE_DAY, null));
		String dayString = dayTag.getValue();

		if (!dayString.matches("-?\\d*")) {
			logger.log("Invalid {DAY} value in taglist. Value must be an integer", LogSource.ERROR, LogSource.TIME, 1);
			return -1;
		}

		int day = Integer.parseInt(dayString);
		int currentDay = DateParser.getCurrent(Calendar.DAY_OF_WEEK);

		System.out.println("Day: " + day);
		System.out.println("Current: " + currentDay);
		return currentDay + day;
	}

}
