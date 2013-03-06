package utilities.externalSoftware.commandLine;

import java.io.IOException;

import utilities.logger.LogSource;
import utilities.logger.Logger;

public class Linux implements CommandLineInterface {

	@Override
	public void execute(Logger logger, String command) {
		// first write the wav file
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(command);

			try {
				process.waitFor();
			} catch (InterruptedException e) {
				logger.log("Problem accessing Linux Shell!", LogSource.ERROR, LogSource.SYNTHESIS_INFO, 1);
				e.printStackTrace();
			}
		} catch (IOException e) {
			logger.log("Problem accessing Linux Shell!", LogSource.ERROR, LogSource.SYNTHESIS_INFO, 1);
			e.printStackTrace();
		}
	}
}
