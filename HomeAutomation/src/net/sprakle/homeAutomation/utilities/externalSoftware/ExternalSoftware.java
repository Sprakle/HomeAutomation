/*
 * Used to access external applications, usually OS dependent
 */

package net.sprakle.homeAutomation.utilities.externalSoftware;

import java.util.HashMap;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineFactory;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class ExternalSoftware {

	HashMap<Software, SoftwareInterface> softwareInterfaces;

	public ExternalSoftware(Logger logger) {
		CommandLineInterface cli = CommandLineFactory.getCommandLine(logger);

		softwareInterfaces = SoftwareFactory.getSoftware(logger, cli);
	}

	public void execute(Software software, String[] args) {
		SoftwareInterface targetSoftware = softwareInterfaces.get(software);
		targetSoftware.execute(args);
	}
}
