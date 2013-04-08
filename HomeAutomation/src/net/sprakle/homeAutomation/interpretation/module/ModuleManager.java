package net.sprakle.homeAutomation.interpretation.module;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.GUI.ModuleGUI;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ModuleManager {
	HashMap<JCheckBox, InterpretationModule> modules;

	Logger logger;

	ModuleGUI moduleGUI;

	public ModuleManager(Logger logger, Synthesis synth, ObjectDatabase od, Tagger tagger, ExternalSoftware exs) {
		this.logger = logger;

		modules = ModuleFactory.getModules(logger, synth, od, tagger, exs);

		ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
		checkboxes.addAll(modules.keySet());
		moduleGUI = new ModuleGUI(checkboxes);
	}

	// checks each module for a claim on the given phrase
	public ClaimResponse submitForClaiming(Phrase phrase) {
		long startTime = System.currentTimeMillis();

		// create module arraylist containing only ones whose checkbox is selected
		ArrayList<InterpretationModule> enabledModules = new ArrayList<InterpretationModule>();
		for (JCheckBox cb : modules.keySet()) {
			if (cb.isSelected())
				enabledModules.add(modules.get(cb));
		}

		// list of all modules that claimed the phrase. Ideally only one module will be in this list
		ClaimResponse response = new ClaimResponse();
		ArrayList<InterpretationModule> claimers = new ArrayList<InterpretationModule>();
		for (InterpretationModule m : enabledModules) {
			if (m.claim(phrase)) {
				claimers.add(m);
			}
		}

		// did we get too many claims?
		if (claimers.size() > 1) {
			response.toManyClaimed = true;
		} else {
			response.toManyClaimed = false;
		}

		// was there not enough claims? (0)
		if (claimers.size() <= 0) {
			response.notClaimed = true;
		} else {
			response.notClaimed = false;
		}

		// if everything worked out well
		if (!response.notClaimed && !response.toManyClaimed) {
			response.module = claimers.get(0);
		}

		long totalTime = System.currentTimeMillis() - startTime;
		logger.log("Checked all modules for claimed in " + totalTime + " ms", LogSource.INTERPRETER_INFO, 2);

		return response;
	}

	// used to return data about claims
	public class ClaimResponse {
		public InterpretationModule module = null;
		public boolean toManyClaimed = false;
		public boolean notClaimed = false;
	}
}
