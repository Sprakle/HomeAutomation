// initializes all modules

package speech.interpretation.module;

import java.util.ArrayList;

import objectDatabase.ObjectDatabase;
import speech.interpretation.module.modules.reloading.Reloading;
import speech.interpretation.utilities.tagger.Tagger;
import utilities.logger.Logger;

public class ModuleFactory {
	static ArrayList<InterpretationModule> getModules(Logger logger, ObjectDatabase od, Tagger tagger) {
		ArrayList<InterpretationModule> modules = new ArrayList<InterpretationModule>();

		// turn modules on and off here
		// IDEA: add runtime module loading and unloading
		modules.add(new Reloading(logger, od, tagger));

		return modules;
	}
}
