/* Contains 3 values. The first is an enumeration defining the tag type, 
 * the second is the value of the tag. EX: {COMMAND(VERB)/"activate"}
 * The third defines the tag's position in the phrase for later sequencing
 * 
 */

package net.sprakle.homeAutomation.interpretation.tagger.tags;

public class Tag {
	private TagType type;

	private String value;
	private String originalText;

	private int position;

	public Tag(TagType type, String value, String originalText, int position) {
		this.type = type;
		this.value = value;
		this.originalText = originalText;
		this.position = position;
	}

	public TagType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String getOriginalText() {
		return originalText;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "{" + type + "/" + value + "}-[" + position + "]";
	}

	// checks if this tag should be sorted before another
	public boolean comesBefore(Tag t) {
		if (position < t.getPosition()) {
			return true;
		} else if (position > t.getPosition()) {
			return false;
		}

		return false;
	}

}