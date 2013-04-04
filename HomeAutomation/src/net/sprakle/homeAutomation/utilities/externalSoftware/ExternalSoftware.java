/*
 * Used to access external applications, usually OS dependent
 */

package net.sprakle.homeAutomation.utilities.externalSoftware;

import java.util.ArrayList;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineFactory;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ExternalSoftware {
	Logger logger;
	CommandLineInterface cli;

	ArrayList<SoftwareInterface> software;

	public ExternalSoftware(Logger logger) {
		this.logger = logger;
		this.cli = CommandLineFactory.getCommandLine(logger);

		software = new ArrayList<SoftwareInterface>();
	}

	public void initSoftware(SoftwareName name) {
		software.add(SoftwareFactory.getSoftware(logger, cli, name));
	}

	public SoftwareInterface getSoftware(SoftwareName name) {
		for (SoftwareInterface si : software) {
			if (si.getSoftwareName() == name) {
				return si;
			}
		}

		logger.log(name + " not yet initialized", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);

		return null;
	}
}
