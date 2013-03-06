package net.sprakle.homeAutomation.speech.interpretation.utilities.intention.determiners;

import java.util.Stack;

import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.speech.interpretation.module.ModuleManager;
import net.sprakle.homeAutomation.speech.interpretation.utilities.tagger.Tagger;
import net.sprakle.homeAutomation.speech.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class DeterminerFactory {
	public static Stack<Determiner> getDeterminers(Logger logger, Synthesis synth, ObjectDatabase od, ModuleManager mm, Tagger tagger) {
		Stack<Determiner> determiners = new Stack<Determiner>();

		// add all determiners
		determiners.push(new ModuleInterpreted(logger, mm));
		determiners.push(new ObjectDatabaseCommand(logger, synth, od, tagger));
		determiners.push(new ObjectDatabaseRQD(logger, synth, od, tagger));

		return determiners;
	}
}
