package net.sprakle.homeAutomation.externalSoftware.software.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;

class MediaCentreInactive implements MediaCentre {

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
		return new ArrayList<Track>();
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
	}
}
