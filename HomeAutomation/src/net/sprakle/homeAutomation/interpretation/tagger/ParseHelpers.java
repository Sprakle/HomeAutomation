/*
 * This class helps with the understanding of strings of tags.
 */

package net.sprakle.homeAutomation.interpretation.tagger;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sprakle.homeAutomation.interpretation.Phrase;
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

		PhraseOutline match = matches.lastEntry().getValue();

		// get the most confident entry
		return match;
	}
}
