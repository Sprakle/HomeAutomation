/*
 * Gets access to the Linux shell, then uses swift TTS engine to create an mp3 file with the voice.
 * the mp3 is then played.
 */

package net.sprakle.homeAutomation.speech.synthesis;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.audio.out.AudioOut;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.externalSoftware.Software;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Synthesis {

	private static final String path = Config.getString("config/files/synthesis_output");

	private Logger logger;

	private ExternalSoftware exs;

	public Synthesis(Logger logger, ExternalSoftware exs) {
		this.logger = logger;
		this.exs = exs;
	}

	public void speak(String phrase) {

		logger.log(phrase, LogSource.SYNTHESIS_OUTPUT, 1);

		phrase = filterNegatives(phrase);

		// first write the wav file
		String[] command = { path, phrase };
		exs.execute(Software.SWIFT, command);

		// then play it
		File speechFile = new File(path);
		AudioOut.playSound(logger, speechFile);
	}

	// since swift cannot pronounce numbers like "-67", they must be modified to use the word "negative"
	private String filterNegatives(String original) {
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
