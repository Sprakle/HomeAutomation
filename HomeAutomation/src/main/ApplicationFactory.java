/* Not exactly a Factory, but used to initialize all top
 * level objects required for the application to run
 */

package main;

import interaction.arduino.Arduino;
import objectDatabase.ObjectDatabase;
import speech.interpretation.Interpreter;
import speech.synthesis.Synthesis;
import timer.MainTimer;
import userInterface.speechInput.SpeechInput;
import userInterface.textInput.TextInput;
import utilities.logger.LogSource;
import utilities.logger.Logger;

@SuppressWarnings("unused")
public class ApplicationFactory {
	private static ApplicationFactory instance;

	private Logger logger;
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
		// create logger
		logger = new Logger();
		logger.log(Constants.name + " v" + Constants.version + " initiated.", LogSource.APPLICATION_EVENT, 1);

		Synthesis.speak(logger, "Initializing " + Constants.name + " version " + Constants.version);

		// initialize UI
		textInput = new TextInput(logger);
		speechInput = new SpeechInput(logger);

		// initialize arduino
		arduino = new Arduino(logger);

		// initialize object database
		objectDatabase = new ObjectDatabase(logger, arduino);

		// initialize interpretation
		interpreter = new Interpreter(logger, objectDatabase, textInput, speechInput);

		// must be called last, as it creates an infinite timer loop
		mainTimer = new MainTimer(logger);
	}
}
