package net.sprakle.homeAutomation.interpretation.tagger;

import java.util.ArrayList;
import java.util.List;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class PhraseOutline {

	private final int EXPECTED_TAG_WEIGHT = 1;
	private final int SPECIFIC_TAG_WEIGHT = 1;
	private final int UNEXPECTED_TAG_WEIGHT = 1;

	private String description;

	private ArrayList<Tag> mandatoryTags;
	private ArrayList<Tag> neutralTags;

	// match settings
	private int maxTagSeparation = 2;
	private boolean negateUnxepectedTagPenalty = false;

	public PhraseOutline(Logger logger, String description) {
		this.description = description;

		mandatoryTags = new ArrayList<Tag>();
		neutralTags = new ArrayList<Tag>();
	}

	/**
	 * Match a phrase against this outline
	 * 
	 * @param phrase
	 * @return an integer of the confidence of the match. 0 is no confidence
	 */
	public int match(Phrase phrase) {

		/*
		 * A phrase can have multiple tags to start matching from, for example:
		 * 		Outline: {FRACTION/null} {TIME_CHANGE/null}			Expecting: "do something at quarter after"
		 * 		Phrase:  "Do half by homework at quarter after"
		 * 
		 * The matcher will work off of the {FRACTION/half} tag, resulting on zero confidence, as the
		 * following tag is not a TIME_CHANGE tag.
		 * 
		 * To combat this, a list is made of all tags in the phrase with the correct starting type (and value if
		 * applicable). For each starting tag, a new List<Tag> is made of it and the tags following, and each of
		 * them are sent to the matcher. The starting tag with the highest confidence is selected as the correct
		 * one, and this confidence is returned, completing the process.
		 */

		// tags in phrase
		List<Tag> phraseTags = phrase.getTags();

		// find all potential starting tags
		List<List<Tag>> potentialMatchers = new ArrayList<List<Tag>>();
		for (Tag t : phraseTags) {
			Tag outlineTag = mandatoryTags.get(0);
			if (tagsMatch(outlineTag, t)) {
				int startIndex = phraseTags.indexOf(t);
				int endIndex = phraseTags.size();
				List<Tag> potentialMatcher = phraseTags.subList(startIndex, endIndex);
				potentialMatchers.add(potentialMatcher);
			}
		}

		int confidence = 0;

		// check each potential starting tag, keeping the one with the highest confidence
		for (List<Tag> potentialMatcher : potentialMatchers) {

			int currentConfidence = getConfidence(potentialMatcher);
			if (currentConfidence > confidence)
				confidence = currentConfidence;
		}

		return confidence;
	}

	private int getConfidence(List<Tag> tags) {
		int confidence = 0;

		int mininumExpectedTags = mandatoryTags.size();

		int expectedTags = 0;
		int specificTags = 0;
		int unexpectedTags = 0;

		Tag previousMatch = null;
		Tag previousOutline = null;

		// tag outlines that have been checked already
		List<Tag> visitedMandatoryTags = new ArrayList<Tag>();

		for (Tag tag : tags) {
			TagResult result = matchTag(tags, visitedMandatoryTags, previousOutline, previousMatch, tag);

			switch (result) {
				case NEUTRAL:
					// do nothing
					break;

				case EXPECTED:
					expectedTags += EXPECTED_TAG_WEIGHT;

					previousMatch = tag;
					previousOutline = visitedMandatoryTags.get(visitedMandatoryTags.size() - 1);
					break;

				case SPECIFIC:
					specificTags += SPECIFIC_TAG_WEIGHT;
					expectedTags += EXPECTED_TAG_WEIGHT;

					previousMatch = tag;
					previousOutline = visitedMandatoryTags.get(visitedMandatoryTags.size() - 1);
					break;

				case UNEXPECTED:
					unexpectedTags += UNEXPECTED_TAG_WEIGHT;
					break;
			}

		}

		if (negateUnxepectedTagPenalty)
			unexpectedTags = 0;

		confidence = expectedTags + specificTags - unexpectedTags;

		if (expectedTags < mininumExpectedTags)
			confidence = 0;

		return confidence;
	}

	private TagResult matchTag(List<Tag> allTags, List<Tag> visitedMandatoryTags, Tag previousOutline, Tag previousMatch, Tag tag) {

		if (taglistContainsType(neutralTags, tag))
			return TagResult.NEUTRAL;

		// make sure the tag is on the outline, by checking if the type and value (if applicable) matches
		// only look through outline tags that have not been checked yet
		boolean inOutline = false;
		List<Tag> pool = new ArrayList<Tag>(mandatoryTags);
		pool.removeAll(visitedMandatoryTags);
		for (Tag outlineTag : pool) {
			boolean match = tagsMatch(outlineTag, tag);
			boolean outlineFollowsPrevious = tagFollowsTag(mandatoryTags, previousOutline, outlineTag, 1);
			boolean tagFollowsPrevious = tagFollowsTag(allTags, previousMatch, tag, maxTagSeparation);

			if (match && outlineFollowsPrevious && tagFollowsPrevious) {
				inOutline = true;
				visitedMandatoryTags.add(outlineTag);

				// if the outline tag had a value, it was matched as specific
				if (outlineTag.getValue() != null)
					return TagResult.SPECIFIC;
				else
					return TagResult.EXPECTED;
			}
		}

		// if not in outline and it's not UNKOWN_TEXT, it's unexpected
		if (!inOutline) {
			if (tag.equalsType(new Tag(TagType.UNKOWN_TEXT, null)))
				return TagResult.NEUTRAL;
			else
				return TagResult.UNEXPECTED;
		}

		// nothing should be here
		return null;
	}

	/**
	 * Matches types, and values ONLY if target has a value
	 * 
	 * @param target
	 *            The tag that test should be
	 * @param test
	 *            Tag tag that should be test
	 * @return true if match
	 */
	private boolean tagsMatch(Tag target, Tag test) {
		boolean checkValue = target.getValue() != null;

		if (checkValue) {
			if (test.equalsTypeValue(target))
				return true;

		} else if (test.equalsType(target))
			return true;

		return false;
	}

	private boolean tagFollowsTag(List<Tag> list, Tag previous, Tag current, int maxSeparation) {
		// automatically return true if the tag is the first
		if (previous == null)
			return true;

		int prevMatchIndex = list.indexOf(previous);
		int tagIndex = list.indexOf(current);
		int difference = tagIndex - prevMatchIndex;
		if (difference > 0 && difference <= maxTagSeparation)
			return true;
		else
			return false;
	}

	private enum TagResult {
		NEUTRAL,
		UNEXPECTED,
		EXPECTED,
		SPECIFIC;
	}

	/**
	 * Greatest difference of indexes two tags in a phrase can have to be
	 * considered adjacent.
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

	/**
	 * There will be no reduction of confidence by unexpected tags. Useful for
	 * when expecting a lot of unknown input, such as a social message
	 */
	public void negateUnxepectedTagPenalty() {
		negateUnxepectedTagPenalty = true;
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
	 * type
	 * 
	 * @param list
	 *            list of tags
	 * @param t
	 *            tag that should be matched
	 * @return true if there is a match
	 */
	private boolean taglistContainsType(List<Tag> list, Tag check) {
		for (Tag t : list) {
			if (t.equalsType(check))
				return true;
		}

		return false;
	}

}
