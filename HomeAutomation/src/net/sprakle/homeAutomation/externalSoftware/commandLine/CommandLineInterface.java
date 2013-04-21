package net.sprakle.homeAutomation.externalSoftware.commandLine;

import java.util.ArrayList;

public interface CommandLineInterface {
	/**
	 * Execute c command on the local OS shell
	 * 
	 * @param command
	 *            Commands as it would be typed into the terminal / shell
	 * @param num
	 *            Number of times to run the command
	 * 
	 * @return Array of each printed lines. Returning from multiple iterations
	 *         not supported
	 */
	public ArrayList<String> execute(String command, int num);
}
