package net.sprakle.homeAutomation.interpretation.tagger;

import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class TagFileParser {

	// gets the trigger of a line
	public static String getTrigger(Logger logger, String line) {
		String trigger = null;

		trigger = getStringBetween(logger, line, "\"", "\"");

		return trigger;
	}

	// gets the tagType of a line
	public static TagType getType(Logger logger, String line) {
		TagType type = null;

		String typeString = getStringBetween(logger, line, "{", "/");

		// get the correct type based on the string
		try {
			type = TagType.valueOf(typeString.toUpperCase());
		} catch (IllegalArgumentException e) {
			logger.log("No type found for " + typeString, LogSource.ERROR, LogSource.TAGGER_INFO, 1);
		}

		return type;
	}

	// gets the value of the line
	public static String getValue(Logger logger, String line) {
		String value = null;

		value = getStringBetween(logger, line, "/", "}");

		return value;
	}

	private static String getStringBetween(Logger logger, String source, String a, String b) {
		String between = null;

		int start = source.indexOf(a) + 1;
		int end = source.indexOf(b, start);

		between = source.substring(start, end);

		// make sure it worked
		if (start == -1 || end == -1) {
			logger.log("Problem parsing: " + source, LogSource.ERROR, LogSource.TAGGER_INFO, 1);
		}

		return between;
	}
}
