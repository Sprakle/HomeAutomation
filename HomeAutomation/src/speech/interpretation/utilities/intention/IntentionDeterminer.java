/* Used to find the intention of a phrase.
 * Possible intentions:
 * 	- Database Command
 *  - Database Request for Data
 *  - Module 
 */

package speech.interpretation.utilities.intention;

import java.util.ArrayList;
import java.util.Stack;

import objectDatabase.ObjectDatabase;
import speech.interpretation.Phrase;
import speech.interpretation.module.ModuleManager;
import speech.interpretation.utilities.intention.determiners.Determiner;
import speech.interpretation.utilities.intention.determiners.DeterminerFactory;
import speech.interpretation.utilities.tagger.Tagger;
import speech.synthesis.Synthesis;
import utilities.logger.LogSource;
import utilities.logger.Logger;
import utilities.personality.dynamicResponse.DynamicResponder;
import utilities.personality.dynamicResponse.ResponseType;

public class IntentionDeterminer {
	public static void determine(Logger logger, ObjectDatabase od, ModuleManager mm, Tagger tagger, Phrase phrase) {

		Stack<Determiner> determinerStack = DeterminerFactory.getDeterminers(logger, od, mm, tagger);

		ArrayList<Determiner> claimers = new ArrayList<Determiner>(); // list of determiners that have claimed the phrase

		// loop through each determiner checking to see if it is the correct one
		while (!determinerStack.isEmpty()) {
			Determiner d = determinerStack.pop();

			if (d.determine(phrase)) {
				claimers.add(d);
				logger.log("Determiner '" + d.getName() + "' claimed the phrase. Checking for additional claims...", LogSource.DETERMINER_INFO, 2);
			}
		}

		if (claimers.size() == 0) { // nothing claimed! complain to user
			logger.log("Phrase '" + phrase.getRawText() + "' was indeterminable", LogSource.DETERMINER_INFO, 1);

			// report to the user that the phrase was indeterminable
			String reply = DynamicResponder.reply(ResponseType.I_DIDNT_UNDERSTAND);
			Synthesis.speak(logger, reply);

		} else if (claimers.size() == 1) { // correct determiner intention found!
			logger.log("Determiner '" + claimers.get(0).getName() + "' is the only claiming determiner. Executing said determiner", LogSource.DETERMINER_INFO, 2);
			claimers.get(0).execute(phrase);

		} else if (claimers.size() > 1) { // too many claimers! complain to user
			Synthesis.speak(logger, DynamicResponder.reply(ResponseType.TOO_AMBIGUOUS));
			logger.log("Too many determiners claimed the phrase!", LogSource.DETERMINER_INFO, 2);

			// report to the user that too many determiners claimed
			String reply = DynamicResponder.reply(ResponseType.I_DIDNT_UNDERSTAND);
			Synthesis.speak(logger, reply);
		}
	}
}
