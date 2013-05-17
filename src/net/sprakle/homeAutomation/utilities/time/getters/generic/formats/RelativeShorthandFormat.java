package net.sprakle.homeAutomation.utilities.time.getters.generic.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.DateParser;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

/**
 * Parses time. Example:
 * 
 * RelativeNumberFormat(logger, "hour", Calendar.HOUR)
 * 
 * "few hours" returns {current hours} + 3
 * 
 * @author ben
 * 
 */
public class RelativeShorthandFormat implements TimeFormat {

	private final PhraseOutline outline;
	private final String unit;
	private final int calendarUnit;

	public RelativeShorthandFormat(String unit, int calendarUnit) {
		this.unit = unit;
		this.calendarUnit = calendarUnit;

		outline = new PhraseOutline("relative shorthand " + unit + " format");
		outline.addMandatoryTag(new Tag(TagType.SHORTHAND_TIME, null));
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
		sequenceRequest[0] = new Tag(TagType.SHORTHAND_TIME, null);
		sequenceRequest[1] = new Tag(TagType.TIME, unit);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);
		Tag numberTag = sequence[0];

		String numberString = numberTag.getValue();
		int number = Integer.parseInt(numberString);

		// since relative, add to current minutes
		int currentOfUnit = DateParser.getCurrent(calendarUnit);

		return currentOfUnit + number;
	}

}
