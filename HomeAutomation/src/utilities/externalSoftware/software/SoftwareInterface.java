package utilities.externalSoftware.software;

import utilities.externalSoftware.commandLine.CommandLineInterface;
import utilities.logger.Logger;

public abstract class SoftwareInterface {

	Logger logger;
	CommandLineInterface cli;

	public SoftwareInterface(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;
	}

	public abstract void execute(String[] args);
}
