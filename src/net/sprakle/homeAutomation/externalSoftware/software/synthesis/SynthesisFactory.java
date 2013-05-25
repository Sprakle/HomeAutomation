package net.sprakle.homeAutomation.externalSoftware.software.synthesis;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class SynthesisFactory implements SoftwareInterfaceFactory {

	private final Logger logger;
	private final CommandLineInterface cli;

	public SynthesisFactory(Logger logger, CommandLineInterface cli) {
		this.logger = logger;
		this.cli = cli;
	}

	@Override
	public SoftwareInterface getActiveSoftware() {
		return new SynthesisActive(logger, cli);
	}

	@Override
	public SoftwareInterface getInactiveSoftware() {
		return new SynthesisInactive();
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.SYNTHESIS;
	}

}
