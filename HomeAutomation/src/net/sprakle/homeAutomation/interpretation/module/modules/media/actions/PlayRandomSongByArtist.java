package net.sprakle.homeAutomation.interpretation.module.modules.media.actions;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.modules.media.MediaAction;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class PlayRandomSongByArtist extends MediaAction {

	public PlayRandomSongByArtist(Logger logger, MediaCentre mc, Tagger tagger) {
		super(logger, mc, tagger);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ArrayList<PhraseOutline> makePhraseOutlines() {

		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		// play random song by artist
		PhraseOutline poA = new PhraseOutline(logger, tagger, getName() + " (S)");
		poA.addTag(new Tag(TagType.PLAYBACK, "play", null, -1));
		poA.addTag(new Tag(TagType.UNKOWN_TEXT, "something", null, -1));
		poA.addTag(new Tag(TagType.POSSESSION, "owned", null, -1));
		poA.addTag(new Tag(TagType.UNKOWN_TEXT, null, null, -1));

		// play random song by artist
		PhraseOutline poB = new PhraseOutline(logger, tagger, getName() + " (T)");
		poB.addTag(new Tag(TagType.PLAYBACK, "play", null, -1));
		poB.addTag(new Tag(TagType.MEDIA, "track", null, -1));
		poB.addTag(new Tag(TagType.POSSESSION, "owned", null, -1));
		poB.addTag(new Tag(TagType.UNKOWN_TEXT, null, null, -1));

		outlines.add(poA);
		outlines.add(poB);

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

		String artist = null;

		if (ownedTagIndex != -1)
			artist = tags.get(ownedTagIndex + 1).getValue();
		else
			logger.log("Unable to determine artist from phrase", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);

		mc.playRandomTrack(artist);
	}
	@Override
	public String getName() {
		return "Play random song by specific artist";
	}

}
