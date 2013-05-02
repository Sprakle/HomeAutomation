package net.sprakle.homeAutomation.interpretation.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBox;

import net.sprakle.homeAutomation.behaviour.BehaviourManager;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.GUI.ModuleGUI;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.speller.Speller;

public class ModuleManager {
	private HashMap<JCheckBox, InterpretationModule> modules;

	private Logger logger;

	public ModuleManager(Logger logger, Synthesis synth, ObjectDatabase od, Tagger tagger, ExternalSoftware exs, Speller speller, BehaviourManager bm) {
		this.logger = logger;

		modules = ModuleFactory.getModules(logger, synth, od, tagger, exs, speller, bm);

		ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
		checkboxes.addAll(modules.keySet());
		new ModuleGUI(checkboxes);
	}

	/**
	 * Checks all InterpretationModules for a claim on the given phrase, and
	 * returns claiming ones
	 * 
	 * @param phrase
	 * @return
	 */
	public List<InterpretationModule> submitForClaiming(Phrase phrase) {
		long startTime = System.currentTimeMillis();

		// create module arraylist containing only ones whose checkbox is selected
		ArrayList<InterpretationModule> enabledModules = new ArrayList<InterpretationModule>();
		for (JCheckBox cb : modules.keySet()) {
			if (cb.isSelected())
				enabledModules.add(modules.get(cb));
		}

		ArrayList<InterpretationModule> claimers = getClaimers(enabledModules, phrase);

		long totalTime = System.currentTimeMillis() - startTime;
		logger.log("Checked all modules for claims in " + totalTime + " ms. Result: " + getModuleNames(claimers), LogSource.INTERPRETER_INFO, 2);

		return claimers;
	}

	private ArrayList<InterpretationModule> getClaimers(ArrayList<InterpretationModule> modules, final Phrase phrase) {
		final ArrayList<InterpretationModule> resultingClaimers = new ArrayList<InterpretationModule>();

		final ArrayList<ClaimerThread> threads = new ArrayList<ClaimerThread>();

		// create a thread for each claimer
		for (InterpretationModule m : modules) {
			ClaimerThread thread = new ClaimerThread(phrase, m);
			threads.add(thread);
		}

		// start each thread
		for (ClaimerThread t : threads) {
			t.start();
		}

		// wait for each
		for (ClaimerThread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (t.getResult() == true)
				resultingClaimers.add(t.getModule());
		}

		return resultingClaimers;
	}

	private String getModuleNames(List<InterpretationModule> modules) {
		String names = "";

		if (modules.size() == 0)
			return "";

		for (InterpretationModule module : modules)
			names += module.getName() + ", ";

		// remove last to characters
		names = names.substring(0, names.length() - 2);
		return names;
	}

	private class ClaimerThread extends Thread {

		private Phrase phrase;
		private InterpretationModule module;
		private boolean result;

		public ClaimerThread(Phrase phrase, InterpretationModule module) {
			this.phrase = phrase;
			this.module = module;
		}

		@Override
		public synchronized void run() {
			if (module.claim(phrase))
				result = true;
		}

		public boolean getResult() {
			return result;
		}

		public InterpretationModule getModule() {
			return module;
		}
	}
}
