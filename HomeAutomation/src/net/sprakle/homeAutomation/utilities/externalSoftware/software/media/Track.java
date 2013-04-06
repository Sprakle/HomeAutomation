package net.sprakle.homeAutomation.utilities.externalSoftware.software.media;

import java.io.File;
import java.io.IOException;

import net.sprakle.homeAutomation.utilities.levenshtein.Levenshtein;
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

		AbstractMP3Tag tagV2 = null;
		AbstractMP3Tag tagV1 = null;

		String v2Title = "";
		String v2Artist = "";

		String v1Title = "";
		String v1Artist = "";

		if (mp3file.hasID3v2Tag()) {
			tagV2 = mp3file.getID3v2Tag();

			v2Title = tagV2.getSongTitle();
			v2Artist = tagV2.getLeadArtist();
		}

		if (mp3file.hasID3v1Tag()) {
			tagV1 = mp3file.getID3v1Tag();

			v1Title = tagV1.getSongTitle();
			v1Artist = tagV1.getLeadArtist();
		}

		// clean
		v2Title = v2Title.replaceAll("[^\\x20-\\x7e]", "");
		v2Artist = v2Artist.replaceAll("[^\\x20-\\x7e]", "");
		v1Title = v1Title.replaceAll("[^\\x20-\\x7e]", "");
		v1Artist = v1Artist.replaceAll("[^\\x20-\\x7e]", "");

		// get as much data as possible from tags
		if (v2Title != "")
			title = v2Title;
		else
			title = v1Title;

		if (v2Artist != "")
			artist = v2Artist;
		else
			artist = v1Artist;

		// use filename for title if there is no tag
		if (title.equals("")) {
			String filename = sourceFile.getName();
			filename = filename.substring(0, filename.lastIndexOf('.'));

			title = filename;
		}

		if (title.equals("") || artist.equals("")) {
			logger.log("Unable to get complete metadata for " + sourceFile.getAbsolutePath() + " (Tag support is minimal at this time - try converting your tags)", LogSource.WARNING, LogSource.EXTERNAL_SOFTWARE, 2);
		}

		sourceFile = null;
		mp3file = null;

		logger.log("Track loaded: '" + title + "' - '" + artist + "'", LogSource.EXTERNAL_SOFTWARE, 5);
	}
	public int levenshteinDistanceTitle(String target) {
		return Levenshtein.getDistance(target, title);
	}

	public int levenshteinDistanceArtist(String target) {
		return Levenshtein.getDistance(target, artist);
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
