package net.sprakle.homeAutomation.interpretation.tagger;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class PhraseOutline {

	private Logger logger;
	private Tagger tagger;

	private String description;

	private ArrayList<Tag> outlineTags;

	public PhraseOutline(Logger logger, Tagger tagger, String description) {
		this.logger = logger;
		this.tagger = tagger;

		this.description = description;

		outlineTags = new ArrayList<Tag>();
	}

	// FIXME: lower confidence if there are extra IDENTIFIED tags

	// return an integer of the confidence of the match. 0 is no confidence
	// Confidence = total matches + total specific tags (tags with their type AND value defined) - total unexpected NON-UNKOWN_TEXT tags
	public int match(Phrase phrase) {

		System.out.println("Matching phrase: " + phrase.getRawText());

		int confidence = 0;

		int mininumExpectedTags = outlineTags.size();

		int expectedTags = 0;
		int specificTags = 0;
		int unexpectedTags = 0;

		// consider making weights decimal values, and multiplying the end results by the weights
		int expectedTagWeight = 1;
		int specificTagWeight = 1;
		int unexpectedTagWeight = 1;

		int lastOutlineTagPos = -1;

		// for every tag in the phrase
		ArrayList<Tag> phraseTags = tagger.tagText(phrase.getRawText());
		System.out.println("== " + this + " ==");
		for (Tag phraseTag : phraseTags) {
			System.out.println("  Phrase tag: " + phraseTag);

			// is it in the outline?
			Tag outlineTag = null;
			for (Tag t : outlineTags)
				if (t.getType().equals(phraseTag.getType())) {
					System.out.println("    checking position");

					// make sure this phrase is in the right order, by checking if the matching outline tag came after the last one, but not too far ahead
					int outlineTagPos = outlineTags.indexOf(t);
					System.out.println("      outline position: " + outlineTagPos + " (" + t + ")");
					System.out.println("      last outline position: " + lastOutlineTagPos);

					int difference = outlineTagPos - lastOutlineTagPos;
					if (difference > 0 && difference < 3) {
						outlineTag = t;

						// break the loop, as we only want to first tag of the correct position
						break;
					}
				}

			if (outlineTag != null) {
				// the phrase tag was contained within the outline, and at the correct position

				// if the outline tag has a value, make sure the phrase tag has a matching one
				String value = outlineTag.getValue();
				System.out.println("    outline tag had value: " + value);
				if (value != null) {
					if (!phraseTag.getValue().equals(value)) {
						// fail
						unexpectedTags += unexpectedTagWeight;
						System.out.println("    phrase tag failed due to no value match against " + outlineTag);
						continue;
					} else {
						System.out.println("    phrase was specific against " + outlineTag);
						specificTags += specificTagWeight; // this tag specified a value. add to confidence
					}
				}

				System.out.println("    phrase tag sucseeded against " + outlineTag);
				lastOutlineTagPos++;
				expectedTags += expectedTagWeight;

			} else {
				System.out.println("    tag was not in the outline");

				// this is an unexpected tag. add to count if this is not an UNKOWN_TEXT tag
				if (!phraseTag.getType().equals(TagType.UNKOWN_TEXT)) {
					unexpectedTags += unexpectedTagWeight;
				}
			}
		}

		System.out.println("  results:");
		System.out.println("    expected tags: " + expectedTags);
		System.out.println("    minimum tags: " + mininumExpectedTags);
		System.out.println("    specific tags: " + specificTags);
		System.out.println("    unexpected tags: " + unexpectedTags);

		confidence = expectedTags + specificTags - unexpectedTags;

		// confidence is zero if there were not enough matches
		if (expectedTags < mininumExpectedTags)
			confidence = 0;

		System.out.println("    TOTAL CONFIDENCE: " + confidence);

		return confidence;
	}
	public void addTag(Tag t) {
		outlineTags.add(t);
	}

	public ArrayList<Tag> getTags() {
		return new ArrayList<Tag>(outlineTags);
	}

	@Override
	public String toString() {
		return "'" + description + "'";
	}
}
