package net.sprakle.homeAutomation.externalSoftware.software.media;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;
import net.sprakle.homeAutomation.utilities.logger.Logger;

class MediaCentreInactive implements MediaCentre {

	private final Logger logger;

	public MediaCentreInactive(Logger logger) {
		this.logger = logger;
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.MEDIA_CENTRE;
	}

	@Override
	public void playTrack(String title, String artist) {
	}

	@Override
	public void enqueueTrack(String title, String artist) {
	}

	@Override
	public void playRandomTrack(String artist) {
	}

	@Override
	public void setVolume(double vol) {
	}

	@Override
	public void changeVolume(double change) {
	}

	@Override
	public ArrayList<Track> getTracks() {
		return new ArrayList<>();
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
	}

	@Override
	public Track levenGet(String title, String artist, int maxDistance) {
		return new Track(logger, null);
	}

	@Override
	public TreeMap<Integer, Track> levenGetMulti(String title, String artist, int maxDistance) {
		return new TreeMap<>();
	}

	@Override
	public Track getCurrentTrack() {
		return new Track(logger, null);
	}
}
