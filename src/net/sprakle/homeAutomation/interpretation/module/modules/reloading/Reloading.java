package net.sprakle.homeAutomation.interpretation.module.modules.reloading;

import java.util.ArrayList;
import java.util.Stack;

import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.interpretation.ExecutionResult;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Reloading implements InterpretationModule {

    private final Logger logger;

	public Reloading(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean claim(Phrase phrase) {
		ArrayList<PhraseOutline> outlines = new ArrayList<>();
		PhraseOutline poA = new PhraseOutline(getName());
		poA.addMandatoryTag(new Tag(TagType.TIME_CHANGE, "restart"));
		poA.addMandatoryTag(new Tag(TagType.INTERNALS, null));
		outlines.add(poA);

		PhraseOutline poMatch = phrase.matchOutlines(outlines);

        return poMatch == poA;
	}

	// TODO: announce through synth

	@Override
	public ExecutionResult execute(Stack<Phrase> phrases) {
		Phrase phrase = phrases.firstElement();

		Tag[] sequenceRequest = new Tag[2];
		sequenceRequest[0] = new Tag(TagType.TIME_CHANGE, "restart");
		sequenceRequest[1] = new Tag(TagType.INTERNALS, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);
		Tag internalsTag = sequence[1];

		EventManager em = EventManager.getInstance(logger);
		ReloadEvent event = new ReloadEvent(internalsTag);
		em.call(EventType.RELOAD, event);

		return ExecutionResult.COMPLETE;
	}

	@Override
	public String getName() {
        String NAME = "Reloader";
        return NAME;
	}
}
