package net.sprakle.homeAutomation.interpretation.module.modules.media;

import java.util.ArrayList;
import java.util.Stack;

import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.externalSoftware.software.synthesis.Synthesis;
import net.sprakle.homeAutomation.interpretation.ExecutionResult;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.AdjustVolume;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.ChangePlaybackState;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.EnqueueSong;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.IncrementalTrackChange;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.PlayRandomSong;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.PlayRandomSongByArtist;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.PlaySong;
import net.sprakle.homeAutomation.interpretation.module.modules.media.actions.SetVolume;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Media implements InterpretationModule {

    private final ArrayList<MediaAction> mediaActions;

	private final Logger logger;

    public Media(Logger logger, ExternalSoftware exs) {
		this.logger = logger;

        MediaCentre mc = (MediaCentre) exs.getSoftware(SoftwareName.MEDIA_CENTRE);
		Synthesis synth = (Synthesis) exs.getSoftware(SoftwareName.SYNTHESIS);

		mediaActions = new ArrayList<>();
		mediaActions.add(new ChangePlaybackState(logger, mc));
		mediaActions.add(new EnqueueSong(logger, mc));
		mediaActions.add(new IncrementalTrackChange(logger, mc));
		mediaActions.add(new PlayRandomSong(logger, mc));
		mediaActions.add(new PlayRandomSongByArtist(logger, mc));
		mediaActions.add(new PlaySong(logger, mc));
		mediaActions.add(new AdjustVolume(logger, mc));
		mediaActions.add(new SetVolume(logger, mc, synth));
	}

	@Override
	public boolean claim(Phrase phrase) {
		MediaAction result = selectExecution(phrase);
		return result != null;
	}

	@Override
	public ExecutionResult execute(Stack<Phrase> phrases) {
		Phrase phrase = phrases.firstElement();

		MediaAction result = selectExecution(phrase);
		result.execute(phrase);

		return ExecutionResult.COMPLETE;
	}

	private MediaAction selectExecution(Phrase phrase) {

		MediaAction maResult = null;

		// add all phrase outlines from media actions
		ArrayList<PhraseOutline> phraseOutlines = new ArrayList<>();
		for (MediaAction ma : mediaActions) {
			phraseOutlines.addAll(ma.getPhraseOutlines());
		}

		PhraseOutline poResult = phrase.matchOutlines(phraseOutlines);

		// get the media action that had the resulting phrase outline
		for (MediaAction ma : mediaActions) {
			if (ma.getPhraseOutlines().contains(poResult))
				maResult = ma;
		}

		return maResult;
	}

	@Override
	public String getName() {
        String NAME = "Media";
        return NAME;
	}

}
