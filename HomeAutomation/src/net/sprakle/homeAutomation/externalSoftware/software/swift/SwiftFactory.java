package net.sprakle.homeAutomation.externalSoftware.software.swift;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;

public class SwiftFactory implements SoftwareInterfaceFactory {

	private CommandLineInterface cli;

	public SwiftFactory(CommandLineInterface cli) {
		this.cli = cli;
	}

	@Override
	public SoftwareInterface getActiveSoftware() {
		return new SwiftActive(cli);
	}

	@Override
	public SoftwareInterface getInactiveSoftware() {
		return new SwiftInactive();
	}

}
