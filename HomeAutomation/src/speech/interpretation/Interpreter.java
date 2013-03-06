package speech.interpretation;

import java.nio.file.Path;
import java.nio.file.Paths;

import objectDatabase.ObjectDatabase;
import speech.interpretation.module.ModuleManager;
import speech.interpretation.utilities.intention.IntentionDeterminer;
import speech.interpretation.utilities.tagger.Tagger;
import speech.synthesis.Synthesis;
import userInterface.speechInput.SpeechInput;
import userInterface.speechInput.SpeechInputObserver;
import userInterface.textInput.TextInput;
import userInterface.textInput.TextInputObserver;
import utilities.logger.LogSource;
import utilities.logger.Logger;

public class Interpreter implements TextInputObserver, SpeechInputObserver {

	private final Path tagfilePath = Paths.get("resources/tagger/main.tagList");

	Logger logger;
	Synthesis synth;

	// used to receive text from user
	TextInput textInput;
	SpeechInput speechInput;

	Phrase currentPhrase;

	ObjectDatabase od;

	ModuleManager moduleManager;
	Tagger tagger;

	public Interpreter(Logger logger, Synthesis synth, ObjectDatabase od, TextInput textInput, SpeechInput speechInput) {
		this.logger = logger;
		this.synth = synth;
		this.od = od;

		this.tagger = new Tagger(logger, synth, tagfilePath);
		this.moduleManager = new ModuleManager(logger, od, tagger);

		this.textInput = textInput;
		this.speechInput = speechInput;
		textInput.addObserver(this);
		speechInput.addObserver(this);
	}

	@Override
	public void textInputUpdate(String input) {
		recievedUserInput(input);
	}

	@Override
	public void speechInputUpdate(String input) {
		recievedUserInput(input);
	}

	private void recievedUserInput(String input) {
		if (readyForNextPhrase()) {
			logger.log("Accepted input: '" + input + "'", LogSource.INTERPRETER_INFO, 1);

			// STEP ONE:
			// convert text to phrase
			Phrase phrase = new Phrase(logger, tagger, input);

			// STEP TWO:
			/* get the intention of the phrase. If an intention was found,
			 * the determiner will execute the intention
			 */
			IntentionDeterminer.determine(logger, synth, od, moduleManager, tagger, phrase);
		}
	}

	private Boolean readyForNextPhrase() {
		Boolean ready = false;

		// if the phrase doesn't yet exist, the program has only just been started
		if (currentPhrase == null) {
			ready = true;
		} else {

			// the phrase may also simply be set to 'complete'
			PipelineStatus status = currentPhrase.getPipelineStatus();
			if (status == PipelineStatus.COMPLETE) {
				ready = true;
			}
		}

		return ready;
	}
}
