package net.sprakle.homeAutomation.interpretation.module.modules.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public abstract class MediaAction {

	protected Logger logger;
	protected MediaCentre mc;
	protected Tagger tagger;

	protected ArrayList<PhraseOutline> phraseOutlines;

	public MediaAction(Logger logger, MediaCentre mc, Tagger tagger) {
		this.logger = logger;
		this.mc = mc;
		this.tagger = tagger;

		phraseOutlines = makePhraseOutlines();
	}

	public void execute(Phrase phrase) {
		logger.log("Preforming Media Action: " + getName(), LogSource.EXTERNAL_SOFTWARE, 2);
		doExecute(phrase);
	}

	public ArrayList<PhraseOutline> getPhraseOutlines() {
		return phraseOutlines;
	}

	protected abstract void doExecute(Phrase phrase);
	protected abstract ArrayList<PhraseOutline> makePhraseOutlines();
	public abstract String getName();
}
