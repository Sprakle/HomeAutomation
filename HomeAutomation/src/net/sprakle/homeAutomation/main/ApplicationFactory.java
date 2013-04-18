/* Not exactly a Factory, but used to initialize all top
 * level objects required for the application to run
 */

package net.sprakle.homeAutomation.main;

import net.sprakle.homeAutomation.behaviour.BehaviourManager;
import net.sprakle.homeAutomation.interaction.arduino.Arduino;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interaction.weather.InternetWeather;
import net.sprakle.homeAutomation.interpretation.Interpreter;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.timer.MainTimer;
import net.sprakle.homeAutomation.userInterface.speechInput.SpeechInput;
import net.sprakle.homeAutomation.userInterface.textInput.TextInput;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.speller.Speller;

@SuppressWarnings("unused")
public class ApplicationFactory {
	private static ApplicationFactory instance;

	private final String STARTUP_SPEECH;

	private Logger logger;
	private ExternalSoftware exs;
	private Speller speller;
	private Synthesis synth;
	private Arduino arduino;
	private ObjectDatabase objectDatabase;
	private InternetWeather iWeather;
	private MainTimer mainTimer;
	private Interpreter interpreter;
	private BehaviourManager behaviourManager;
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

		logger = new Logger();
		logger.log(name + " v" + version + " initiated.", LogSource.APPLICATION_EVENT, 1);

		exs = new ExternalSoftware(logger);
		exs.initSoftware(SoftwareName.SWIFT);

		speller = new Speller(logger);

		synth = new Synthesis(logger, exs);
		synth.speak(STARTUP_SPEECH);

		// TODO: make external software initialize software automatically
		exs.initSoftware(SoftwareName.MEDIA_CENTRE);

		textInput = new TextInput(logger);
		speechInput = new SpeechInput(logger);

		arduino = new Arduino(logger, synth);

		objectDatabase = new ObjectDatabase(logger, synth, arduino);

		iWeather = new InternetWeather(logger);

		interpreter = new Interpreter(logger, synth, objectDatabase, exs, speller, iWeather);

		behaviourManager = new BehaviourManager(logger, objectDatabase, exs);

		synth.speak("ready");

		// must be called last, as it creates an infinite timer loop
		mainTimer = new MainTimer(logger);
	}
}
