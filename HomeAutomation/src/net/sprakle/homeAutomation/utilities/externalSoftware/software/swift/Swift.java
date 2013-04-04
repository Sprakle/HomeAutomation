/*
 * Swift is used to convert a string into a wav file.
 * 
 * Arguments:
 * 		[path/file] [phrase]
 * 
 * These swift commands happen to work on BOTH windows and linux
 */

package net.sprakle.homeAutomation.utilities.externalSoftware.software.swift;

import net.sprakle.homeAutomation.utilities.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Swift extends SoftwareInterface {

	public Swift(Logger logger, CommandLineInterface cli) {
		super(logger, cli);
	}

	public void writeSpeechFile(String path, String phrase) {
		String command = "swift -n David \"" + phrase + "\" -o " + path;
		cli.execute(logger, command);
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.SWIFT;
	}
}
