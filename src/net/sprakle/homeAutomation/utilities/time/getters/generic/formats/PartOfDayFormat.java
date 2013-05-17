package net.sprakle.homeAutomation.utilities.time.getters.generic.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

/**
 * Parses time. Example:
 * 
 * RelativeNumberFormat(logger, "hour", 0)
 * 
 * "breakfast" returns the hours of "breakfast" defined in the taglist
 * 
 * @author ben
 * 
 */
public class PartOfDayFormat implements TimeFormat {

	private final Logger logger;

	private final PhraseOutline outline;

	private final int taglistIndex;

	/**
	 * 
	 * @param logger
	 * @param unit
	 * @param unit
	 *            In the taglist {PART_OF_DAY} tags, numbers are separated by
	 *            '-'. Choose the index of the relevant number. 0 is the first
	 */
	public PartOfDayFormat(Logger logger, String unit, int taglistIndex) {
		this.logger = logger;
		this.taglistIndex = taglistIndex;

		outline = new PhraseOutline("part of day " + unit + " format");
		outline.addMandatoryTag(new Tag(TagType.PART_OF_DAY, null));
		outline.negateUnxepectedTagPenalty();
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag partOfDayTag = phrase.getTag(new Tag(TagType.PART_OF_DAY, null));

		String partOfDayString = partOfDayTag.getValue();
		int num = PODStringToInt(partOfDayString);

		return num;
	}

	private int PODStringToInt(String s) {
		String hourString = s.split("-")[taglistIndex];

		if (!hourString.matches("\\d*")) {
			logger.log("Invalid part of day in config", LogSource.ERROR, LogSource.TIME, 1);
			return -1;
		}

		int num = Integer.parseInt(hourString);
		return num;
	}

}
