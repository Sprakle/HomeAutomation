package net.sprakle.homeAutomation.externalSoftware.software.media;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class MediaCentreFactory implements SoftwareInterfaceFactory {

	private final Logger logger;
	private final CommandLineInterface cli;

	public MediaCentreFactory(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;
	}

	@Override
	public SoftwareInterface getActiveSoftware() {
		return new MediaCentreActive(logger, cli);
	}

	@Override
	public SoftwareInterface getInactiveSoftware() {
		return new MediaCentreInactive(logger);
	}

}
