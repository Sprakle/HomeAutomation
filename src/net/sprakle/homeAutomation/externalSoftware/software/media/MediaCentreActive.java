package net.sprakle.homeAutomation.externalSoftware.software.media;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sprakle.homeAutomation.events.Event;
import net.sprakle.homeAutomation.events.EventListener;
import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.MediaController;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.linux.Rhythmbox;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.mac.MacMediaController;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.windows.WindowsMediaController;
import net.sprakle.homeAutomation.interpretation.module.modules.reloading.ReloadEvent;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.main.Info;
import net.sprakle.homeAutomation.main.OS;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

class MediaCentreActive implements MediaCentre, EventListener {

	private final Logger logger;

	private MediaController controller;

	public MediaCentreActive(Logger logger, CommandLineInterface cli) {
		this.logger = logger;

		OS os = Info.getOS();

		switch (os) {
			case LINUX:
				controller = new Rhythmbox(logger, cli);
				break;

			case MAC:
				controller = new WindowsMediaController(logger, cli);
				break;

			case WINDOWS:
				controller = new MacMediaController(logger, cli);
				break;

			default:
				logger.log("Unsupported operating system", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
				break;
		}

		controller.loadTracks();

		EventManager.getInstance(logger).addListener(EventType.RELOAD, this);
	}

	@Override
	public void playTrack(String title, String artist) {
		controller.playTrack(title, artist);
	}

	@Override
	public void enqueueTrack(String title, String artist) {
		controller.enqueueTrack(title, artist);
	}

	@Override
	public void playRandomTrack(String artist) {
		controller.playRandomTrack(artist);
	}

	@Override
	public void setVolume(double vol) {
		if (vol < 0 || vol > 1) {
			logger.log("Invalid volume value", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
			return;
		}

		controller.setVolume(vol);
	}

	@Override
	public void changeVolume(double change) {
		if (change < -1 || change > 1) {
			logger.log("Invalid volume value", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
			return;
		}

		controller.changeVolume(change);
	}

	@Override
	public ArrayList<Track> getTracks() {
		return controller.getTracks();
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
		logger.log("Media playback command " + pc + " recieved", LogSource.EXTERNAL_SOFTWARE, 2);
		controller.playbackCommand(pc);
	}

	@Override
	public void call(EventType et, Event e) {
		if (et != EventType.RELOAD) {
			return; // not applicable
		}

		ReloadEvent reloadEvent = (ReloadEvent) e;
		Tag tag = reloadEvent.getTag();

		if (!tag.getValue().equals("media")) {
			return;
		}

		controller.loadTracks();
	}

	@Override
	public Track levenGet(String title, String artist, int maxDistance) {
		return controller.levenGet(title, artist, maxDistance);
	}

	@Override
	public TreeMap<Integer, Track> levenGetMulti(String title, String artist, int maxDistance) {
		return controller.levenGetMulti(title, artist, maxDistance);
	}

	@Override
	public Track getCurrentTrack() {
		return controller.getCurrentTrack();
	}
}
