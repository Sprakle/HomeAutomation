package net.sprakle.homeAutomation.externalSoftware.commandLine.os;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class WindowsCLI implements CommandLineInterface {

	private Logger logger;

	public WindowsCLI(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void execute(String command) {
		logger.log("CLI not supported in this operating system", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

}
