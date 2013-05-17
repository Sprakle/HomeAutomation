/* has the ability to claim a phrase, and perform actions based
 * on the said phrase
 */

package net.sprakle.homeAutomation.interpretation.module;

import java.util.Stack;

import net.sprakle.homeAutomation.interpretation.ExecutionResult;
import net.sprakle.homeAutomation.interpretation.Phrase;

public interface InterpretationModule {
	public boolean claim(Phrase phrase);
	public ExecutionResult execute(Stack<Phrase> phrases);

	public String getName();
}
