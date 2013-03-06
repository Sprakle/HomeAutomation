package speech.interpretation;

import java.util.ArrayList;

import speech.interpretation.utilities.tagger.Tagger;
import speech.interpretation.utilities.tagger.tags.Tag;
import utilities.logger.LogSource;
import utilities.logger.Logger;

public class Phrase {
	// original text from input
	String rawText;

	// defines the interpretation pipeline status. Once set to 'complete', new phrases may be accepted
	PipelineStatus pipelineStatus;

	public Phrase(Logger logger, Tagger tagger, String rawText) {
		pipelineStatus = PipelineStatus.INITIALIZED;

		this.rawText = rawText;

		logger.log("New phrase created. Raw text: '" + rawText + "'", LogSource.PHRASE_INFO, 3);

		logger.log("Tagging phrase:", LogSource.PHRASE_INFO, 2);
		ArrayList<Tag> tags = tagger.tagText(rawText);
		String tagString = "";
		for (Tag t : tags) {
			tagString += t.getFormattedAsText();
		}
		logger.log(tagString, LogSource.PHRASE_INFO, 2);
	}

	public PipelineStatus getPipelineStatus() {
		return pipelineStatus;
	}

	public String getRawText() {
		return rawText;
	}
}
