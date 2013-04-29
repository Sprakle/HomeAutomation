package net.sprakle.homeAutomation.utilities.time.getters.hours.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

// Note: if this is catching to many phrases, add make the phrase outline {NUMBER} {TIME_CHANGE} {NUMBER} and add a similar one for fractions
/**
 * Parses hours. Examples:
 * 
 * "10 to 7" returns 7
 * 
 * "20 after 6" returns 6
 * 
 * @author ben
 * 
 */
public class NumberDiffFormat implements TimeFormat {

	private PhraseOutline outline;

	public NumberDiffFormat(Logger logger) {
		outline = new PhraseOutline(logger, "number difference hour format");
		outline.addMandatoryTag(new Tag(TagType.TIME_CHANGE, null));
		outline.addMandatoryTag(new Tag(TagType.NUMBER, null));
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
		sequenceRequest[1] = new Tag(TagType.NUMBER, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);

		Tag numberTag = sequence[1];

		String numberString = numberTag.getValue();
		int number = Integer.parseInt(numberString);

		return number;
	}

}
