package speech.interpretation.utilities.intention.determiners;

import java.util.Stack;

import objectDatabase.ObjectDatabase;
import speech.interpretation.module.ModuleManager;
import speech.interpretation.utilities.tagger.Tagger;
import utilities.logger.Logger;

public class DeterminerFactory {
	public static Stack<Determiner> getDeterminers(Logger logger, ObjectDatabase od, ModuleManager mm, Tagger tagger) {
		Stack<Determiner> determiners = new Stack<Determiner>();

		// add all determiners
		determiners.push(new ModuleInterpreted(logger, mm));
		determiners.push(new ObjectDatabaseCommand(logger, od, tagger));
		determiners.push(new ObjectDatabaseRQD(logger, od, tagger));

		return determiners;
	}
}
