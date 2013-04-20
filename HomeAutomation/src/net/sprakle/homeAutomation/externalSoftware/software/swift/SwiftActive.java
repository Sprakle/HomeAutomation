/*
 * Swift is used to convert a string into a wav file.
 * 
 * Arguments:
 * 		[path/file] [phrase]
 * 
 * These swift commands happen to work on BOTH windows and linux
 */

package net.sprakle.homeAutomation.externalSoftware.software.swift;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;

class SwiftActive implements Swift {

	private CommandLineInterface cli;

	public SwiftActive(CommandLineInterface cli) {
		this.cli = cli;
	}

	@Override
	public void writeSpeechFile(String path, String phrase) {
		String command = "swift -n David \"" + phrase + "\" -o " + path;
		cli.execute(command);
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.SWIFT;
	}
}
