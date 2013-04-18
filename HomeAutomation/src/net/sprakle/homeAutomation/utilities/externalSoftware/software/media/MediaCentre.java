package net.sprakle.homeAutomation.utilities.externalSoftware.software.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.events.Event;
import net.sprakle.homeAutomation.events.EventListener;
import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.interpretation.module.modules.reloading.ReloadEvent;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.main.Info;
import net.sprakle.homeAutomation.main.OS;
import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.os.LinuxCLI;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.MediaController;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.linux.Rhythmbox;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.mac.MacMediaController;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.windows.WindowsMediaController;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class MediaCentre extends SoftwareInterface implements EventListener {

	MediaController controller;

	public MediaCentre(Logger logger, CommandLineInterface cli) {
		super(logger, cli);

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

	/**
	 * 
	 * @param title
	 *            must be given
	 * @param artist
	 *            if null, a track will be searched for using just the title
	 */
	public void playTrack(String title, String artist) {
		controller.playTrack(title, artist);
	}

	/**
	 * 
	 * @param title
	 *            must be given
	 * @param artist
	 *            if null, a track will be searched for using just the title
	 */
	public void enqueueTrack(String title, String artist) {
		controller.enqueueTrack(title, artist);
	}

	/**
	 * 
	 * @param artist
	 *            If null, will play a completely random track. If not null,
	 *            will play a random track by the given artist
	 */
	public void playRandomTrack(String artist) {
		controller.playRandomTrack(artist);
	}

	/**
	 * @param vol
	 *            Volume as a double between 0 and 1
	 */
	public void setVolume(double vol) {
		if (vol < 0 || vol > 1) {
			logger.log("Invalid volume value", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
			return;
		}

		controller.setVolume(vol);
	}

	/**
	 * @param vol
	 *            change in volume as a double between -1 and 1
	 */
	public void changeVolume(double change) {
		if (change < -1 || change > 1) {
			logger.log("Invalid volume value", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
			return;
		}

		controller.changeVolume(change);
	}

	public ArrayList<Track> getTracks() {
		return controller.getTracks();
	}

	public void playbackCommand(PlaybackCommand pc) {
		logger.log("Media playback command " + pc + " recieved", LogSource.EXTERNAL_SOFTWARE, 2);
		controller.playbackCommand(pc);
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.MEDIA_CENTRE;
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

	public static void main(String[] args) {
		Logger logger = new Logger();
		CommandLineInterface cli = new LinuxCLI(logger);
		MediaCentre mc = new MediaCentre(logger, cli);
		mc.changeVolume(-0.9);
	}
}
