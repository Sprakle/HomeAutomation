package net.sprakle.homeAutomation.utilities.externalSoftware.software;

import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public abstract class SoftwareInterface {

	protected Logger logger;
	protected CommandLineInterface cli;

	protected final SoftwareName SOFTWARE_NAME;

	public SoftwareInterface(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;

		SOFTWARE_NAME = getSoftwareName();
	}

	public abstract SoftwareName getSoftwareName();
}
