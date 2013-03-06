// initializes all modules

package net.sprakle.homeAutomation.speech.interpretation.module;

import java.util.ArrayList;

import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.speech.interpretation.module.modules.reloading.Reloading;
import net.sprakle.homeAutomation.speech.interpretation.utilities.tagger.Tagger;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class ModuleFactory {
	static ArrayList<InterpretationModule> getModules(Logger logger, ObjectDatabase od, Tagger tagger) {
		ArrayList<InterpretationModule> modules = new ArrayList<InterpretationModule>();

		// turn modules on and off here
		// IDEA: add runtime module loading and unloading
		modules.add(new Reloading(logger, od, tagger));

		return modules;
	}
}
