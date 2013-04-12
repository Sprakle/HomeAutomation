package net.sprakle.homeAutomation.interpretation.tagger;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class PhraseOutline {

	private String description;

	private ArrayList<Tag> mandatoryTags;
	private ArrayList<Tag> neutralTags;

	private int maxTagSeparation = 2;

	public PhraseOutline(Logger logger, String description) {
		this.description = description;

		mandatoryTags = new ArrayList<Tag>();
		neutralTags = new ArrayList<Tag>();
	}

	// return an integer of the confidence of the match. 0 is no confidence
	// Confidence = total matches + total specific tags (tags with their type AND value defined) - total unexpected NON-UNKOWN_TEXT tags
	public int match(Phrase phrase) {

		int confidence = 0;

		int mininumExpectedTags = mandatoryTags.size();

		int expectedTags = 0;
		int specificTags = 0;
		int unexpectedTags = 0;

		// consider making weights decimal values, and multiplying the end results by the weights
		int expectedTagWeight = 1;
		int specificTagWeight = 1;
		int unexpectedTagWeight = 1;

		int lastOutlineTagPos = -1;

		// for every tag in the phrase
		ArrayList<Tag> phraseTags = phrase.getTags();
		for (Tag phraseTag : phraseTags) {

			// skip if neutral tag
			if (taglistContainsType(neutralTags, phraseTag))
				continue;

			// is it in the outline?
			Tag outlineTag = null;
			for (Tag t : mandatoryTags) {
				if (t.equalsType(phraseTag)) {

					// make sure this phrase is in the right order, by checking if the matching outline tag came after the last one, but not too far ahead
					int outlineTagPos = mandatoryTags.indexOf(t);

					int difference = outlineTagPos - lastOutlineTagPos;
					if (difference > 0 && difference < maxTagSeparation + 1) {
						outlineTag = t;

						// break the loop, as we only want to first tag of the correct position
						break;
					}
				}
			}

			if (outlineTag != null) {
				// the phrase tag was contained within the outline, and at the correct position

				// if the outline tag has a value, make sure the phrase tag has a matching one
				if (outlineTag.getValue() != null) {
					if (!outlineTag.equalsValue(phraseTag)) {
						// fail
						unexpectedTags += unexpectedTagWeight;
						continue;
					} else {
						specificTags += specificTagWeight; // this tag specified a value. add to confidence
					}
				}

				lastOutlineTagPos++;
				expectedTags += expectedTagWeight;

			} else {

				// this is an unexpected tag. add to count if this is not an UNKOWN_TEXT tag
				if (!phraseTag.equalsType(new Tag(TagType.UNKOWN_TEXT, null))) {
					unexpectedTags += unexpectedTagWeight;
				}
			}
		}

		confidence = expectedTags + specificTags - unexpectedTags;

		// confidence is zero if there were not enough matches
		if (expectedTags < mininumExpectedTags)
			confidence = 0;

		return confidence;
	}

	/**
	 * Greatest difference of indexes two tags can have to be considered
	 * adjacent
	 * 
	 * @param max
	 */
	public void setMaxTagSeparation(int max) {
		maxTagSeparation = max;
	}

	/**
	 * This tag MUST be in a phrase for its confidence to be above 0 Mandatory
	 * tags will be matched by their value if it is not null, otherwise and tag
	 * of the same type will match
	 * 
	 * @param t
	 *            shell tag
	 */
	public void addMandatoryTag(Tag t) {
		mandatoryTags.add(t);
	}

	/**
	 * Neutral tags will not count against or for the confidence of a tag
	 * neutral tags are only matched by their type, not value
	 * 
	 * @param t
	 *            shell tag
	 */
	public void addNeutralTag(Tag t) {
		neutralTags.add(t);
	}

	public ArrayList<Tag> getTags() {
		return new ArrayList<Tag>(mandatoryTags);
	}

	@Override
	public String toString() {
		return "'" + description + "'";
	}

	/**
	 * Checks a list of tags if it contains a tag that matches the given tags
	 * value
	 * 
	 * @param list
	 *            list of tags
	 * @param t
	 *            tag that should be matched
	 * @return true if there is a match
	 */
	private boolean taglistContainsValue(ArrayList<Tag> list, Tag check) {
		for (Tag t : list) {
			if (t.equalsValue(check))
				return true;
		}

		return false;
	}

	/**
	 * Checks a list of tags if it contains a tag that matches the given tags
	 * type
	 * 
	 * @param list
	 *            list of tags
	 * @param t
	 *            tag that should be matched
	 * @return true if there is a match
	 */
	private boolean taglistContainsType(ArrayList<Tag> list, Tag check) {
		for (Tag t : list) {
			if (t.equalsType(check))
				return true;
		}

		return false;
	}

	/**
	 * Checks a list of tags if it contains a tag that matches the given tags
	 * type AND value
	 * 
	 * @param list
	 *            list of tags
	 * @param t
	 *            tag that should be matched
	 * @return true if there is a match
	 */
	private boolean taglistContainsTypeValue(ArrayList<Tag> list, Tag check) {
		for (Tag t : list) {
			if (t.equalsTypeValue(check))
				return true;
		}

		return false;
	}

}
