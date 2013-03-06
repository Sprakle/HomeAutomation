package net.sprakle.homeAutomation.utilities.externalSoftware.commandLine;

import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Windows implements CommandLineInterface {

	@Override
	public void execute(Logger logger, String command) {
		System.err.println("Windows command line not yet supported. Add support in:");
		System.err.println("    utilities.externalSoftware.commandLine.Windows");
	}

}
