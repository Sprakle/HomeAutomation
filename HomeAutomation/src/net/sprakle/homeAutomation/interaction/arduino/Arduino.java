//TODO: Create a method of getting device names from a pin number

package net.sprakle.homeAutomation.interaction.arduino;

import net.sprakle.arduinoInterface.ArduinoInterface;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Arduino implements LogicTimerObserver {

	private Logger logger;

	private ArduinoInterface ai;

	private boolean[] digitalSubscriptions;
	private int[] digitalValues;

	private boolean[] analogueSubscriptions;
	private int[] analogueValues;

	// max time to wait for serial response
	private final int SERIAL_TIMEOUT;

	// public db file argument mappings
	public static final String ARG_TECHNOLOGY = "tech";
	public static final String ARG_PIN = "pin";

	public Arduino(Logger logger) {
		this.logger = logger;

		SERIAL_TIMEOUT = Config.getInt("config/arduino/serial_timeout");
		ai = new ArduinoInterface();

		int minDigitalPin = Config.getInt("config/arduino/min_digital_pin");
		int maxDigitalPin = Config.getInt("config/arduino/max_digital_pin");
		int minAnaloguePin = Config.getInt("config/arduino/min_analogue_pin");
		int maxAnaloguePin = Config.getInt("config/arduino/max_analogue_pin");

		digitalSubscriptions = new boolean[maxDigitalPin - minDigitalPin];
		digitalValues = new int[maxDigitalPin - minDigitalPin];
		analogueSubscriptions = new boolean[maxAnaloguePin - minAnaloguePin];
		analogueValues = new int[maxAnaloguePin - minAnaloguePin];

		LogicTimer timer = LogicTimer.getLogicTimer();
		timer.addObserver(this);
	}

	public int interact(OutgoingMode mode, int pin, int data) {
		int result = -1;

		if (mode.isValid(pin, data) == false) {
			logger.log("Invalid outgoing interaction: " + mode, LogSource.ERROR, LogSource.ARDUINO, 1);
			return -1;
		}

		switch (mode) {
			case ANALOGUE_READ: {
				return analogueRead(pin);
			}

			case ANALOGUE_WRITE: {
				String modeS = OutgoingMode.ANALOGUE_WRITE.getInSerial();
				ai.sendString(modeS + "-" + String.format("%02d", pin) + "-" + data);
				if (waitForSerialConfirm(pin))
					logger.log("Set pin " + pin + " to " + data, LogSource.ARDUINO, 2);
				else
					logger.log("Unable to confirm analogue write", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;
			}

			case ANALOGUE_SUBSCRIBE: {
				String modeS = OutgoingMode.ANALOGUE_SUBSCRIBE.getInSerial();
				ai.sendString(modeS + "-" + String.format("%02d", pin));
				if (!waitForSerialConfirm(pin))
					logger.log("Unable to confirm analogue subscription", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;
			}

			case DIGITAL_READ: {
				return digitalRead(pin);
			}

			case DIGITAL_WRITE: {
				String modeS = OutgoingMode.DIGITAL_WRITE.getInSerial();
				ai.sendString(modeS + "-" + String.format("%02d", pin) + "-" + data);
				if (waitForSerialConfirm(pin))
					logger.log("Set pin " + pin + " to " + data, LogSource.ARDUINO, 2);
				else
					logger.log("Unable to confirm digital write", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;
			}

			case DIGITAL_SUBSCRIBE: {
				String modeS = OutgoingMode.DIGITAL_SUBSCRIBE.getInSerial();
				ai.sendString(modeS + "-" + String.format("%02d", pin));
				if (!waitForSerialConfirm(pin))
					logger.log("Unable to confirm digital subscription", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;
			}

			case EMERGENCY_STOP:
				String modeS = OutgoingMode.EMERGENCY_STOP.getInSerial();
				ai.sendString(modeS + "-" + String.format("%02d", pin) + "-" + data);
				break;

			default:
				logger.log("Unsuported outgoing operation", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;
		}

		return result;
	}

	private int analogueRead(int pin) {
		if (analogueSubscriptions[pin] == false) {
			// create new subscription
			interact(OutgoingMode.ANALOGUE_SUBSCRIBE, pin, -1);
			analogueSubscriptions[pin] = true;

			// create first update
			ai.sendString(OutgoingMode.ANALOGUE_READ.getInSerial() + "-" + String.format("%02d", pin));
			analogueValues[pin] = waitForSerialValue(pin);
		}

		return analogueValues[pin];
	}

	private int digitalRead(int pin) {
		if (digitalSubscriptions[pin] == false) {
			// create new subscription
			interact(OutgoingMode.DIGITAL_SUBSCRIBE, pin, -1);
			digitalSubscriptions[pin] = true;

			// create first update
			ai.sendString(OutgoingMode.DIGITAL_READ.getInSerial() + "-" + String.format("%02d", pin));
			digitalValues[pin] = waitForSerialValue(pin);
		}

		return digitalValues[pin];
	}

	// used by digital and analogue read
	private int waitForSerialValue(int pin) {
		int response = -1;

		long startTime = System.currentTimeMillis();
		while (true) {

			String input = ai.getInput();
			if (input != null) {

				if (getIncomingMode(input) == null) {
					logger.log("Recieved invalid response from arduino: " + input, LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				String replyMode = input.substring(0, 2);
				int replyPin = Integer.parseInt(input.substring(3, 5));
				int replyData = Integer.parseInt(input.substring(6));

				// ensure it's the info we requested
				if (IncomingMode.VALUE.isValid(replyMode, replyPin, input.substring(6)) && replyPin == pin) {
					response = replyData;
				} else {
					logger.log("Got unexpected serial message while expecting value", LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				break;
			}

			long currTime = System.currentTimeMillis();
			if (currTime - startTime > SERIAL_TIMEOUT) {
				logger.log("Serial response timed out!", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	// used by digital and analogue write. returns true of confirmation was successful
	private boolean waitForSerialConfirm(int pin) {
		boolean response = false;

		long startTime = System.currentTimeMillis();
		while (true) {

			String input = ai.getInput();
			if (input != null) {

				if (getIncomingMode(input) == null) {
					logger.log("Recieved invalid response from arduino: " + input, LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				String replyMode = input.substring(0, 2);
				int replyPin = Integer.parseInt(input.substring(3, 5));

				// ensure it's the info we requested
				if (replyMode.equals("cn") && replyPin == pin) {
					response = true;
				} else {
					logger.log("Got unexpected serial message while expecting confirmation", LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				break;
			}

			long currTime = System.currentTimeMillis();
			if (currTime - startTime > SERIAL_TIMEOUT) {
				logger.log("Serial response timed out!", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	// called if an unexpected serial string is received, such as a subscription update
	private void serialUpdate(String msg) {
		IncomingMode mode = getIncomingMode(msg);
		if (mode == null) {
			logger.log("Recieved invalid serial data: " + msg, LogSource.ERROR, LogSource.ARDUINO, 1);
			return;
		}

		int pin = Integer.parseInt(msg.substring(3, 5));
		int value = Integer.parseInt(msg.substring(6));

		switch (mode) {
			case ANALOGUE_UPDATE:
				analogueValues[pin] = value;
				break;

			case DIGITAL_UPDATE:
				digitalValues[pin] = value;
				break;

			case DEBUG:
				logger.log("Arduino debug: " + msg, LogSource.ARDUINO, 1);
				break;

			case FALIURE:
				logger.log("Arduino reported failiure: " + msg, LogSource.ERROR, LogSource.ARDUINO, 1);
				break;

			default:
				logger.log("Recieved unexpected serial data: " + msg, LogSource.WARNING, LogSource.ARDUINO, 1);
				return;
		}
	}

	IncomingMode getIncomingMode(String msg) {
		String mode = null;
		int pin = -1;
		String data = null;

		mode = msg.substring(0, 2);

		String pinString = msg.substring(3, 5);
		if (!pinString.matches("\\d*")) {
			return null;
		}

		pin = Integer.parseInt(pinString);

		// get data if it exists
		if (msg.length() > 5)
			data = msg.substring(6);

		for (IncomingMode im : IncomingMode.values())
			if (im.isValid(mode, pin, data))
				return im;

		return null;
	}

	@Override
	public void advanceLogic() {
		String input = ai.getInput();
		if (input != null) {
			serialUpdate(input);
		}
	}
}