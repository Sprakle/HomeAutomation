package net.sprakle.homeAutomation.interpretation.module.modules.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.ChangePlaybackState;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.EnqueueSong;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.IncrementalTrackChange;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.PlayRandomSong;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.PlayRandomSongByArtist;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.PlaySong;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Media extends InterpretationModule {
	private final String NAME = "Media";

	private ArrayList<MediaAction> mediaActions;

	private Logger logger;
	private MediaCentre mc;

	public Media(Logger logger, ExternalSoftware exs) {
		this.logger = logger;

		mc = (MediaCentre) exs.getSoftware(SoftwareName.MEDIA_CENTRE);

		mediaActions = new ArrayList<MediaAction>();
		mediaActions.add(new ChangePlaybackState(logger, mc));
		mediaActions.add(new EnqueueSong(logger, mc));
		mediaActions.add(new IncrementalTrackChange(logger, mc));
		mediaActions.add(new PlayRandomSong(logger, mc));
		mediaActions.add(new PlayRandomSongByArtist(logger, mc));
		mediaActions.add(new PlaySong(logger, mc));
	}

	@Override
	public boolean claim(Phrase phrase) {
		MediaAction result = selectExecution(phrase);
		return result != null;
	}

	@Override
	public void execute(Phrase phrase) {

		MediaAction result = selectExecution(phrase);
		result.execute(phrase);
	}

	private MediaAction selectExecution(Phrase phrase) {

		MediaAction maResult = null;

		// add all phrase outlines from media actions
		ArrayList<PhraseOutline> phraseOutlines = new ArrayList<PhraseOutline>();
		for (MediaAction ma : mediaActions) {
			phraseOutlines.addAll(ma.getPhraseOutlines());
		}

		PhraseOutline poResult = phrase.matchOutlines(logger, phraseOutlines);

		// get the media action that had the resulting phrase outline
		for (MediaAction ma : mediaActions) {
			if (ma.getPhraseOutlines().contains(poResult))
				maResult = ma;
		}

		return maResult;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
