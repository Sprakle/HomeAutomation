package net.sprakle.homeAutomation.externalSoftware.software.arduino.supporting;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class IncomingInteraction {

	private final Logger logger;

	private final IncomingMode mode;
	private final int pin;
	private final String data;

	public IncomingInteraction(Logger logger, String msg) {
		this.logger = logger;

		mode = getIncomingMode(msg);
		pin = getPinInt(msg);
		data = getDataString(msg);

		if (mode == null)
			logger.log("Invalid mode: " + msg, LogSource.ERROR, LogSource.ARDUINO, 1);

		if (pin == -1)
			logger.log("Invalid mode: " + msg, LogSource.ERROR, LogSource.ARDUINO, 1);
	}

	private IncomingMode getIncomingMode(String msg) {
		String mode = null;
		int pin = -1;
		String data = null;

		mode = getModeString(msg);
		pin = getPinInt(msg);
		data = getDataString(msg);

		for (IncomingMode im : IncomingMode.values())
			if (im.isValid(mode, pin, data))
				return im;

		return null;
	}

	private String getModeString(String msg) {
		return msg.substring(0, 2);
	}

	private int getPinInt(String msg) {
		String pinString = msg.substring(3, 5);
		if (!pinString.matches("\\d*")) {
			logger.log("Invalid integer value in interaction: " + msg, LogSource.ERROR, LogSource.ARDUINO, 1);
			return -1;
		}

		return Integer.parseInt(pinString);
	}

	private String getDataString(String msg) {
		if (msg.length() > 5)
			return msg.substring(6);
		else
			return null;
	}

	public IncomingMode getMode() {
		return mode;
	}

	public int getPin() {
		return pin;
	}

	public String getData() {
		return data;
	}

	public int getDataAsInteger() {
		if (!data.matches("\\d*")) {
			logger.log("Unable to convert data to integer: " + data, LogSource.ERROR, LogSource.ARDUINO, 1);
			return -1;
		}

		return Integer.parseInt(data);
	}
}
