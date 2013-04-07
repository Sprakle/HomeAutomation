package net.sprakle.homeAutomation.interpretation.module.modules.reloading;

import java.util.ArrayList;

import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.ParseHelpers;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Reloading extends InterpretationModule {
	private final String NAME = "Reloader";

	private Logger logger;
	private Tagger tagger;

	public Reloading(Logger logger, Tagger tagger) {
		this.logger = logger;
		this.tagger = tagger;
	}

	@Override
	public boolean claim(Phrase phrase) {
		boolean result = false;

		Tag match = selectExecution(phrase);
		if (match != null)
			result = true;

		return result;
	}

	@Override
	public void execute(Phrase phrase) {

		Tag match = selectExecution(phrase);

		if (match == null)
			logger.log("Unable to find interpretation module to reload", LogSource.ERROR, LogSource.INTERPRETER_INFO, 1);

		EventManager em = EventManager.getInstance(logger);
		ReloadEvent event = new ReloadEvent(match);
		em.call(EventType.RELOAD, event);
	}

	private Tag selectExecution(Phrase phrase) {
		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();
		PhraseOutline poA = new PhraseOutline(logger, tagger, getName());
		poA.addTag(new Tag(TagType.TIME_CHANGE, "restart", null, -1));
		poA.addTag(new Tag(TagType.INTERNALS, null, null, -1));
		outlines.add(poA);

		PhraseOutline poMatch = ParseHelpers.match(logger, outlines, phrase);

		if (poMatch != poA)
			return null;

		Tag reloadTag = ParseHelpers.getTagOfType(logger, tagger, TagType.TIME_CHANGE, phrase);
		Tag tagMatch = ParseHelpers.getTagOfType(logger, tagger, TagType.INTERNALS, phrase, reloadTag.getPosition());

		return tagMatch;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
