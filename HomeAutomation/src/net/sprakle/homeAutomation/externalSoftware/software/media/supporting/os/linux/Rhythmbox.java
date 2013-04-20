package net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.linux;

import java.io.File;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.MediaController;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Rhythmbox extends MediaController {
	private final String CLIENT = "rhythmbox-client";

	CommandLineInterface cli;

	public Rhythmbox(Logger logger, CommandLineInterface cli) {
		super(logger);

		this.logger = logger;
		this.cli = cli;
	}

	@Override
	public void loadTracks() {
		String userDir = System.getProperty("user.home") + "/";
		String rhythmFile = Config.getString("config/external_software/rhythmbox/relative_directory");
		File xmlFile = new File(userDir + rhythmFile);
		tracks = TrackFactory.getTracks(logger, xmlFile);
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
		String command = CLIENT + " " + pc.getCommand();

		// command must be sent twice to go back
		int num = pc == PlaybackCommand.BACK ? 2 : 1;
		cli.execute(command, num);
	}

	@Override
	public void playTrack(Track track) {
		String pre = CLIENT + " --play-uri";
		cli.execute(pre + " \"" + track.getPath() + "\"", 1);
	}
	@Override
	public void enqueueTrack(Track track) {
		String pre = CLIENT + " --enqueue";
		cli.execute(pre + " \"" + track.getPath() + "\"", 1);
	}

	@Override
	public void setVolume(double vol) {
		String pre = CLIENT + " --set-volume";
		cli.execute(pre + " " + vol, 1);
	}

	@Override
	public void changeVolume(double change) {
		String command = CLIENT + " --volume-" + (change > 0 ? "up" : "down");

		// get number of times to call volume up/down (each is 10%)
		change = Math.abs(change);
		int changes = (int) (change * 10);
		cli.execute(command, changes);
	}
}
