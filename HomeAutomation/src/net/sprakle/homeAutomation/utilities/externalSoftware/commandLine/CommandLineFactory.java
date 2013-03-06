package net.sprakle.homeAutomation.utilities.externalSoftware.commandLine;

import net.sprakle.homeAutomation.utilities.OS.DetermineOS;
import net.sprakle.homeAutomation.utilities.OS.OperatingSystem;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class CommandLineFactory {
	public static CommandLineInterface getCommandLine(Logger logger) {
		CommandLineInterface cli = null;

		OperatingSystem os = DetermineOS.determine();
		switch (os) {
			case LINUX:
				cli = new Linux();
				break;

			case WINDOWS:
				cli = new Windows();
				break;

			case OTHER:
				logger.log("Operating system not supported", LogSource.ERROR, 1);
				break;
		}

		return cli;
	}
}
