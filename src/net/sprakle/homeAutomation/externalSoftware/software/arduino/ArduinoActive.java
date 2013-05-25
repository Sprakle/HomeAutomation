//TODO: Create a method of getting device names from a pin number

package net.sprakle.homeAutomation.externalSoftware.software.arduino;

import net.sprakle.arduinoInterface.ArduinoInterface;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.arduino.supporting.IncomingInteraction;
import net.sprakle.homeAutomation.externalSoftware.software.arduino.supporting.IncomingMode;
import net.sprakle.homeAutomation.externalSoftware.software.arduino.supporting.OutgoingMode;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

class ArduinoActive implements Arduino, LogicTimerObserver {

	private final Logger logger;

	private final ArduinoInterface ai;

	private final boolean[] digitalSubscriptions;
	private final int[] digitalValues;

	private final boolean[] analogueSubscriptions;
	private final int[] analogueValues;

	// max time to wait for serial response
	private final int SERIAL_TIMEOUT;

	ArduinoActive(Logger logger) {
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

		if (waitForArduinoStart(10, 500)) {
			logger.log("Arduino started and ready for commands", LogSource.ARDUINO, 1);
		} else {
			logger.log("Unable to start arduino", LogSource.ERROR, LogSource.ARDUINO, 1);
			return;
		}

		LogicTimer timer = LogicTimer.getLogicTimer();
		timer.addObserver(this);
	}

	@Override
	public int interact(OutgoingMode mode, int pin, int data) {
		int result = -1;

		if (!mode.isValid(pin, data)) {
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
		if (!analogueSubscriptions[pin]) {
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
		if (!digitalSubscriptions[pin]) {
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

				IncomingInteraction interaction = new IncomingInteraction(logger, input);

				IncomingMode replyMode = interaction.getMode();
				int replyPin = interaction.getPin();
				int replyData = interaction.getDataAsInteger();

				// ensure it's the info we requested
				if (replyMode == IncomingMode.VALUE && replyPin == pin) {
					response = replyData;
				} else {
					logger.log("Got unexpected serial message while expecting value: " + input, LogSource.ERROR, LogSource.ARDUINO, 1);
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

				IncomingInteraction interaction = new IncomingInteraction(logger, input);
				IncomingMode incomingMode = interaction.getMode();
				int incomingPin = interaction.getPin();
				if (incomingMode == IncomingMode.CONFIRMATION && incomingPin == pin)
					response = true;
				else
					logger.log("Got unexpected serial message while expecting confirmation", LogSource.ERROR, LogSource.ARDUINO, 1);
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
		IncomingInteraction interaction = new IncomingInteraction(logger, msg);

		IncomingMode mode = interaction.getMode();
		int pin = interaction.getPin();
		int value = interaction.getDataAsInteger();

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

	// returns once arduino is ready to accept commands
	private boolean waitForArduinoStart(int tries, int timeoutPerTry) {

		for (int i = 0; i < tries; i++) {
			ai.sendString("ch-00");

			long startTime = System.currentTimeMillis();
			while (true) {
				String input = ai.getInput();
				if (input != null && input.equals("cn-00-ready"))
					return true;

				long totalTime = System.currentTimeMillis() - startTime;
				if (totalTime > timeoutPerTry)
					break;

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}
	@Override
	public void advanceLogic() {
		String input = ai.getInput();
		if (input != null) {
			serialUpdate(input);
		}
	}
}