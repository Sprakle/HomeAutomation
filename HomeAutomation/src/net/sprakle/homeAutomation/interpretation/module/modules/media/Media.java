package net.sprakle.homeAutomation.interpretation.module.modules.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
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

public class Media extends InterpretationModule {
	private final String NAME = "Media";

	Logger logger;
	Tagger tagger;

	ExternalSoftware exs;

	public Media(Logger logger, Tagger tagger, ExternalSoftware exs) {
		this.logger = logger;
		this.tagger = tagger;

		this.exs = exs;
	}

	@Override
	public Boolean claim(Phrase phrase) {
		boolean claim = false;

		// next song (timechange, media)
		// play music (playback, media)
		// play X (playback) + leven check

		// first tag possibilities - next/last song
		PhraseOutline posibility0 = new PhraseOutline(logger, tagger, 0);
		posibility0.addTag(new Tag(TagType.TIME_CHANGE, null, null, -1));
		posibility0.addTag(new Tag(TagType.MEDIA, null, null, -1));

		// second tag possibilities - play/pause
		PhraseOutline posibility1 = new PhraseOutline(logger, tagger, 1);
		posibility1.addTag(new Tag(TagType.PLAYBACK, null, null, -1));
		posibility1.addTag(new Tag(TagType.MEDIA, null, null, -1));

		// fourth tag possibilities - enqueue song
		PhraseOutline posibility2 = new PhraseOutline(logger, tagger, 2);
		posibility2.addTag(new Tag(TagType.PLAYBACK, null, null, -1));
		posibility2.addTag(new Tag(TagType.UNKOWN_TEXT, null, null, -1));
		posibility2.addTag(new Tag(TagType.TIME_CHANGE, null, null, -1));

		// third tag possibilities - playing specific song
		PhraseOutline posibility3 = new PhraseOutline(logger, tagger, 3);
		posibility3.addTag(new Tag(TagType.PLAYBACK, null, null, -1));
		posibility3.addTag(new Tag(TagType.UNKOWN_TEXT, null, null, -1));

		// 2D
		ArrayList<PhraseOutline> possibilities = new ArrayList<PhraseOutline>();
		possibilities.add(posibility0);
		possibilities.add(posibility1);
		possibilities.add(posibility2);
		possibilities.add(posibility3);

		PhraseOutline result = ParseHelpers.match(logger, possibilities, phrase);
		if (result != null) {
			claim = true;
		}

		return claim;
	}

	@Override
	public void execute(Phrase phrase) {
		MediaCentre mc = (MediaCentre) exs.getSoftware(SoftwareName.MEDIA_CENTRE);

		if (ParseHelpers.hasTagOfType(logger, tagger, TagType.PLAYBACK, phrase)) {
			executePlayback(phrase, mc);
		}

		if (ParseHelpers.hasTagOfType(logger, tagger, TagType.TIME_CHANGE, phrase)) {
			executeTimeChange(phrase, mc);
		}
	}

	private void executePlayback(Phrase phrase, MediaCentre mc) {
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

	private void executeTimeChange(Phrase phrase, MediaCentre mc) {
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
