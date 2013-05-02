package net.sprakle.homeAutomation.interpretation.module.modules.media.actions;

import java.util.ArrayList;

import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.modules.media.MediaAction;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class AdjustVolume extends MediaAction {

	public AdjustVolume(Logger logger, MediaCentre mc) {
		super(logger, mc);
	}

	@Override
	protected void doExecute(Phrase phrase) {
		Tag setterTag = phrase.getTag(new Tag(TagType.SETTER, null));
		String setter = setterTag.getValue();

		switch (setter) {
			case "+":
				mc.changeVolume(0.2);
				break;

			case "-":
				mc.changeVolume(-0.2);
				break;
		}
	}

	@Override
	protected ArrayList<PhraseOutline> makePhraseOutlines() {
		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		PhraseOutline relativeIncrement = new PhraseOutline(logger, getName());
		relativeIncrement.addMandatoryTag(new Tag(TagType.SETTER, "+"));
		relativeIncrement.addMandatoryTag(new Tag(TagType.MEDIA, "track"));

		PhraseOutline relativeDecrement = new PhraseOutline(logger, getName());
		relativeDecrement.addMandatoryTag(new Tag(TagType.SETTER, "-"));
		relativeDecrement.addMandatoryTag(new Tag(TagType.MEDIA, "track"));

		relativeIncrement.addNeutralTag(new Tag(TagType.AUDIO, "volume"));
		relativeDecrement.addNeutralTag(new Tag(TagType.AUDIO, "volume"));
		outlines.add(relativeIncrement);
		outlines.add(relativeDecrement);
		return outlines;
	}

	@Override
	public String getName() {
		return "Media centre relative volume adjustment";
	}

}
