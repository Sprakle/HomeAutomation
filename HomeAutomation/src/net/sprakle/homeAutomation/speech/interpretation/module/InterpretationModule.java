/* has the ability to claim a phrase, and perform actions based
 * on the said phrase
 */

package net.sprakle.homeAutomation.speech.interpretation.module;

import net.sprakle.homeAutomation.speech.interpretation.Phrase;

public abstract class InterpretationModule {

	protected final String NAME;
	public InterpretationModule() {
		this.NAME = this.getClass().getSimpleName();
	}

	public abstract Boolean claim(Phrase phrase);
	public abstract void execute(Phrase phrase);
}
