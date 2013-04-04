/*
 * This class helps with the understanding of strings of tags.
 */

package net.sprakle.homeAutomation.interpretation.tagger;

import java.util.ArrayList;

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
	public static ArrayList<Tag> match(Logger logger, Tagger tagger, ArrayList<ArrayList<Tag>> array, Phrase phrase) {
		// for every 1 dimensional array AKA phrase outline
		for (ArrayList<Tag> at : array) {

			int tagMatches = 0;
			int required = at.size();

			int lastPosition = -1;

			// for every phrase outline
			System.out.println("checking possibility");
			for (Tag t : at) {
				if (hasTagOfType(logger, tagger, t.getType(), phrase)) {
					System.out.println("   working with tag: " + t.getFormattedAsText());

					// make sure it comes after the last tag
					Tag phraseTag = getTagOfType(logger, tagger, t.getType(), phrase);
					int position = phraseTag.getPosition();
					System.out.println("  position: " + position);
					if (position <= lastPosition)
						continue;

					lastPosition = position;
					tagMatches++;
					System.out.println("   match");
				}
			}

			if (tagMatches >= required) {
				return at;
			}
		}

		// if it got this far, there are no matches
		return null;
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
