/*
 * Swift is used to convert a string into a wav file.
 * 
 * Arguments:
 * 		[path/file] [phrase]
 * 
 * These swift commands happen to work on BOTH windows and linux
 */

package net.sprakle.homeAutomation.externalSoftware.software.swift;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

class SwiftActive implements Swift {

	private final String FILE_NAME = "synth.wav";

	private Logger logger;

	private CommandLineInterface cli;

	public SwiftActive(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;
	}

	@Override
	public void speak(String phrase) {
		String command = "swift -n David \"" + phrase + "\" -o " + FILE_NAME;
		cli.execute(command, 1);

		File audioFile = new File(FILE_NAME);
		audioFile.deleteOnExit();
		playSound(audioFile);
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.SWIFT;
	}

	private void playSound(File soundFile) {
		logger.log("Playing audio file: '" + soundFile.getPath() + "'", LogSource.AUDIO_OUT, 2);

		AudioInputStream stream;
		AudioFormat format;
		DataLine.Info info;
		Clip clip;

		try {
			stream = AudioSystem.getAudioInputStream(soundFile);
			format = stream.getFormat();
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start(); // this might need to be closed!
		} catch (Exception e) {
			logger.log("Problem playing audio file!", LogSource.ERROR, LogSource.AUDIO_OUT, 1);
		}
	}
}
