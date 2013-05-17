/* Contains 3 values. The first is an enumeration defining the tag type, 
 * the second is the value of the tag. EX: {COMMAND(VERB)/"activate"}
 * The third defines the tag's position in the phrase for later sequencing
 * 
 */

package net.sprakle.homeAutomation.interpretation.tagger.tags;

import net.sprakle.homeAutomation.interpretation.tagger.TagFileParser;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Tag {

	private String originalTrigger;
	private final TagType type;
	private final String value;

	public Tag(TagType type, String value) {
		this.type = type;
		this.value = value;
	}

	public Tag(Logger logger, String originalText) {
		TagType type = TagFileParser.getType(logger, originalText);
		String value = TagFileParser.getValue(logger, originalText);

		this.originalTrigger = TagFileParser.getTrigger(logger, originalText);
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

	/**
	 * Text that was used to create this tag. May be null if this tag was
	 * created through other means
	 * 
	 * @return
	 */
	public String getOriginalTrigger() {
		return originalTrigger;
	}

	public boolean equalsType(Tag t) {
        return type == t.getType();

    }

	boolean equalsValue(Tag t) {
        return value.equals(t.getValue());

    }

	public boolean equalsTypeValue(Tag t) {
        return equalsType(t) && equalsValue(t);

    }

}