package utilities.externalSoftware;

import java.util.HashMap;

import utilities.externalSoftware.commandLine.CommandLineInterface;
import utilities.externalSoftware.software.SoftwareInterface;
import utilities.externalSoftware.software.Swift;
import utilities.logger.Logger;

public class SoftwareFactory {
	public static HashMap<Software, SoftwareInterface> getSoftware(Logger logger, CommandLineInterface cli) {
		HashMap<Software, SoftwareInterface> software;
		software = new HashMap<Software, SoftwareInterface>();

		software.put(Software.SWIFT, new Swift(logger, cli));

		return software;
	}
}
