package net.sprakle.homeAutomation.utilities.time.getters.minutes.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

/**
 * Parses mintes. Examples:
 * 
 * "at 5" returns 0 Used when the user only specifies an hour, and
 * 
 * @author ben
 * 
 */
public class AbsoluteFormat implements TimeFormat {

	private PhraseOutline outline;

	public AbsoluteFormat(Logger logger) {
		outline = new PhraseOutline(logger, "absolute minute format");
		outline.addMandatoryTag(new Tag(TagType.LANGUAGE, "at"));
		outline.addMandatoryTag(new Tag(TagType.NUMBER, null));
		outline.negateUnxepectedTagPenalty();
	}

	@Override
	public PhraseOutline getOutline() {
		return outline;
	}

	@Override
	public int getTime(Phrase phrase) {
		return 0;
	}

}
