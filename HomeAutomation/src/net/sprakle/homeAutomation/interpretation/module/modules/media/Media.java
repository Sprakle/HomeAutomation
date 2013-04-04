package net.sprakle.homeAutomation.interpretation.module.modules.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.ParseHelpers;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.rhythmbox.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.rhythmbox.Rhythmbox;
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

		System.out.println("MEDIA CHECK");

		// first tag possibilities
		ArrayList<Tag> posibility1 = new ArrayList<Tag>();
		posibility1.add(new Tag(TagType.TIME_CHANGE, null, -1));
		posibility1.add(new Tag(TagType.MEDIA, null, -1));

		// second tag possibilities
		ArrayList<Tag> posibility2 = new ArrayList<Tag>();
		posibility2.add(new Tag(TagType.PLAYBACK, null, -1));
		posibility2.add(new Tag(TagType.MEDIA, null, -1));

		// 2D
		ArrayList<ArrayList<Tag>> tags = new ArrayList<ArrayList<Tag>>();
		tags.add(posibility1);
		tags.add(posibility2);

		if (ParseHelpers.match(logger, tagger, tags, phrase) != null) {
			claim = true;
		}

		System.out.println("FINISHED MEDIA CHECK");

		return claim;
	}

	@Override
	public void execute(Phrase phrase) {
		if (ParseHelpers.hasTagOfType(logger, tagger, TagType.PLAYBACK, phrase)) {
			executePlayback(phrase);
		}

		if (ParseHelpers.hasTagOfType(logger, tagger, TagType.TIME_CHANGE, phrase)) {
			executeTimeChange(phrase);
		}
	}

	private void executePlayback(Phrase phrase) {
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

		Rhythmbox rhythmbox = (Rhythmbox) exs.getSoftware(SoftwareName.RHYTHMBOX);
		rhythmbox.playbackCommand(command);
	}

	private void executeTimeChange(Phrase phrase) {
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

		Rhythmbox rhythmbox = (Rhythmbox) exs.getSoftware(SoftwareName.RHYTHMBOX);
		rhythmbox.playbackCommand(command);

		// execute back twice
		if (commandString.equals("prev"))
			rhythmbox.playbackCommand(command);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
