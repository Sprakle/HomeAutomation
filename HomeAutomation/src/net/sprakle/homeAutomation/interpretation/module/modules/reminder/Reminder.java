package net.sprakle.homeAutomation.interpretation.module.modules.reminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import net.sprakle.homeAutomation.behaviour.BehaviourManager;
import net.sprakle.homeAutomation.interpretation.ExecutionResult;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.DateParser;

public class Reminder implements InterpretationModule {

	private Logger logger;
	private Synthesis synth;
	private BehaviourManager bm;

	public Reminder(Logger logger, Synthesis synth, BehaviourManager bm) {
		this.logger = logger;
		this.synth = synth;
		this.bm = bm;
	}

	@Override
	public boolean claim(Phrase phrase) {

		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();
		PhraseOutline poA = new PhraseOutline(logger, getName());
		poA.addMandatoryTag(new Tag(TagType.SCHEDULE, "remind"));
		poA.addMandatoryTag(new Tag(TagType.UNKOWN_TEXT, null));
		poA.negateUnxepectedTagPenalty();
		outlines.add(poA);

		PhraseOutline match = phrase.matchOutlines(logger, outlines);

		return outlines.contains(match) && DateParser.containsDate(logger, phrase);
	}

	@Override
	public ExecutionResult execute(Stack<Phrase> phrases) {
		Phrase phrase = phrases.pop();

		Date date = DateParser.parseDate(logger, phrase, true);
		synth.speak("Created reminder on " + date);

		return ExecutionResult.COMPLETE;
	}

	@Override
	public String getName() {
		return "Reminder";
	}
}