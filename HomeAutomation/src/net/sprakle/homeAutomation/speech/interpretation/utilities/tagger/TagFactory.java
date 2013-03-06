/*
 * Takes a line of text (usually from the tagList file) and creates a tag based on it.
 */

package net.sprakle.homeAutomation.speech.interpretation.utilities.tagger;

import net.sprakle.homeAutomation.speech.interpretation.utilities.tagger.tags.Tag;
import net.sprakle.homeAutomation.speech.interpretation.utilities.tagger.tags.TagType;
import net.sprakle.homeAutomation.speech.interpretation.utilities.tagger.tags.TagUtilities;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class TagFactory {
	static Tag getTag(Logger logger, String s, String rawText) {
		TagType type = TagFileParser.getType(logger, s);
		String value = TagFileParser.getValue(logger, s);

		String trigger = TagFileParser.getTrigger(logger, s);
		int position = TagUtilities.getPosition(logger, trigger, rawText);

		Tag tag = new Tag(type, value, position);

		return tag;
	}
}
