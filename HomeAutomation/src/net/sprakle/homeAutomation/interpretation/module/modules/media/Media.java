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
import net.sprakle.homeAutomation.interpretation.tagger.ParseHelpers;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.logger.Logger;

// IDEA: run claimers each on their own thread

public class Media extends InterpretationModule {
	private final String NAME = "Media";

	private ArrayList<MediaAction> mediaActions;

	private Logger logger;
	private Tagger tagger;
	private MediaCentre mc;

	public Media(Logger logger, Tagger tagger, ExternalSoftware exs) {
		this.logger = logger;
		this.tagger = tagger;

		exs.initSoftware(SoftwareName.MEDIA_CENTRE);
		mc = (MediaCentre) exs.getSoftware(SoftwareName.MEDIA_CENTRE);

		mediaActions = new ArrayList<MediaAction>();
		mediaActions.add(new ChangePlaybackState(logger, mc, tagger));
		mediaActions.add(new EnqueueSong(logger, mc, tagger));
		mediaActions.add(new IncrementalTrackChange(logger, mc, tagger));
		mediaActions.add(new PlayRandomSong(logger, mc, tagger));
		mediaActions.add(new PlayRandomSongByArtist(logger, mc, tagger));
		mediaActions.add(new PlaySong(logger, mc, tagger));
	}

	// TODO: check project for accidentally autoboxed primitives

	@Override
	public Boolean claim(Phrase phrase) {
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

		PhraseOutline poResult = ParseHelpers.match(logger, phraseOutlines, phrase);

		// get the media action that had the resulting phrase outline
		for (MediaAction ma : mediaActions) {
			if (ma.getPhraseOutlines().contains(poResult))
				maResult = ma;
		}

		return maResult;
	}

	private void playbackCommand(Phrase phrase, MediaCentre mc) {
		Tag tag = ParseHelpers.getTagOfType(logger, tagger, TagType.PLAYBACK, phrase);
		String commandString = tag.getValue();

		PlaybackCommand command = null;
		switch (commandString) {
			case "play":
				command = PlaybackCommand.PLAY;
				break;

			case "pause":
				command = PlaybackCommand.PAUSE;
				break;
		}

		mc.playbackCommand(command);
	}

	private void timeChangeCommand(Phrase phrase, MediaCentre mc) {
		Tag tag = ParseHelpers.getTagOfType(logger, tagger, TagType.TIME_CHANGE, phrase);
		String commandString = tag.getValue();

		PlaybackCommand command = null;
		switch (commandString) {
			case "next":
				command = PlaybackCommand.NEXT;
				break;

			case "prev":
				command = PlaybackCommand.BACK;
				break;

			case "restart":
				command = PlaybackCommand.BACK;
				break;

			case "random":
				break;
		}

		mc.playbackCommand(command);

		// execute back twice
		if (commandString.equals("prev"))
			mc.playbackCommand(command);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
