/*
 * This class helps with the understanding of strings of tags.
 */

package net.sprakle.homeAutomation.interpretation.tagger;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ParseHelpers {

	/*
	 * accepts a 2D array of tags , and and a phrase. Return's true if the phrase can be described by the array of ---
	 * EX: Array: (POWER_OPTION) (OD_OBJECT) || (SET_VALUE) (OD_OBJECT)    Phrase.rawText: "turn on the lamp"
	 * 		This will return true
	 * 
	 * EX: Array: (POWER_OPTION) (OD_OBJECT) || (SET_VALUE) (OD_OBJECT)    Phrase.rawText: "turn on the light"
	 * 		This will return true7
	 * 
	 * This should return the phrase outline arraylist of the matching outline
	 */

	public static PhraseOutline match(Logger logger, ArrayList<PhraseOutline> phraseOutlines, Phrase phrase) {

		// used to sort outlines by their confidence rating
		TreeMap<Integer, PhraseOutline> matches = new TreeMap<Integer, PhraseOutline>();

		// for every 1 dimensional array AKA phrase outline
		for (PhraseOutline at : phraseOutlines) {

			int outlineConfidence = at.match(phrase);
			if (outlineConfidence > 0) {
				matches.put(outlineConfidence, at);
			}
		}

		// return null if no matches
		if (matches.size() == 0)
			return null;

		// get the most confident entry
		return matches.lastEntry().getValue();
	}

	/*
	 *  when given a shell tag (Only the TagType is set) it will return the full tag from a phrase
	 *  If there are multiple tags found, or no tags found, null will be returned
	 */
	public static Tag getTagOfType(Logger logger, Tagger tagger, TagType queryType, Phrase phrase) {
		Tag result = null;

		ArrayList<Tag> tagsInPhrase = tagger.tagText(phrase.getRawText());

		ArrayList<Tag> matchingTags = new ArrayList<Tag>();
		for (Tag t : tagsInPhrase) {
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
	public static Tag getTagOfType(Logger logger, Tagger tagger, TagType queryType, Phrase phrase, int startIndex) {

		// replace up to startIndex with whitespace
		String whitespace = "";
		for (int i = 0; i < startIndex; i++) {
			whitespace += " ";
		}

		String text = whitespace + phrase.getRawText().substring(startIndex);
		ArrayList<Tag> tagsInPhrase = tagger.tagText(text);

		for (Tag t : tagsInPhrase) {
			if (t.getType() == queryType) {
				return t;
			}
		}

		return null;
	}
}
