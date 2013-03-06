package utilities.externalSoftware.commandLine;

import utilities.OS.DetermineOS;
import utilities.OS.OperatingSystem;
import utilities.logger.LogSource;
import utilities.logger.Logger;

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
