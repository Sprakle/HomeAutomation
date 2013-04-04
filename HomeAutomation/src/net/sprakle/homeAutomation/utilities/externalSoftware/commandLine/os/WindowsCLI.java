package net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.os;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class WindowsCLI implements CommandLineInterface {

	@Override
	public void execute(Logger logger, String command) {
		logger.log("CLI not supported in this operating system", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

}
