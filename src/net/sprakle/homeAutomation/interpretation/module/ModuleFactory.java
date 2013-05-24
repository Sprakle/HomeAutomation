// Initialises all modules. Remember to add new modules here

package net.sprakle.homeAutomation.interpretation.module;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;

import net.sprakle.homeAutomation.behaviour.BehaviourManager;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interpretation.module.modules.deleteCurrentSong.DeleteCurrentSong;
import net.sprakle.homeAutomation.interpretation.module.modules.math.Math;
import net.sprakle.homeAutomation.interpretation.module.modules.media.Media;
import net.sprakle.homeAutomation.interpretation.module.modules.memo.Memo;
import net.sprakle.homeAutomation.interpretation.module.modules.objectDatabaseCommand.ObjectDatabaseCommand;
import net.sprakle.homeAutomation.interpretation.module.modules.reloading.Reloading;
import net.sprakle.homeAutomation.interpretation.module.modules.spelling.Spelling;
import net.sprakle.homeAutomation.interpretation.module.modules.weatherForecasting.WeatherForecasting;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.speller.Speller;

class ModuleFactory {
	static HashMap<JCheckBox, InterpretationModule> getModules(Logger logger, ModuleDependencies dep) {
		HashMap<JCheckBox, InterpretationModule> modules = new HashMap<>();

		// temporarily hold modules for easy adding of each checkbox
		ArrayList<InterpretationModule> moduleArray = new ArrayList<>();

		moduleArray.add(new ObjectDatabaseCommand(logger, dep.exs, dep.od));
		moduleArray.add(new Media(logger, dep.exs));
		moduleArray.add(new Math(logger, dep.exs));
		moduleArray.add(new Spelling(dep.exs, dep.speller));
		moduleArray.add(new WeatherForecasting(dep.exs));
		moduleArray.add(new Reloading(logger));
		moduleArray.add(new DeleteCurrentSong(logger, dep.exs));
		moduleArray.add(new Memo(logger, dep.bm, dep.exs));

		// add module and checkbox
		for (InterpretationModule im : moduleArray) {
			String label = im.getName();
			JCheckBox checkbox = new JCheckBox(label);

			modules.put(checkbox, im);
		}

		return modules;
	}
}
