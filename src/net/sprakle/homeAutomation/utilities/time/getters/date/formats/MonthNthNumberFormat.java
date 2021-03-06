package net.sprakle.homeAutomation.utilities.time.getters.date.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

public class MonthNthNumberFormat implements TimeFormat {

	private final Logger logger;
	private final PhraseOutline outline;

	public MonthNthNumberFormat(Logger logger) {
		this.logger = logger;

		outline = new PhraseOutline("nth number date format");
		outline.addMandatoryTag(new Tag(TagType.MONTH, null));
		outline.addMandatoryTag(new Tag(TagType.NTH_NUMBER, null));
		outline.negateUnxepectedTagPenalty();
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag[] sequenceRequest = new Tag[2];
		sequenceRequest[0] = new Tag(TagType.MONTH, null);
		sequenceRequest[1] = new Tag(TagType.NTH_NUMBER, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);
		Tag numberTag = sequence[1];

		String numberString = numberTag.getValue();

		if (!numberString.matches("\\d*")) {
			logger.log("Invalid {NTH_NUMBER} value in taglist. Value must be an integer", LogSource.ERROR, LogSource.TIME, 1);
			return -1;
		}

		int number = Integer.parseInt(numberString);

		return number;
	}

}
