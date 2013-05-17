package net.sprakle.homeAutomation.utilities.time.getters.days.formats;

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

		outline = new PhraseOutline("absolute day format");
		outline.addMandatoryTag(new Tag(TagType.DAY, null));
		outline.negateUnxepectedTagPenalty();
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag dayTag = phrase.getTag(new Tag(TagType.DAY, null));
		String dayString = dayTag.getValue();

		if (!dayString.matches("\\d*")) {
			logger.log("Invalid {DAY} value in taglist. Value must be an integer", LogSource.ERROR, LogSource.TIME, 1);
			return -1;
		}

		// Java calendar has Sunday as 1, instead of the taglist 0
		int day = Integer.parseInt(dayString) + 1;

		return day;
	}
}
