/* has the ability to claim a phrase, and perform actions based
 * on the said phrase
 */

package net.sprakle.homeAutomation.interpretation.module;

import net.sprakle.homeAutomation.interpretation.Phrase;

public abstract class InterpretationModule {

	public abstract boolean claim(Phrase phrase);
	public abstract void execute(Phrase phrase);

	public abstract String getName();
}
