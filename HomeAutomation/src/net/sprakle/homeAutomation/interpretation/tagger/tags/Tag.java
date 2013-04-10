/* Contains 3 values. The first is an enumeration defining the tag type, 
 * the second is the value of the tag. EX: {COMMAND(VERB)/"activate"}
 * The third defines the tag's position in the phrase for later sequencing
 * 
 */

package net.sprakle.homeAutomation.interpretation.tagger.tags;

import net.sprakle.homeAutomation.interpretation.tagger.TagFileParser;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Tag {
	private TagType type;

	private String value;

	public Tag(TagType type, String value) {
		this.type = type;
		this.value = value;
	}

	public Tag(Logger logger, String originalText) {
		TagType type = TagFileParser.getType(logger, originalText);
		String value = TagFileParser.getValue(logger, originalText);

		this.type = type;
		this.value = value;
	}

	public TagType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{" + type + "/" + value + "}";
	}
}