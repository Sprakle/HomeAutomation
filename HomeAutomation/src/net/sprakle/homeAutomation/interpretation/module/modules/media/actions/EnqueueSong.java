package net.sprakle.homeAutomation.interpretation.module.modules.media.actions;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.modules.media.MediaAction;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class EnqueueSong extends MediaAction {

	public EnqueueSong(Logger logger, MediaCentre mc) {
		super(logger, mc);
	}

	@Override
	protected ArrayList<PhraseOutline> makePhraseOutlines() {

		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		PhraseOutline poA = new PhraseOutline(logger, getName() + " (title only)");
		poA.addTag(new Tag(TagType.PLAYBACK, "play"));
		poA.addTag(new Tag(TagType.UNKOWN_TEXT, null));
		poA.addTag(new Tag(TagType.TIME_CHANGE, "next"));

		PhraseOutline poB = new PhraseOutline(logger, getName() + " (title + artist)");
		poB.addTag(new Tag(TagType.PLAYBACK, "play"));
		poB.addTag(new Tag(TagType.UNKOWN_TEXT, null));
		poB.addTag(new Tag(TagType.POSSESSION, "owned"));
		poB.addTag(new Tag(TagType.UNKOWN_TEXT, null));
		poB.addTag(new Tag(TagType.TIME_CHANGE, "next"));

		outlines.add(poA);
		outlines.add(poB);

		return outlines;
	}

	@Override
	public void doExecute(Phrase phrase) {
		Tag byTag = phrase.getTag(new Tag(TagType.POSSESSION, "by"));
		if (byTag == null)
			executeTitleOnly(phrase);
		else
			executeTitleAndArtist(phrase);
	}

	private void executeTitleOnly(Phrase phrase) {
		String title = null;

		Tag playTag = phrase.getTag(new Tag(TagType.PLAYBACK, "play"));
		Tag titleTag = phrase.getRelativeTag(playTag, new Tag(TagType.UNKOWN_TEXT, null), 1);
		title = titleTag.getValue();

		mc.enqueueTrack(title, null);
	}

	private void executeTitleAndArtist(Phrase phrase) {
		ArrayList<Tag> tags = phrase.getTags();

		int ownedTagIndex = -1;
		for (Tag t : tags) {

			if (t.getType() == TagType.POSSESSION && t.getValue().equals("owned"))
				ownedTagIndex = tags.indexOf(t);
		}

		Tag titleTag = tags.get(ownedTagIndex - 1);
		Tag artistTag = tags.get(ownedTagIndex + 1);

		String title = null;
		String artist = null;

		if (titleTag != null & artistTag != null) {
			title = titleTag.getValue();
			artist = artistTag.getValue();
		} else {
			logger.log("Unable to determine track from phrase", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
		}

		mc.enqueueTrack(title, artist);
	}

	@Override
	public String getName() {
		return "Enqueue song";
	}

}
