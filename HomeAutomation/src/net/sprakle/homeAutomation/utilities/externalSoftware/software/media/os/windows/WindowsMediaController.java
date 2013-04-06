package net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.windows;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.Track;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.MediaController;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class WindowsMediaController extends MediaController {

	Logger logger;
	CommandLineInterface cli;

	public WindowsMediaController(Logger logger, CommandLineInterface cli) {
		super(logger);

		this.logger = logger;
		this.cli = cli;
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
		logger.log("Windows Media Controller not yet implemented!", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

	@Override
	public void playTrack(Track track) {
		logger.log("Windows Media Controller not yet implemented!", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

	@Override
	public void enqueueTrack(Track track) {
		logger.log("Windows Media Controller not yet implemented!", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

}
