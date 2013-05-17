package net.sprakle.homeAutomation.utilities.time.getters.months.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

public class AbsoluteFormat implements TimeFormat {

	private final Logger logger;
	private final PhraseOutline outline;

	public AbsoluteFormat(Logger logger) {
		this.logger = logger;

		outline = new PhraseOutline("absolute month format");
		outline.addMandatoryTag(new Tag(TagType.MONTH, null));
		outline.negateUnxepectedTagPenalty();
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag monthTag = phrase.getTag(new Tag(TagType.MONTH, null));
		String monthString = monthTag.getValue();

		if (!monthString.matches("\\d*")) {
			logger.log("Invalid {MONTH} value in taglist. Value must be an integer", LogSource.ERROR, LogSource.TIME, 1);
			return -1;
		}

		int month = Integer.parseInt(monthString);

		return month;
	}

}
