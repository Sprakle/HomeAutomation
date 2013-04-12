package net.sprakle.homeAutomation.utilities.externalSoftware.commandLine;

import net.sprakle.homeAutomation.main.Info;
import net.sprakle.homeAutomation.main.OS;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.os.LinuxCLI;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.os.MacCLI;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.os.WindowsCLI;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class CommandLineFactory {
	public static CommandLineInterface getCommandLine(Logger logger) {
		CommandLineInterface cli = null;

		OS os = Info.getOS();
		switch (os) {
			case LINUX:
				cli = new LinuxCLI(logger);
				break;

			case WINDOWS:
				cli = new WindowsCLI(logger);
				break;

			case MAC:
				cli = new MacCLI(logger);
				break;

			default:
				logger.log("Operating system not supported", LogSource.ERROR, 1);
				break;
		}

		return cli;
	}
}
