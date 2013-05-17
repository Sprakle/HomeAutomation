package net.sprakle.homeAutomation.utilities.perspective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

class PerspectiveGroup {
	private final HashMap<Integer, List<String>> perspectives;

	public PerspectiveGroup(Logger logger, List<String> lines) {
		perspectives = new HashMap<>();

		for (String s : lines) {
			int perspective = parsePerspective(s);
			List<String> words = parseWords(s);

			if (perspective == -1 || words.size() < 1) {
				logger.log("Unable to parse perspective map", LogSource.ERROR, LogSource.FILE_ACCES, 1);
				return;
			}

			perspectives.put(perspective, words);
		}
	}

	private int parsePerspective(String line) {
		String regex = "\\d+(?=:)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);

		// Check all occurrences
		if (matcher.find()) {
			String perspectiveString = matcher.group();
			int perspective = Integer.parseInt(perspectiveString);
			return perspective;
		}

		return -1;
	}

	private List<String> parseWords(String line) {
		List<String> words = new ArrayList<>();

		// quote (letter followed by one or more spaces / letters / apostrophes) quote
		String regex = "(?<=\")\\w((\\w)|( )|('))*(?=\")";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);

		// Check all occurrences
		while (matcher.find()) {
			String word = matcher.group();
			words.add(word);
		}

		return words;
	}

	public String switchPerspective(String sentence, int from, int to) {

		List<String> originalWords = perspectives.get(from);
		String toWord = perspectives.get(to).get(0);

		// for every original word of the given perspective
		for (String word : originalWords) {

			// space (given word) space
			String regex = "(?<= )(?i)" + word + "(?= )";

			// replace matching words with new word
			sentence = sentence.replaceAll(regex, toWord);
		}

		return sentence;
	}
}