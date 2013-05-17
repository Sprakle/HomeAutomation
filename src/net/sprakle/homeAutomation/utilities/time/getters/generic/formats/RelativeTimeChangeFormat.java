package net.sprakle.homeAutomation.utilities.time.getters.generic.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.DateParser;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

/**
 * Parses time. Example:
 * 
 * RelativeNumberFormat(logger, "week", Calendar.WEEK_OF_MONTH)
 * 
 * "next week" returns {current weeks} + 1
 * 
 * @author ben
 * 
 */
public class RelativeTimeChangeFormat implements TimeFormat {

	private final Logger logger;

	private final PhraseOutline outline;
	private final String unit;
	private final int calendarUnit;

	public RelativeTimeChangeFormat(Logger logger, String unit, int calendarUnit) {
		this.logger = logger;

		this.unit = unit;
		this.calendarUnit = calendarUnit;

		outline = new PhraseOutline("relatime time change " + unit + " format");
		outline.addMandatoryTag(new Tag(TagType.TIME_CHANGE, null));
		outline.addMandatoryTag(new Tag(TagType.TIME, unit));
		outline.negateUnxepectedTagPenalty();
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag[] sequenceRequest = new Tag[2];
		sequenceRequest[0] = new Tag(TagType.TIME_CHANGE, null);
		sequenceRequest[1] = new Tag(TagType.TIME, unit);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);
		Tag changeTag = sequence[0];
		String changeString = changeTag.getValue();

		int change = 0;
        switch (changeString) {
            case "next":
                change = 1;
                break;
            case "prev":
                change = -1;
                break;
            default:
                logger.log("Invalid change in taglist", LogSource.ERROR, LogSource.TIME, 1);
                break;
        }

		// since relative, add to current minutes
		int currentOfUnit = DateParser.getCurrent(calendarUnit);

		return currentOfUnit + change;
	}

}
