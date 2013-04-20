package net.sprakle.homeAutomation.externalSoftware.commandLine;

public interface CommandLineInterface {
	/**
	 * Execute c command on the local OS shell
	 * 
	 * @param command
	 *            Commands as it would be typed into the terminal / shell
	 * @param num
	 *            Number of times to run the command
	 */
	public void execute(String command, int num);
}
