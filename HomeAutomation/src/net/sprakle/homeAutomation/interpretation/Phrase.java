package net.sprakle.homeAutomation.interpretation;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Phrase {
	// original text from input
	String rawText;

	private ArrayList<Tag> tags;

	public Phrase(Logger logger, Tagger tagger, String rawText) {
		this.rawText = rawText;

		logger.log("New phrase created. Raw text: '" + rawText + "'", LogSource.PHRASE_INFO, 3);

		tags = tagger.tagText(rawText);

		logger.log("Tagging phrase:", LogSource.PHRASE_INFO, 2);
		String tagString = "";
		for (Tag t : tags) {
			tagString += t.getFormattedAsText() + " ";
		}
		logger.log(tagString, LogSource.PHRASE_INFO, 2);
	}

	public String getRawText() {
		return rawText;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}
}
