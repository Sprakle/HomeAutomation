package net.sprakle.homeAutomation.externalSoftware.software.swift;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class SwiftFactory implements SoftwareInterfaceFactory {

	private Logger logger;
	private CommandLineInterface cli;

	public SwiftFactory(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;
	}

	@Override
	public SoftwareInterface getActiveSoftware() {
		return new SwiftActive(logger, cli);
	}

	@Override
	public SoftwareInterface getInactiveSoftware() {
		return new SwiftInactive();
	}

}
