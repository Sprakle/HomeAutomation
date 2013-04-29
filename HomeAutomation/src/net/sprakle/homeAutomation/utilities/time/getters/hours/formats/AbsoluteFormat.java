package net.sprakle.homeAutomation.utilities.time.getters.hours.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

/**
 * Parses hours. Examples:
 * 
 * "at 5" returns 5
 * 
 * @author ben
 * 
 */
public class AbsoluteFormat implements TimeFormat {

	private PhraseOutline outline;

	public AbsoluteFormat(Logger logger) {
		outline = new PhraseOutline(logger, "absolute hour format");
		outline.addMandatoryTag(new Tag(TagType.UNKOWN_TEXT, "at"));
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
		sequenceRequest[0] = new Tag(TagType.UNKOWN_TEXT, "at");
		sequenceRequest[1] = new Tag(TagType.NUMBER, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);

		Tag numberTag = sequence[1];

		String numberString = numberTag.getValue();
		int number = Integer.parseInt(numberString);

		return number;
	}

}
