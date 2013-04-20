/*
 * Gets access to the Linux shell, then uses swift TTS engine to create an mp3 file with the voice.
 * the mp3 is then played.
 */

package net.sprakle.homeAutomation.synthesis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.swift.Swift;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Synthesis {

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
		Swift swift = (Swift) exs.getSoftware(SoftwareName.SWIFT);
		swift.speak(phrase);
	}

	// since swift cannot pronounce numbers like "-67", they must be modified to use the word "negative"
	private String filterNegatives(String original) {
		String filtered = original;

		String regex = "-[0-9]{1,}";

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
