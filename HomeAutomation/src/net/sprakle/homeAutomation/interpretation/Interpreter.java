package net.sprakle.homeAutomation.interpretation;

import net.sprakle.homeAutomation.events.Event;
import net.sprakle.homeAutomation.events.EventListener;
import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.module.ModuleManager;
import net.sprakle.homeAutomation.interpretation.module.ModuleManager.ClaimResponse;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.userInterface.speechInput.SpeechInput;
import net.sprakle.homeAutomation.userInterface.speechInput.UserSpeechRecievedEvent;
import net.sprakle.homeAutomation.userInterface.textInput.TextInput;
import net.sprakle.homeAutomation.userInterface.textInput.UserTextRecievedEvent;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.DynamicResponder;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.Response;
import net.sprakle.homeAutomation.utilities.speller.Speller;

public class Interpreter implements EventListener {

	Logger logger;
	Synthesis synth;

	// used to receive text from user
	TextInput textInput;
	SpeechInput speechInput;

	Phrase currentPhrase;

	ObjectDatabase od;

	ModuleManager moduleManager;
	Tagger tagger;

	public Interpreter(Logger logger, Synthesis synth, ObjectDatabase od, ExternalSoftware exs, Speller speller) {
		this.logger = logger;
		this.synth = synth;
		this.od = od;

		this.tagger = new Tagger(logger, synth);
		this.moduleManager = new ModuleManager(logger, synth, od, tagger, exs, speller);

		EventManager em = EventManager.getInstance(logger);
		em.addListener(EventType.USER_SPEECH_RECIEVED, this);
		em.addListener(EventType.USER_TEXT_RECIEVED, this);
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
			ClaimResponse response = moduleManager.submitForClaiming(phrase);
			if (response.notClaimed) {
				synth.speak(DynamicResponder.reply(Response.I_DIDNT_UNDERSTAND));
				return;
			}
			if (response.toManyClaimed) {
				synth.speak(DynamicResponder.reply(Response.TOO_AMBIGUOUS));
				return;
			}

			InterpretationModule target = response.module;
			target.execute(phrase);
		}
	}

	private boolean readyForNextPhrase() {
		boolean ready = false;

		// if the phrase doesn't yet exist, the program has only just been started
		if (currentPhrase == null) {
			ready = true;
		}

		return ready;
	}

	@Override
	public void call(EventType et, Event e) {
		switch (et) {
			case USER_SPEECH_RECIEVED:
				UserSpeechRecievedEvent sre = (UserSpeechRecievedEvent) e;
				recievedUserInput(sre.speech);
				break;

			case USER_TEXT_RECIEVED:
				UserTextRecievedEvent utre = (UserTextRecievedEvent) e;
				recievedUserInput(utre.speech);
				break;
			default:
				// not applicable
				break;
		}
	}
}
