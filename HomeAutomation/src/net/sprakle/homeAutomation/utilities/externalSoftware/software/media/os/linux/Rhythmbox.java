package net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.linux;

import java.io.File;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.Track;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.MediaController;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Rhythmbox extends MediaController {
	private final String CLIENT = "rhythmbox-client";

	CommandLineInterface cli;

	public Rhythmbox(Logger logger, CommandLineInterface cli) {
		super(logger);

		this.logger = logger;
		this.cli = cli;

		String userDir = System.getProperty("user.home") + "/";
		String rhythmFile = Config.getString("config/external_software/rhythmbox/relative_directory");
		File xmlFile = new File(userDir + rhythmFile);
		tracks = TrackFactory.getTracks(logger, xmlFile);
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
		String command = CLIENT + " " + pc.getCommand();
		cli.execute(command);
	}

	@Override
	public void playTrack(Track track) {
		String pre = CLIENT + " --play-uri";
		cli.execute(pre + " \"" + track.getPath() + "\"");
	}
	@Override
	public void enqueueTrack(Track track) {
		String pre = CLIENT + " --enqueue";
		cli.execute(pre + " \"" + track.getPath() + "\"");
	}
}