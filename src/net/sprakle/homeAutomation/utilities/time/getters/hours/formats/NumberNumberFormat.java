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
 * "10 30" returns 10
 * 
 * @author ben
 * 
 */
public class NumberNumberFormat implements TimeFormat {

	private final PhraseOutline outline;

	public NumberNumberFormat() {
		outline = new PhraseOutline("Number:number hour format");
		outline.addMandatoryTag(new Tag(TagType.NUMBER, null));
		outline.addMandatoryTag(new Tag(TagType.NUMBER, null));
		outline.negateUnxepectedTagPenalty();
		outline.setMaxTagSeparation(1);
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag[] sequenceRequest = new Tag[2];
		sequenceRequest[0] = new Tag(TagType.NUMBER, null);
		sequenceRequest[1] = new Tag(TagType.NUMBER, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);
		Tag hourTag = sequence[0];

		return Integer.parseInt(hourTag.getValue());
	}

}
