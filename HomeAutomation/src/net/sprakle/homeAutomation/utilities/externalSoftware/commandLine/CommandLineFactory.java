package net.sprakle.homeAutomation.utilities.externalSoftware.commandLine;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.main.OS;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.os.LinuxCLI;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.os.MacCLI;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.os.WindowsCLI;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class CommandLineFactory {
	public static CommandLineInterface getCommandLine(Logger logger) {
		CommandLineInterface cli = null;

		OS os = Config.getOS();
		switch (os) {
			case LINUX:
				cli = new LinuxCLI();
				break;

			case WINDOWS:
				cli = new WindowsCLI();
				break;

			case MAC:
				cli = new MacCLI();
				break;

			default:
				logger.log("Operating system not supported", LogSource.ERROR, 1);
				break;
		}

		return cli;
	}
}
