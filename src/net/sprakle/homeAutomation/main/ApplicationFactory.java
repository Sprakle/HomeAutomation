/* Not exactly a Factory, but used to initialize all top
 * level objects required for the application to run
 */

package net.sprakle.homeAutomation.main;

import net.sprakle.homeAutomation.behaviour.BehaviourManager;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.synthesis.Synthesis;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interpretation.Interpreter;
import net.sprakle.homeAutomation.interpretation.module.ModuleDependencies;
import net.sprakle.homeAutomation.timer.MainTimer;
import net.sprakle.homeAutomation.userInterface.speechInput.SpeechInput;
import net.sprakle.homeAutomation.userInterface.textInput.TextInput;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.speller.Speller;
import net.sprakle.homeAutomation.utilities.time.DateParser;

@SuppressWarnings("unused")
class ApplicationFactory {
	private static ApplicationFactory instance;

    private DateParser dateParser;

    public static void createInstance() {
		if (instance == null) {
			instance = new ApplicationFactory();
		}
	}

	private ApplicationFactory() {

		// set startup speech
		String name = Config.getString("config/system/name");
		String version = Config.getString("config/system/version");
        String STARTUP_SPEECH = "Initializing " + name + " version " + version;

        Logger logger = new Logger();
		logger.log(name + " v" + version + " initiated.", LogSource.APPLICATION_EVENT, 1);

        ExternalSoftware exs = new ExternalSoftware(logger);

		Synthesis synth = (Synthesis) exs.getSoftware(SoftwareName.SYNTHESIS);
		synth.speak(STARTUP_SPEECH);

		/*
		 * BEGIN INITIALISATION
		 */

        Speller speller = new Speller(logger);

        TextInput textInput = new TextInput(logger);
        SpeechInput speechInput = new SpeechInput(logger);

        ObjectDatabase objectDatabase = new ObjectDatabase(logger, exs);

        BehaviourManager behaviourManager = new BehaviourManager(logger, objectDatabase, exs);

        Interpreter interpreter = new Interpreter(logger, new ModuleDependencies(objectDatabase, exs, speller, behaviourManager));

		/*
		 * END INITIALISATION
		 */

		synth.speak("ready");

		// must be called last, as it creates an infinite timer loop
        MainTimer mainTimer = new MainTimer(logger);
	}
}
