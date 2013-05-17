package net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.mac;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.MediaController;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class MacMediaController extends MediaController {

	private final Logger logger;

	@SuppressWarnings("UnusedParameters")
    public MacMediaController(Logger logger, CommandLineInterface cli) {
		super(logger);

		this.logger = logger;
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
		freakOut();
	}

	@Override
	public void playTrack(Track track) {
		freakOut();
	}

	@Override
	public void enqueueTrack(Track track) {
		freakOut();
	}

	@Override
	public void loadTracks() {
		freakOut();
	}

	@Override
	public void setVolume(double vol) {
		freakOut();
	}

	@Override
	public void changeVolume(double change) {
		freakOut();
	}

	@Override
	public Track getCurrentTrack() {
		freakOut();
		return null;
	}

	private void freakOut() {
		logger.log("Mac Media Controller not yet implemented!", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

}
