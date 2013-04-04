package net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.windows;

import java.util.ArrayList;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.SystemCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.Track;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.MediaController;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class WindowsMediaController implements MediaController {

	Logger logger;
	CommandLineInterface cli;

	public WindowsMediaController(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;
	}

	@Override
	public ArrayList<Track> getTracks() {
		logger.log("Windows Media Controller not yet implemented!", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
		return null;
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
		logger.log("Windows Media Controller not yet implemented!", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

	@Override
	public void systemCommand(SystemCommand sc, String arguments) {
		logger.log("Windows Media Controller not yet implemented!", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

}
