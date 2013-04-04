package net.sprakle.homeAutomation.utilities.externalSoftware.software.rhythmbox;

import java.io.File;
import java.io.IOException;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

public class Track {
	private String title;
	private String artist;

	private File sourceFile;
	private String path;

	public Track(Logger logger, File sourceFile) {
		this.sourceFile = sourceFile;
		this.path = sourceFile.getAbsolutePath();

		// get tags
		MP3File mp3file = null;
		try {
			mp3file = new MP3File(sourceFile);
		} catch (IOException | TagException e) {
			logger.log("Unable to load MP3 file: " + sourceFile.getAbsolutePath(), LogSource.WARNING, LogSource.EXTERNAL_SOFTWARE, 1);
			e.printStackTrace();
			return;
		}

		AbstractMP3Tag tag = null;

		if (mp3file.hasID3v1Tag()) {
			tag = mp3file.getID3v1Tag();

		} else if (mp3file.hasID3v2Tag()) {
			tag = mp3file.getID3v2Tag();

		} else {
			logger.log("Unable to get tag for track: " + sourceFile.getAbsolutePath(), LogSource.WARNING, LogSource.EXTERNAL_SOFTWARE, 1);
		}

		title = tag.getSongTitle();
		artist = tag.getLeadArtist();

		// clean
		title = title.replaceAll("[^\\x20-\\x7e]", "");
		artist = artist.replaceAll("[^\\x20-\\x7e]", "");

		//use filename for title if there is no metadata
		if (title.equals("")) {
			String filename = sourceFile.getName();
			filename = filename.substring(0, filename.lastIndexOf('.'));

			title = filename;
		}

		if (title.equals("") && artist.equals("")) {
			logger.log("Unable to get metadata for " + sourceFile.getAbsolutePath(), LogSource.WARNING, LogSource.EXTERNAL_SOFTWARE, 2);
		}

		sourceFile = null;
		mp3file = null;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getPath() {
		return path;
	}
}
