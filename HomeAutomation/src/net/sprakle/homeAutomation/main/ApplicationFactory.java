/* Not exactly a Factory, but used to initialize all top
 * level objects required for the application to run
 */

package net.sprakle.homeAutomation.main;

import net.sprakle.homeAutomation.interaction.arduino.Arduino;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interpretation.Interpreter;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.timer.MainTimer;
import net.sprakle.homeAutomation.userInterface.speechInput.SpeechInput;
import net.sprakle.homeAutomation.userInterface.textInput.TextInput;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

@SuppressWarnings("unused")
public class ApplicationFactory {
	private static ApplicationFactory instance;

	private final String STARTUP_SPEECH;

	private Logger logger;
	private ExternalSoftware exs;
	private Synthesis synth;
	private Arduino arduino;
	private ObjectDatabase objectDatabase;
	private MainTimer mainTimer;
	private Interpreter interpreter;
	private TextInput textInput;
	private SpeechInput speechInput;

	public static void createInstance() {
		if (instance == null) {
			instance = new ApplicationFactory();
		}
	}

	private ApplicationFactory() {

		// set startup speech
		String name = Config.getString("config/system/name");
		String version = Config.getString("config/system/version");
		STARTUP_SPEECH = "Initializing " + name + " version " + version;

		// create logger
		logger = new Logger();
		logger.log(name + " v" + version + " initiated.", LogSource.APPLICATION_EVENT, 1);

		// initialize external software, used by synth to access swift
		exs = new ExternalSoftware(logger);
		exs.initSoftware(SoftwareName.SWIFT);

		synth = new Synthesis(logger, exs);
		synth.speak(STARTUP_SPEECH);

		//exs.initSoftware(SoftwareName.MEDIA_CENTRE);

		// initialize UI
		textInput = new TextInput(logger);
		speechInput = new SpeechInput(logger);

		// initialize arduino
		arduino = new Arduino(logger, synth);

		// initialize object database
		objectDatabase = new ObjectDatabase(logger, synth, arduino);

		// initialize interpretation
		interpreter = new Interpreter(logger, synth, objectDatabase, exs);

		synth.speak("ready");

		// must be called last, as it creates an infinite timer loop
		mainTimer = new MainTimer(logger);
	}
}
