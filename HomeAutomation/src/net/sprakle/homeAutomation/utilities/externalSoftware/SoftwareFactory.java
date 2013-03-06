package net.sprakle.homeAutomation.utilities.externalSoftware;

import java.util.HashMap;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.Swift;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class SoftwareFactory {
	public static HashMap<Software, SoftwareInterface> getSoftware(Logger logger, CommandLineInterface cli) {
		HashMap<Software, SoftwareInterface> software;
		software = new HashMap<Software, SoftwareInterface>();

		software.put(Software.SWIFT, new Swift(logger, cli));

		return software;
	}
}
