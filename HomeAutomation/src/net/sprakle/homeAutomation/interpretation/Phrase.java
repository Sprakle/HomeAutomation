package net.sprakle.homeAutomation.interpretation;

import java.util.ArrayList;

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

	public int getTagIndex(Tag t) {
		return tags.indexOf(t);
	}

	// TODO: instead of a tagtype, allow passing of a shell tag, whitch can include a value

	/*
	 *  when given a shell tag (Only the TagType is set) it will return the full tag from a phrase
	 *  If there are multiple tags found, or no tags found, null will be returned
	 */
	public Tag getTagOfType(TagType queryType) {
		Tag result = null;

		ArrayList<Tag> matchingTags = new ArrayList<Tag>();
		for (Tag t : tags) {
			if (t.getType() == queryType) {
				matchingTags.add(t);
			}
		}

		if (matchingTags.size() == 1) {
			result = matchingTags.get(0);
		}

		return result;
	}

	// similar to other getTagOfType(), but searches after a specific index - returns first one fond at the given index 
	public Tag getTagOfType(TagType queryType, int startIndex) {
		ArrayList<Tag> releventTags = new ArrayList<Tag>(tags.subList(startIndex, tags.size()));

		for (Tag t : releventTags) {
			if (t.getType() == queryType) {
				return t;
			}
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
	 * 
	 * @param type
	 *            use to get a specific type
	 * 
	 * @param absolute
	 *            tag to base the traversal from
	 * @param delta
	 *            amount to traverse. 0 = the tag passed, 1 = the tag after
	 * @return
	 */
	public Tag getRelativeTag(TagType type, Tag absolute, int delta) {
		while (delta > 0 && delta < tags.size()) {
			Tag t = getRelativeTag(absolute, delta);

			if (t.getType() == type)
				return t;

			// wrong type. find the next in line
			if (delta > 0)
				delta++;
			else
				delta--;
		}

		return null;
	}

	@Override
	public String toString() {
		return rawText;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}
}
