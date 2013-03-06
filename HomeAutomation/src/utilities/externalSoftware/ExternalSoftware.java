/*
 * Used to access external applications, usually OS dependent
 */

package utilities.externalSoftware;

import java.util.HashMap;

import utilities.externalSoftware.commandLine.CommandLineFactory;
import utilities.externalSoftware.commandLine.CommandLineInterface;
import utilities.externalSoftware.software.SoftwareInterface;
import utilities.logger.Logger;

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
