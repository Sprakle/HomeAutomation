package net.sprakle.homeAutomation.utilities.logger;

import net.sprakle.homeAutomation.main.Config;

/**
 * Logging is resource intensive, taking from 1 - 10 ms on average. Do not call
 * log() if your process must be executed quickly or many times
 * 
 * @author ben
 * 
 */
public class Logger {

	private final LoggerGUI gui;

	private final Long initialTime = System.currentTimeMillis();

	private boolean shouldLog = false;
	private boolean crashOnError = false;

	// limit to the detail of log printing. 0 is infinite
	private final int VERBOSITY = Config.getInt("config/logger/verbosity");

    public Logger() {
		gui = new LoggerGUI();

		shouldLog = Config.getBinary("config/system/enable_logging");
		crashOnError = Config.getBinary("config/system/crash_on_error");
	}

	// used for only one source
	public synchronized void log(String text, LogSource source, int verbosity) {
		if (!shouldLog)
			return;

		if (!checkLogCall(text, source, null, verbosity))
			return;

		executeLog(text, source, verbosity);
	}

	// used to provide more details on log. EX: log("Something bad happened!", LogSource.ERROR, LogSource.DATABASE, 1);
	public synchronized void log(String text, LogSource source, LogSource secondarySource, int verbosity) {
		if (!shouldLog)
			return;

		if (!checkLogCall(text, source, secondarySource, verbosity))
			return;

		text = "(" + secondarySource + ") " + text;

		executeLog(text, source, verbosity);
	}

	// sanity check + check if error
	private boolean checkLogCall(String text, LogSource source, LogSource secondarySource, int verbosity) {
		if (text == null)
			return false;

		if (source == LogSource.ERROR) {
			executeLog(text, source, verbosity);
			printToConsole("Fatal error (" + secondarySource + "): " + text);

			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (crashOnError)
				System.exit(1);
		}

		return true;
	}

	private void executeLog(String text, LogSource source, int verbosity) {
		if (verbosity < VERBOSITY || VERBOSITY == 0) {
			printToConsole(text);

			// print to GUI
			gui.println(text, source);
		}
	}

	private void printToConsole(String text) {
		// print '>' to signal it's coming from the logger
		String time = addLeadingZeros(Math.round(System.currentTimeMillis() - initialTime), 8);
		System.out.println(time + " > " + text);
	}

	private String addLeadingZeros(int number, int targetLength) {
		StringBuilder s = new StringBuilder(targetLength);
		int zeroes = targetLength - (int) (Math.log(number) / Math.log(10)) - 1;
		for (int i = 0; i < zeroes; i++) {
			s.append(0);
		}
		return s.append(number).toString();
	}
}
