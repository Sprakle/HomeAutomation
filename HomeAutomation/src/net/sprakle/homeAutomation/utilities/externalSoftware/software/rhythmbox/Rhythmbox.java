package net.sprakle.homeAutomation.utilities.externalSoftware.software.rhythmbox;

import java.io.File;
import java.util.ArrayList;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Rhythmbox extends SoftwareInterface {
	// rhythmbox database: /home/ben/.local/share/rhythmbox
	// '%20' must be removed and replaced with ' ', and '/' added to the beginning

	private final String CLIENT = "rhythmbox-client";

	private ArrayList<Track> tracks;

	public Rhythmbox(Logger logger, CommandLineInterface cli) {
		super(logger, cli);

		String userDir = Config.getString("config/local/user_directory");
		String rhythmFile = Config.getString("config/external_software/rhythmbox/relative_directory");
		File xmlFile = new File(userDir + rhythmFile);
		tracks = TrackFactory.getTracks(logger, xmlFile);
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	public void playbackCommand(PlaybackCommand pc) {
		String command = CLIENT + " " + pc.getCommand();
		cli.execute(logger, command);
	}

	public void systemCommand(SystemCommand sc, String arguments) {
		startIfStopped();

		String command = CLIENT + " " + sc.getCommand() + " " + arguments;
		cli.execute(logger, command);
	}

	private void startIfStopped() {
		String command = CLIENT + " --no-present";
		cli.execute(logger, command);
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.RHYTHMBOX;
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
