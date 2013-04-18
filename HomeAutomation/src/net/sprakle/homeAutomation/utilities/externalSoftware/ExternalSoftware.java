/*
 * Used to access external applications, usually OS dependent
 */

package net.sprakle.homeAutomation.utilities.externalSoftware;

import java.util.ArrayList;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineFactory;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.SoftwareInterface;
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

	/**
	 * Software can be initialised before it is needed
	 * 
	 * @param name
	 */
	public void initSoftware(SoftwareName name) {
		software.add(SoftwareFactory.getSoftware(logger, cli, name));
	}

	public SoftwareInterface getSoftware(SoftwareName name) {
		for (SoftwareInterface si : software) {
			if (si.getSoftwareName() == name) {
				return si;
			}
		}

		initSoftware(name);
		return getSoftware(name);
	}
}
