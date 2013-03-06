package net.sprakle.homeAutomation.utilities.audio.out;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class AudioOut {

	public static void playSound(Logger logger, File soundFile) {

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