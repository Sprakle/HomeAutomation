package net.sprakle.homeAutomation.utilities.externalSoftware;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.swift.Swift;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class SoftwareFactory {
	public static SoftwareInterface getSoftware(Logger logger, CommandLineInterface cli, SoftwareName name) {
		SoftwareInterface software = null;

		switch (name) {
			case MEDIA_CENTRE:
				software = new MediaCentre(logger, cli);
				break;
			case SWIFT:
				software = new Swift(logger, cli);
				break;
		}

		return software;
	}
}
