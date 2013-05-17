package net.sprakle.homeAutomation.interpretation;

import java.util.List;
import java.util.Stack;

import net.sprakle.homeAutomation.behaviour.BehaviourManager;
import net.sprakle.homeAutomation.events.Event;
import net.sprakle.homeAutomation.events.EventListener;
import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.module.ModuleManager;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.userInterface.speechInput.UserSpeechRecievedEvent;
import net.sprakle.homeAutomation.userInterface.textInput.UserTextRecievedEvent;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.speller.Speller;

public class Interpreter implements EventListener {

	private final Logger logger;

	// every time a phrase is made, it is added to the stack, until a the user-computer interaction is complete
	private Stack<Phrase> phrases;

	// if a module's requires additional user interaction after execution, the next phrase will be automatically sent to it 
	private InterpretationModule prevModule;
	private boolean additionalInteractionRequired;

	private final ModuleManager moduleManager;
	private final Tagger tagger;

	public Interpreter(Logger logger, ObjectDatabase od, ExternalSoftware exs, Speller speller) {
		this.logger = logger;

		this.tagger = new Tagger(logger);
		this.moduleManager = new ModuleManager(logger, od, exs, speller);

		phrases = new Stack<>();

		EventManager em = EventManager.getInstance(logger);
		em.addListener(EventType.USER_SPEECH_RECIEVED, this);
		em.addListener(EventType.USER_TEXT_RECIEVED, this);
	}

	private void recievedUserInput(String input) {
		logger.log("Accepted input: '" + input + "'", LogSource.INTERPRETER_INFO, 1);

		// convert text to a phrase, tagging it
		Phrase phrase = new Phrase(logger, tagger, input);
		logger.log("Tagged input: " + phrase, LogSource.TAGGER_INFO, 3);

		// if the last module requires addition user interaction, automatically execute it
		if (additionalInteractionRequired) {
			executeModule(prevModule, phrase);
			return;
		}

		// each interpretation module will be checked for a claim
		List<InterpretationModule> result = moduleManager.submitForClaiming(phrase);

		if (result.size() < 1) {
			logger.log("Ignoring phrase, as no modules were able to accept it", LogSource.INTERPRETER_INFO, 2);
			return;
		}

		if (result.size() > 1) {
			logger.log("Multiple modules claimed the phrase. Modules should be made more specific", LogSource.WARNING, LogSource.INTERPRETER_INFO, 1);
		}

		InterpretationModule target = result.get(0);
		executeModule(target, phrase);
	}

	private void executeModule(InterpretationModule target, Phrase phrase) {
		prevModule = target;

		phrases.push(phrase);
		ExecutionResult result = target.execute(phrases);

		// if the user-computer interaction is complete, everything is reset
		if (result == ExecutionResult.COMPLETE) {
			logger.log("User-system interaction complete after " + phrases.size() + " interactions", LogSource.INTERPRETER_INFO, 2);
			phrases = new Stack<>();
			additionalInteractionRequired = false;
			return;
		} else {
			logger.log("Additional user-system interaction required", LogSource.INTERPRETER_INFO, 2);
			additionalInteractionRequired = true;
		}
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
