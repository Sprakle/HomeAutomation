package net.sprakle.homeAutomation.externalSoftware.commandLine.os;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class LinuxCLI implements CommandLineInterface {

	Logger logger;

	public LinuxCLI(Logger logger) {
		this.logger = logger;
	}

	@Override
	public ArrayList<String> execute(String command, int num) {
		logger.log("Executing Linux CLI command: \"" + command + "\" " + num + " times", LogSource.EXTERNAL_SOFTWARE, 2);

		String line = null;
		ArrayList<String> lines = new ArrayList<String>();

		ProcessBuilder builder = new ProcessBuilder("/bin/bash");
		builder.redirectErrorStream(true);

		Process process = null;
		try {
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
			error();
		}

		OutputStream stdin = process.getOutputStream();
		InputStream stdout = process.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

		try {
			if (command.trim().equals("exit")) {
				// Putting 'exit' amongst the echo --EOF--s below doesn't work.
				writer.write("exit\n");
			} else {
				for (int i = 0; i < num; i++)
					writer.write("((" + command + ") && echo --EOF--) || echo --EOF--\n");
			}
			writer.flush();

			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			error();
		}

		while (line != null && !line.trim().equals("--EOF--")) {
			logger.log("Terminal stream: '" + line + "'", LogSource.EXTERNAL_SOFTWARE, 1);
			lines.add(line);
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				error();
			}
		}

		return lines;
	}

	private void error() {
		logger.log("Error accesing terminal", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}
}
