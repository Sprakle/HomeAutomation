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
 * "10 to" returns -10
 * 
 * "20 after" returns 20
 * 
 * @author ben
 * 
 */
public class NumberDiffFormat implements TimeFormat {

	private final PhraseOutline outline;

	public NumberDiffFormat() {
		outline = new PhraseOutline("number difference minute format");
		outline.addMandatoryTag(new Tag(TagType.NUMBER, null));
		outline.addMandatoryTag(new Tag(TagType.TIME_CHANGE, null));
		outline.negateUnxepectedTagPenalty();
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}
	@Override
	public int getTime(Phrase phrase) {
		Tag[] sequenceRequest = new Tag[2];
		sequenceRequest[0] = new Tag(TagType.NUMBER, null);
		sequenceRequest[1] = new Tag(TagType.TIME_CHANGE, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);

		Tag numberTag = sequence[0];
		Tag changeTag = sequence[1];

		String numberString = numberTag.getValue();
		int number = Integer.parseInt(numberString);

		if (changeTag.getValue().equals("prev"))
			number *= -1;

		return number;
	}

}
