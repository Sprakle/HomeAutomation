/* has the ability to claim a phrase, and perform actions based
 * on the said phrase
 */

package speech.interpretation.module;

import speech.interpretation.Phrase;

public interface InterpretationModule {
	public Boolean claim(Phrase phrase);

	public void execute(Phrase phrase);
}
