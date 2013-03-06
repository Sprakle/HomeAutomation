package net.sprakle.homeAutomation.utilities.logger;


public class Logger {

	LoggerGUI gui;

	Long initialTime = System.currentTimeMillis();
	Long prevTime = System.currentTimeMillis();

	// limit to the detail of log printing. 0 is infinite. Must be >= 0 || <= 3
	int verbosityLimit = 0;

	public Logger() {
		gui = new LoggerGUI();
	}

	// used for only one source
	public void log(String text, LogSource source, int verbosity) {

		text = text.trim();

		// If there is an error, create an alert
		if (source == LogSource.ERROR) {
			System.exit(1);
		}

		if (verbosity < verbosityLimit || verbosityLimit == 0) {
			// print '>' to signal it's coming from the logger
			String time = addLeadingZeros(Math.round(System.currentTimeMillis() - initialTime), 8);
			System.out.println(time + " > " + text);

			// print to GUI
			gui.println(text, source);
		}
	}

	// used to provide more details on log. EX: log("Something bad happened!", LogSource.ERROR, LogSource.DATABASE, 1);
	public void log(String text, LogSource source, LogSource secondarySource, int verbosity) {

		text = "(" + secondarySource + ") " + text.trim();

		// If there is an error, create an alert
		if (source == LogSource.ERROR) {
			System.exit(1);
		}

		if (verbosity < verbosityLimit || verbosityLimit == 0) {
			// print '>' to signal it's coming from the logger
			String time = addLeadingZeros(Math.round(System.currentTimeMillis() - initialTime), 8);
			System.out.println(time + " > " + text);

			// print to GUI
			gui.println(text, source);
		}
	}

	private String addLeadingZeros(int number, int targetLength) {
		StringBuffer s = new StringBuffer(targetLength);
		int zeroes = targetLength - (int) (Math.log(number) / Math.log(10)) - 1;
		for (int i = 0; i < zeroes; i++) {
			s.append(0);
		}
		return s.append(number).toString();
	}
}
