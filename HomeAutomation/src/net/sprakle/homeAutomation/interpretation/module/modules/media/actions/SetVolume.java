package net.sprakle.homeAutomation.interpretation.module.modules.media.actions;

import java.util.ArrayList;

import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.externalSoftware.software.synthesis.Synthesis;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.modules.media.MediaAction;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.DynamicResponder;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.Response;

public class SetVolume extends MediaAction {

	private Synthesis synth;

	public SetVolume(Logger logger, MediaCentre mc, Synthesis synth) {
		super(logger, mc);

		this.synth = synth;
	}

	@Override
	protected void doExecute(Phrase phrase) {
		double set = getAbsoluteSet(phrase);

		if (set < 0 || set > 1) {
			String couldNot = DynamicResponder.reply(Response.I_COULD_NOT);
			synth.speak(couldNot + " set the volume to that level. I only accept integers between 0 and 100, or fractions");
			return;
		}

		mc.setVolume(set);
	}

	private double getAbsoluteSet(Phrase phrase) {
		Tag numberTag = phrase.getTag(new Tag(TagType.NUMBER, null));
		Tag fractionTag = phrase.getTag(new Tag(TagType.FRACTION, null));

		if (numberTag != null) {
			String numberString = numberTag.getValue();
			if (numberString.matches("\\d*"))
				return Double.parseDouble(numberString) / 100;
			else
				return -1;
		}

		if (fractionTag != null) {
			String fractionString = fractionTag.getValue();
			if (fractionString.matches("(0)|(0.\\d*)|(1)"))
				return Double.parseDouble(fractionString);
			else
				return -1;
		}

		return -1;
	}

	@Override
	protected ArrayList<PhraseOutline> makePhraseOutlines() {
		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		PhraseOutline absoluteNumber = new PhraseOutline(logger, getName());
		absoluteNumber.addMandatoryTag(new Tag(TagType.SETTER, "x"));
		absoluteNumber.addMandatoryTag(new Tag(TagType.MEDIA, "track"));
		absoluteNumber.addMandatoryTag(new Tag(TagType.NUMBER, null));

		PhraseOutline absoluteFraction = new PhraseOutline(logger, getName());
		absoluteFraction.addMandatoryTag(new Tag(TagType.SETTER, null));
		absoluteFraction.addMandatoryTag(new Tag(TagType.MEDIA, "track"));
		absoluteFraction.addMandatoryTag(new Tag(TagType.FRACTION, null));

		absoluteNumber.addNeutralTag(new Tag(TagType.AUDIO, "volume"));
		absoluteFraction.addNeutralTag(new Tag(TagType.AUDIO, "volume"));
		outlines.add(absoluteNumber);
		outlines.add(absoluteFraction);

		return outlines;
	}

	@Override
	public String getName() {
		return "Media centre set volume";
	}

}
