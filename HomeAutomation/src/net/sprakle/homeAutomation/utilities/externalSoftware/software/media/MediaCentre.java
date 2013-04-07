package net.sprakle.homeAutomation.utilities.externalSoftware.software.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.main.Config;
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

public class MediaCentre extends SoftwareInterface {

	MediaController controller;

	public MediaCentre(Logger logger, CommandLineInterface cli) {
		super(logger, cli);

		OS os = Config.getOS();

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

	public static void main(String args[]) {
		Logger logger = new Logger();

		MediaCentre mc = new MediaCentre(logger, new LinuxCLI(logger));

		mc.playRandomTrack("poets of the fall");
	}
}
