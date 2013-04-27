package net.sprakle.homeAutomation.interpretation;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Phrase {

	Logger logger;

	// original text from input
	String rawText;

	private ArrayList<Tag> tags;

	public Phrase(Logger logger, Tagger tagger, String rawText) {
		this.logger = logger;
		this.rawText = rawText;

		logger.log("New phrase created. Raw text: '" + rawText + "'", LogSource.PHRASE_INFO, 3);

		tags = tagger.tagText(rawText);

		logger.log("Tagging phrase:", LogSource.PHRASE_INFO, 2);
		String tagString = "";
		for (Tag t : tags) {
			tagString += t + " ";
		}
		logger.log(tagString, LogSource.PHRASE_INFO, 2);
	}

	public int indexOfTag(Tag t) {
		return tags.indexOf(t);
	}

	public Tag getTag(int index) {
		return tags.get(index);
	}

	/**
	 * Returns the first occurrence of a matching tag
	 * 
	 * @param shellTag
	 * @return
	 */
	public Tag getTag(Tag shellTag) {
		for (Tag t : tags) {
			if (match(shellTag, t))
				return t;
		}

		return null;
	}

	/**
	 * Used to get a sequence of tags from a phrase. Tags are matched by type,
	 * and value if specified. Order of tags is considered
	 * 
	 * @param sequenceRequested
	 * @return
	 */
	public Tag[] getTagSequence(Tag[] sequenceRequested) {
		Tag sequence[] = new Tag[sequenceRequested.length];

		System.out.println("Seqreq len: " + sequenceRequested.length);
		int startIndex = indexOf(sequenceRequested);

		for (int i = 0; i < sequence.length; i++) {
			sequence[i] = tags.get(startIndex + i);
		}

		return sequence;
	}

	// used in getting a sequence of tags
	private int indexOf(Tag[] pattern) {
		int[] failure = computeFailure(pattern);

		Tag[] data = tags.toArray(new Tag[tags.size()]);

		int j = 0;
		if (data.length == 0)
			return -1;

		for (int i = 0; i < data.length; i++) {
			while (j > 0 && !match(pattern[j], data[i])) {
				j = failure[j - 1];
			}
			if (match(pattern[j], data[i])) {
				j++;
			}
			if (j == pattern.length) {
				return i - pattern.length + 1;
			}
		}
		return -1;
	}

	// Used in getting a sequence of tags
	private int[] computeFailure(Tag[] pattern) {
		int[] failure = new int[pattern.length];

		int j = 0;
		for (int i = 1; i < pattern.length; i++) {
			while (j > 0 && !match(pattern[j], pattern[i])) {
				j = failure[j - 1];
			}
			if (match(pattern[j], pattern[i])) {
				j++;
			}
			failure[i] = j;
		}

		return failure;
	}

	// similar to other getTagOfType(), but searches after a specific index - returns first one fond at the given index 
	public Tag getTag(Tag shellTag, int startIndex) {
		ArrayList<Tag> releventTags = new ArrayList<Tag>(tags.subList(startIndex, tags.size()));

		for (Tag t : releventTags) {
			if (match(shellTag, t))
				return t;
		}

		return null;
	}

	/**
	 * 
	 * @param absolute
	 *            tag to base the traversal from
	 * @param delta
	 *            amount to traverse. 0 = the tag passed, 1 = the tag after
	 * @return
	 */
	public Tag getRelativeTag(Tag absolute, int delta) {
		int absoluteIndex = tags.indexOf(absolute);

		if (absoluteIndex == -1)
			logger.log("Invalid absolute tag", LogSource.ERROR, LogSource.PHRASE_INFO, 1);

		int relativeIndex = absoluteIndex + delta;
		if (relativeIndex < 0 || relativeIndex > tags.size() - 1)
			return null;

		Tag relativeTag = tags.get(relativeIndex);
		return relativeTag;
	}

	/**
	 * Searches for a specific tag starting from the absolute tag. Starts at
	 * absoluteIndex + delta, and continues until it has found a match or passed
	 * allowed tolerances
	 * 
	 * @param absolute
	 *            Tag to start searching from
	 * @param shellTag
	 *            Tag that must be matched
	 * @param delta
	 *            Added to absolute index to start search
	 * @param maxTraversal
	 *            Maximum distance to search
	 * @return The matching tag. Returns null if nothing found
	 */
	public Tag getRelativeTag(Tag absolute, Tag shellTag, int delta, int maxTraversal) {
		// increment delta until tag is found, the end of the array has been reached, or the max traversal value has been passed

		int absoluteIndex = tags.indexOf(absolute);
		int maxDeltaLeft = absoluteIndex * -1;
		int maxDeltaRight = tags.size() - 1;

		// while delta is within allowed tolerances
		while (delta >= maxDeltaLeft && delta <= maxDeltaRight && Math.abs(delta) <= maxTraversal) {
			Tag t = getRelativeTag(absolute, delta);

			if (match(shellTag, t))
				return t;

			// wrong type. find the next in line
			if (delta > 0)
				delta++;
			else
				delta--;
		}

		return null;
	}

	private boolean match(Tag shell, Tag target) {
		System.out.println("Matching " + shell + " to " + target);
		TagType type = shell.getType();
		String value = shell.getValue();

		boolean mustMatchType = type != null;
		boolean mustMatchValue = value != null;

		boolean typeMatched = true;
		boolean valueMatched = true;

		// auto fail if mustMatchType and the target has no type, same with value
		if ((mustMatchType && target.getType() == null) || (mustMatchValue && target.getValue() == null))
			return false;

		if (mustMatchType && target.getType() != type)
			typeMatched = false;

		if (mustMatchValue && !target.getValue().equals(value))
			valueMatched = false;

		if (typeMatched && valueMatched)
			return true;

		return false;
	}

	@Override
	public String toString() {
		return rawText;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}

	/**
	 * 
	 * @param logger
	 * @param phraseOutlines
	 *            arraylist of possibilities to match
	 * @return the matching outline. returns null if there are not matches
	 */
	public PhraseOutline matchOutlines(Logger logger, List<PhraseOutline> phraseOutlines) {
		// used to sort outlines by their confidence rating
		TreeMap<Integer, PhraseOutline> matches = new TreeMap<Integer, PhraseOutline>();

		// for every 1 dimensional array AKA phrase outline
		for (PhraseOutline at : phraseOutlines) {

			int outlineConfidence = at.match(this);
			if (outlineConfidence > 0) {
				matches.put(outlineConfidence, at);
			}
		}

		// return null if no matches
		if (matches.size() == 0)
			return null;

		PhraseOutline match = matches.lastEntry().getValue();

		// get the most confident entry
		return match;
	}
}
