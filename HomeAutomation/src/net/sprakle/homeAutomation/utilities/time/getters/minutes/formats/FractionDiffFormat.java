package net.sprakle.homeAutomation.utilities.time.getters.minutes.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

/**
 * Parses minutes. Examples:
 * 
 * "quarter to" returns -25
 * 
 * "half past" returns 30
 * 
 * @author ben
 * 
 */
public class FractionDiffFormat implements TimeFormat {

	private PhraseOutline outline;

	public FractionDiffFormat(Logger logger) {
		outline = new PhraseOutline(logger, "fraction difference minute format");
		outline.addMandatoryTag(new Tag(TagType.FRACTION, null));
		outline.addMandatoryTag(new Tag(TagType.TIME_CHANGE, null));
		outline.setMaxTagSeparation(1);
		outline.negateUnxepectedTagPenalty();
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag[] sequenceRequest = new Tag[2];
		sequenceRequest[0] = new Tag(TagType.FRACTION, null);
		sequenceRequest[1] = new Tag(TagType.TIME_CHANGE, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);

		Tag fractionTag = sequence[0];
		Tag changeTag = sequence[1];

		String numberString = fractionTag.getValue();
		float number = Float.parseFloat(numberString);

		number *= 60;

		if (changeTag.getValue().equals("prev"))
			number *= -1;

		return Math.round(number);
	}

}
