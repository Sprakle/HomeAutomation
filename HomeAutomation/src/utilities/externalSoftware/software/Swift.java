/*
 * Swift is used to convert a string into a wav file.
 * 
 * Arguments:
 * 		[path/file] [phrase]
 * 
 * These swift commands happen to work on BOTH windows and linux
 */

package utilities.externalSoftware.software;

import utilities.externalSoftware.commandLine.CommandLineInterface;
import utilities.logger.Logger;

public class Swift extends SoftwareInterface {

	public Swift(Logger logger, CommandLineInterface cli) {
		super(logger, cli);
	}

	@Override
	public void execute(String[] args) {
		String path = args[0];
		String phrase = args[1];

		String command = "swift -n David \"" + phrase + "\" -o " + path;
		cli.execute(logger, command);
	}
}
