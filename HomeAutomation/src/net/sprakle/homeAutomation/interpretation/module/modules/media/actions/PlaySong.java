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

public class PlaySong extends MediaAction {

	public PlaySong(Logger logger, MediaCentre mc) {
		super(logger, mc);
	}

	@Override
	protected ArrayList<PhraseOutline> makePhraseOutlines() {

		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		PhraseOutline poA = new PhraseOutline(logger, getName());
		poA.addTag(new Tag(TagType.PLAYBACK, "play"));
		poA.addTag(new Tag(TagType.UNKOWN_TEXT, null));
		poA.addTag(new Tag(TagType.POSSESSION, "owned"));
		poA.addTag(new Tag(TagType.UNKOWN_TEXT, null));

		outlines.add(poA);

		return outlines;
	}

	@Override
	public void doExecute(Phrase phrase) {
		// get the {UNKOWN_TEXT} tag value after the {POSSESION/BY} tag
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

		mc.playTrack(title, artist);
	}

	@Override
	public String getName() {
		return "Play song";
	}

}
