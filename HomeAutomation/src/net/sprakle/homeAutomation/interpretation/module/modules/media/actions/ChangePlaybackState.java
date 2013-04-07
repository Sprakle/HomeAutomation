package net.sprakle.homeAutomation.interpretation.module.modules.media.actions;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.modules.media.MediaAction;
import net.sprakle.homeAutomation.interpretation.tagger.ParseHelpers;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ChangePlaybackState extends MediaAction {

	public ChangePlaybackState(Logger logger, MediaCentre mc, Tagger tagger) {
		super(logger, mc, tagger);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ArrayList<PhraseOutline> makePhraseOutlines() {

		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		PhraseOutline poA = new PhraseOutline(logger, tagger, getName());
		poA.addTag(new Tag(TagType.PLAYBACK, null, null, -1));
		poA.addTag(new Tag(TagType.MEDIA, null, null, -1));

		outlines.add(poA);

		return outlines;
	}

	@Override
	public void doExecute(Phrase phrase) {
		Tag commandTag = ParseHelpers.getTagOfType(logger, tagger, TagType.PLAYBACK, phrase);
		String commandString = commandTag.getValue();

		PlaybackCommand command = null;
		switch (commandString) {
			case "play":
				command = PlaybackCommand.PLAY;
				break;

			case "pause":
				command = PlaybackCommand.PAUSE;
				break;

			default:
				logger.log("Unable to choose playback command from phrase", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
				break;
		}

		mc.playbackCommand(command);
	}

	@Override
	public String getName() {
		return "Change playback state";
	}

}
