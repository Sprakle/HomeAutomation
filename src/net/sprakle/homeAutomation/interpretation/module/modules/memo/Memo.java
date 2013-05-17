package net.sprakle.homeAutomation.interpretation.module.modules.memo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import net.sprakle.homeAutomation.behaviour.Behaviour;
import net.sprakle.homeAutomation.behaviour.BehaviourDefinition;
import net.sprakle.homeAutomation.behaviour.BehaviourManager;
import net.sprakle.homeAutomation.behaviour.actions.ActionDefinition;
import net.sprakle.homeAutomation.behaviour.triggers.TriggerDefinition;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interpretation.ExecutionResult;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.perspective.PerspectiveConverter;
import net.sprakle.homeAutomation.utilities.time.DateParser;

public class Memo implements InterpretationModule {

	private final Logger logger;
	private final BehaviourManager bm;
	private final PerspectiveConverter pConverter;
	private final Tagger tagger;

	@SuppressWarnings("UnusedParameters")
    private Memo(Logger logger, Tagger tagger, BehaviourManager bm, ExternalSoftware exs) {
		this.logger = logger;
		this.tagger = tagger;
		this.bm = bm;

		pConverter = new PerspectiveConverter(logger);
	}

	@Override
	public boolean claim(Phrase phrase) {

		ArrayList<PhraseOutline> outlines = new ArrayList<>();
		PhraseOutline poA = new PhraseOutline(getName());
		poA.addMandatoryTag(new Tag(TagType.SCHEDULE, "memo"));
		poA.addMandatoryTag(new Tag(TagType.UNKOWN_TEXT, null));
		poA.negateUnxepectedTagPenalty();
		outlines.add(poA);

		PhraseOutline match = phrase.matchOutlines(outlines);

		return outlines.contains(match) && DateParser.containsDate(logger, phrase);
	}

	@Override
	public ExecutionResult execute(Stack<Phrase> phrases) {
		Phrase phrase = phrases.pop();

		// isolate part of user input defining the memo itself (minus preamble and date)
		String sentence = phrase.getRawText();
		sentence = removeMemo(phrase, sentence);
		sentence = DateParser.removeDate(logger, tagger, sentence);

		// convert perspective of sentence
		sentence = pConverter.convert(sentence, 1, 2);

		// make behaviour with a time trigger and a reminder action (repeats until acknowledged)
		Date date = DateParser.parseDate(logger, phrase, true);
		String millisString = Long.toString(date.getTime());

		// behaviour
		String name = "Memo alert";
		String description = "Alert the user of a memo";
		BehaviourDefinition bDef = new BehaviourDefinition(name, description, 1000);

		// trigger
		TriggerDefinition tDef = new TriggerDefinition("time");
		tDef.elements.put("parse_mode", "milliseconds");
		tDef.elements.put("time", millisString);
		bDef.triggers.add(tDef);

		// action
		ActionDefinition aDef = new ActionDefinition("speak");
		aDef.elements.put("speech", sentence);
		bDef.triggerStartActions.add(aDef);

		// add to behaviours
		Behaviour behaviour = bm.createBehaviour(bDef, true);
		bm.addBehaviour(behaviour);

		return ExecutionResult.COMPLETE;
	}
	// remove the part of a phrase that signifies taking a memo. ex: "remind me to" or "take a memo"
	private String removeMemo(Phrase phrase, String rawText) {

		Tag memoTag = phrase.getTag(new Tag(TagType.SCHEDULE, "memo"));
		String memoTagRawText = memoTag.getOriginalTrigger();

		int memoStart = rawText.indexOf(memoTagRawText);
		if (memoStart == -1)
			return rawText;

		int memoEnd = memoStart + memoTagRawText.length();

		// delete all before memo
		return rawText.substring(memoEnd);
	}

	@Override
	public String getName() {
		return "Memo";
	}

	public static void main(String[] args) {
		Logger logger = new Logger();
		Tagger tagger = new Tagger(logger);
		ExternalSoftware exs = new ExternalSoftware(logger);
		ObjectDatabase od = new ObjectDatabase(logger, exs);
		BehaviourManager bm = new BehaviourManager(logger, od, exs);

		Memo memo = new Memo(logger, tagger, bm, exs);

		Phrase phrase = new Phrase(logger, tagger, "remind me to do my homework on sunday at 5 30");
		Stack<Phrase> phrases = new Stack<>();
		phrases.push(phrase);

		memo.execute(phrases);
	}
}