package net.sprakle.homeAutomation.interpretation.module.modules.media.actions;

import java.util.ArrayList;

import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.modules.media.MediaAction;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class EnqueueSong extends MediaAction {

	public EnqueueSong(Logger logger, MediaCentre mc) {
		super(logger, mc);
	}

	@Override
	protected ArrayList<PhraseOutline> makePhraseOutlines() {

		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		PhraseOutline poA = new PhraseOutline(logger, getName() + " (title only)");
		poA.addMandatoryTag(new Tag(TagType.PLAYBACK, "play"));
		poA.addMandatoryTag(new Tag(TagType.UNKOWN_TEXT, null));
		poA.addMandatoryTag(new Tag(TagType.TIME_CHANGE, "next"));

		PhraseOutline poB = new PhraseOutline(logger, getName() + " (title + artist)");
		poB.addMandatoryTag(new Tag(TagType.PLAYBACK, "play"));
		poB.addMandatoryTag(new Tag(TagType.UNKOWN_TEXT, null));
		poB.addMandatoryTag(new Tag(TagType.POSSESSION, "owned"));
		poB.addMandatoryTag(new Tag(TagType.UNKOWN_TEXT, null));
		poB.addMandatoryTag(new Tag(TagType.TIME_CHANGE, "next"));

		outlines.add(poA);
		outlines.add(poB);

		return outlines;
	}

	@Override
	public void doExecute(Phrase phrase) {
		Tag byTag = phrase.getTag(new Tag(TagType.POSSESSION, "owned"));
		if (byTag == null)
			executeTitleOnly(phrase);
		else
			executeTitleAndArtist(phrase);
	}

	private void executeTitleOnly(Phrase phrase) {
		Tag[] sequenceRequest = new Tag[2];
		sequenceRequest[0] = new Tag(TagType.PLAYBACK, "play");
		sequenceRequest[1] = new Tag(TagType.UNKOWN_TEXT, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);

		Tag titleTag = sequence[1];
		String title = titleTag.getValue();

		mc.enqueueTrack(title, null);
	}

	private void executeTitleAndArtist(Phrase phrase) {
		Tag[] sequenceRequest = new Tag[3];
		sequenceRequest[0] = new Tag(TagType.UNKOWN_TEXT, null);
		sequenceRequest[1] = new Tag(TagType.POSSESSION, "owned");
		sequenceRequest[2] = new Tag(TagType.UNKOWN_TEXT, null);

		Tag[] sequence = phrase.getTagSequence(sequenceRequest);
		Tag titleTag = sequence[0];
		Tag artistTag = sequence[2];

		mc.enqueueTrack(titleTag.getValue(), artistTag.getValue());
	}

	@Override
	public String getName() {
		return "Enqueue song";
	}

}
