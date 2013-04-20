package net.sprakle.homeAutomation.interpretation.module.modules.media.actions;

import java.util.ArrayList;

import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.modules.media.MediaAction;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class IncrementalTrackChange extends MediaAction {

	public IncrementalTrackChange(Logger logger, MediaCentre mc) {
		super(logger, mc);
	}

	@Override
	protected ArrayList<PhraseOutline> makePhraseOutlines() {

		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		PhraseOutline poA = new PhraseOutline(logger, getName());
		poA.addMandatoryTag(new Tag(TagType.TIME_CHANGE, null));
		poA.addMandatoryTag(new Tag(TagType.MEDIA, "track"));

		PhraseOutline poB = new PhraseOutline(logger, getName());
		poB.addMandatoryTag(new Tag(TagType.PLAYBACK, "play"));
		poB.addMandatoryTag(new Tag(TagType.TIME_CHANGE, null));
		poB.addMandatoryTag(new Tag(TagType.MEDIA, "track"));

		outlines.add(poA);
		outlines.add(poB);

		return outlines;
	}

	@Override
	public void doExecute(Phrase phrase) {
		Tag tag = phrase.getTag(new Tag(TagType.TIME_CHANGE, null));
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
		return "Incremental track change";
	}

}
