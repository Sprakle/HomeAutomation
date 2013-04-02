// initializes all modules

package net.sprakle.homeAutomation.interpretation.module;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.module.modules.objectDatabaseCommand.ObjectDatabaseCommand;
import net.sprakle.homeAutomation.interpretation.module.modules.objectDatabaseRQD.ObjectDatabaseRQD;
import net.sprakle.homeAutomation.interpretation.module.modules.reloading.Reloading;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ModuleFactory {
	static ArrayList<InterpretationModule> getModules(Logger logger, Synthesis synth, ObjectDatabase od, Tagger tagger) {
		ArrayList<InterpretationModule> modules = new ArrayList<InterpretationModule>();

		// turn modules on and off here
		// IDEA: add runtime module loading and unloading
		modules.add(new ObjectDatabaseCommand(logger, synth, od, tagger));
		modules.add(new ObjectDatabaseRQD(logger, synth, od, tagger));
		modules.add(new Reloading(logger, od, tagger));

		return modules;
	}
}
