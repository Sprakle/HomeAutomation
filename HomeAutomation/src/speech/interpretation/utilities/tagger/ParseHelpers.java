/*
 * This class helps with the understanding of strings of tags.
 */

package speech.interpretation.utilities.tagger;

import java.util.ArrayList;

import speech.interpretation.Phrase;
import speech.interpretation.utilities.tagger.tags.Tag;
import speech.interpretation.utilities.tagger.tags.TagType;
import utilities.logger.Logger;

public class ParseHelpers {

	/*
	 * accepts a 2D array of tags , and and a phrase. Return's true if the phrase can be described by the array of ---
	 * EX: Array: (POWER_OPTION / SET_VALUE) (OD_OBJECT)    Phrase.rawText: "turn on the lamp"
	 * 		This will return true
	 * 
	 * EX: Array: (POWER_OPTION / SET_VALUE) (OD_OBJECT)    Phrase.rawText: "turn on the light"
	 * 		This will return true7
	 * 
	 * if there are too many of a tag, it will return false
	 */
	public static Boolean match(Logger logger, Tagger tagger, ArrayList<ArrayList<Tag>> array, Phrase phrase) {
		Boolean result = true;

		// for every 1 dimensional array AKA individual list of possibilities 
		for (ArrayList<Tag> at : array) {

			// check to see if there are any matches
			ArrayList<Tag> matches = new ArrayList<Tag>();

			// for every individual tag in the 1D array
			for (Tag t : at) {

				if (hasTagOfType(logger, tagger, t.getType(), phrase)) {
					// we found a match! add it too the list!
					matches.add(t);
				}
			}

			// how many matches did we find?
			if (matches.size() == 0) {
				// no matches!
				result = false;
				return result;
			}
		}

		// if it got this far, each 1D array had a match

		return result;
	}

	public static Boolean hasTagOfType(Logger logger, Tagger tagger, TagType queryType, Phrase phrase) {
		Boolean result = false;

		ArrayList<Tag> tags = tagger.tagText(phrase.getRawText());

		for (Tag t : tags) {
			if (t.getType() == queryType) {
				result = true;
			}
		}

		return result;
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
}
