/*
 * Returns each line of a UTF 8 encoded file in a String ArrayList
 */

package net.sprakle.homeAutomation.utilities.fileAccess.read;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class LineByLine {

	private final static String ENCODING = "UTF-8";

	private final static String SINGLE_START_DELIMITER = "//";
	private final static String SINGLE_END_DELIMITER = "(\\n)|(\\r)";

	private final static String MULTI_START_DELIMITER = "/\\*";
	private final static String MULTI_END_DELIMITER = "\\*/";

	/**
	 * Parses a text file into lines
	 * 
	 * Double slash comments will cause the remainder of a line to be ignored
	 * Slash-asterisk comments will cause text to be ignored until an
	 * asterisk-slash
	 * 
	 * 
	 * @param logger
	 * @param path
	 * @param removeBlanks
	 *            If true, lines that have no characters or only spaces/tabs
	 *            will be removed
	 * @return
	 */
	public static List<String> read(Logger logger, Path path, boolean removeComments, boolean removeBlanks) {

		logger.log("Reading file '" + path + "' line by line", LogSource.FILE_ACCES, 2);

		String lines = null;
		try {
			byte[] data = Files.readAllBytes(path);

			lines = new String(data, ENCODING);
		} catch (IOException e) {
			logger.log("Unable to read file! '" + path + "'", LogSource.ERROR, LogSource.FILE_ACCES, 1);
		}

		if (removeComments) {
			lines = removeBetween(logger, lines, SINGLE_START_DELIMITER, SINGLE_END_DELIMITER, true);
			lines = removeBetween(logger, lines, MULTI_START_DELIMITER, MULTI_END_DELIMITER, false);
		}

		// remove escapes not preceded by other escapes
		lines = lines.replaceAll("(?<!\\\\)\\\\", "");

		// split into list
		String[] lineArray = lines.split("(\\n)|(\\r)");
		List<String> lineList = new ArrayList<String>(Arrays.asList(lineArray));

		// remove blanks if required
		if (removeBlanks) {
			Iterator<String> iter = lineList.iterator();
			while (iter.hasNext()) {
				String line = iter.next();
				if (isBlank(line)) {
					iter.remove();
				}
			}
		}

		return lineList;
	}

	private static String removeBetween(Logger logger, String lines, String startDelimiter, String endDelimiter, boolean keepEnd) {

		String regex = "(?<!\\\\)" + startDelimiter;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(lines);

		// Check all occurrences
		while (matcher.find()) {
			int start = matcher.start();
			int end = indexOf(lines, endDelimiter, start);

			if (end == -1)
				logger.log("Single line comment not ended. Required: '" + endDelimiter + "'", LogSource.ERROR, LogSource.FILE_ACCES, 1);

			if (!keepEnd)
				end += endDelimiter.length();

			String a = lines.substring(0, start);
			String b = lines.substring(end);
			lines = a + b;

			matcher = pattern.matcher(lines);
		}

		return lines;
	}

	private static int indexOf(String s, String regex, int startIndex) {
		s = s.substring(startIndex);

		String patternStr = regex;
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			return startIndex + matcher.start();
		}
		return -1;
	}

	private static boolean isBlank(String line) {
		return line.matches("(^[ \\t]+|[ \\t]+$)|");
	}
}
