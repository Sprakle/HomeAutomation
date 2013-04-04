package net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.linux;

import java.io.File;
import java.util.ArrayList;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.SystemCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.Track;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.TrackFactory;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.MediaController;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Rhythmbox implements MediaController {
	private final String CLIENT = "rhythmbox-client";

	Logger logger;
	CommandLineInterface cli;

	private ArrayList<Track> tracks;

	public Rhythmbox(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;

		String userDir = System.getProperty("user.home") + "/";
		String rhythmFile = Config.getString("config/external_software/rhythmbox/relative_directory");
		File xmlFile = new File(userDir + rhythmFile);
		tracks = TrackFactory.getTracks(logger, xmlFile);
	}

	@Override
	public ArrayList<Track> getTracks() {
		return tracks;
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
		String command = CLIENT + " " + pc.getCommand();
		cli.execute(logger, command);
	}

	@Override
	public void systemCommand(SystemCommand sc, String arguments) {
		startIfStopped();

		String command = CLIENT + " " + sc.getCommand() + " " + arguments;
		cli.execute(logger, command);
	}

	private void startIfStopped() {
		String command = CLIENT + " --no-present";
		cli.execute(logger, command);
	}

	// Get info on track
	/*
	String path = "/home/ben/Music/TheDashDub/Constellations (TheDashDub Remix).mp3";
	File trackFile = new File(path);
	System.out.println(trackFile.getAbsolutePath());
	Track track = new Track(logger, trackFile);
	*/

	// Send command
	/*
	Rhythmbox rb = (Rhythmbox) es.getSoftware(SoftwareName.RHYTHMBOX);
	rb.systemCommand(SystemCommand.ENQUEUE, "");
	ExternalSoftware es = new ExternalSoftware(logger);
	*/
}
