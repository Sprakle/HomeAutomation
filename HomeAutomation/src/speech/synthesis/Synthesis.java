/*
 * Gets access to the Linux shell, then uses swift TTS engine to create an mp3 file with the voice.
 * the mp3 is then played.
 */

package speech.synthesis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.audio.out.AudioOut;
import utilities.logger.LogSource;
import utilities.logger.Logger;

public class Synthesis {

	private static final Path mp3TempPath = Paths.get("src/speech/synthesis/output/");

	public static void speak(Logger logger, String phrase) {

		phrase = filterNegatives(phrase);

		// first write the wav file
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("swift -n David \"" + phrase + "\" -o " + mp3TempPath + "/speech.wav");
			logger.log(phrase, LogSource.SYNTHESIS_OUTPUT, 2);

			try {
				process.waitFor();
			} catch (InterruptedException e) {
				logger.log("Problem accessing Linux Shell!", LogSource.ERROR, LogSource.SYNTHESIS_INFO, 1);
				e.printStackTrace();
			}
		} catch (IOException e) {
			logger.log("Problem accessing Linux Shell!", LogSource.ERROR, LogSource.SYNTHESIS_INFO, 1);
			e.printStackTrace();
		}

		// then play it
		File speechFile = new File(mp3TempPath + "/speech.wav");
		AudioOut.playSound(logger, speechFile);
	}

	// since swift cannot pronounce numbers like "-67", they must be modified to use the word "negative"
	private static String filterNegatives(String original) {
		String filtered = original;

		String regex = "\\-[0-9]{0,}";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(filtered);
		while (matcher.find()) {
			if (matcher.group().length() == 0)
				break;

			// get the number less the dash
			int beginIndex = matcher.start();
			int endIndex = matcher.end();
			String number = filtered.substring(beginIndex + 1, endIndex);

			filtered = filtered.replaceFirst(regex, "negative " + number);
		}

		return filtered;
	}
}
