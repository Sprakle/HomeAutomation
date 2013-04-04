package net.sprakle.homeAutomation.utilities.externalSoftware.software.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.main.OS;
import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
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
		}
	}

	public ArrayList<Track> getTracks() {
		return controller.getTracks();
	}

	public void playbackCommand(PlaybackCommand pc) {
		logger.log("Media playback command " + pc + " recieved", LogSource.EXTERNAL_SOFTWARE, 2);
		controller.playbackCommand(pc);
	}

	public void systemCommand(SystemCommand sc, String arguments) {
		logger.log("Media systen command " + sc + " recieved", LogSource.EXTERNAL_SOFTWARE, 2);
		controller.systemCommand(sc, arguments);
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.MEDIA_CENTRE;
	}

}
