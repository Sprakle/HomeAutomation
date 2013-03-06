package net.sprakle.homeAutomation.utilities.externalSoftware.software;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public abstract class SoftwareInterface {

	Logger logger;
	CommandLineInterface cli;

	public SoftwareInterface(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;
	}

	public abstract void execute(String[] args);
}
