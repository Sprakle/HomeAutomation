package speech.interpretation.utilities.tagger.tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import utilities.logger.LogSource;
import utilities.logger.Logger;

public class TagUtilities {
	// returns the position of a tag in a phrase
	public static int getPosition(Logger logger, String trigger, String rawText) {
		int pos = -1;

		if (rawText.contains(trigger)) {
			pos = rawText.indexOf(trigger);
		} else {
			// trigger not found in rawText
			logger.log("Unable to get position of trigger '" + trigger + "' in rawText '" + rawText, LogSource.ERROR, LogSource.TAGGER_INFO, 1);
		}

		return pos;
	}

	// orders tags based on their position variable
	public static ArrayList<Tag> orderTags(ArrayList<Tag> sort) {

		//sort using standard JDK sort method
		Collections.sort(sort, new Comparator<Tag>() {

			@Override
			public int compare(Tag t1, Tag t2) {
				return t1.getPosition() - t2.getPosition();
			}
		});

		return sort;
	}
}
