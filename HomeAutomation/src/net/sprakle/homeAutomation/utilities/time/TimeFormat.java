package net.sprakle.homeAutomation.utilities.time;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;

/**
 * A way a user can format a specific unit of time. Ex:
 * 
 * 10 30 is a NumberNumber format
 * 
 * @author ben
 * 
 */
public interface TimeFormat {

	/**
	 * If this outline is matched against a phrase, this timeFormat will be
	 * called upon
	 * 
	 * This MUST return the same reference each time called, not create a new
	 * outline
	 * 
	 * @return
	 */
	public PhraseOutline getOutline();

	/**
	 * Returns the number if this unit of time that has past since the next
	 * biggest unit of time. Ex:
	 * 
	 * Phrase: "quarter to 7" passed to a minute format returns -25
	 * 
	 * @param phrase
	 * @return
	 */
	public int getTime(Phrase phrase);

}
