/*
 * Used to control physical devices connected via arduino

 * 
 * Pin Constraints:
 * 		Digital Read:	0-15
 * 		Digital Write:	0-7
 * 		Analogue Read / Write:	0-5
 */

//TODO: Create a method of getting device names from a pin number

package net.sprakle.homeAutomation.interaction.arduino;

import net.sprakle.arduinoInterface.ArduinoInterface;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.DynamicResponder;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.Response;

public class Arduino implements LogicTimerObserver {

	// public db file argument mappings
	public static final String ARG_TECHNOLOGY = "tech";
	public static final String ARG_PIN = "pin";

	// public constraints (MIN SHOULD ALWAYS BE ZERO)
	public final int MIN_DIGITAL_READ_PIN;
	public final int MAX_DIGITAL_READ_PIN;
	public final int MIN_DIGITAL_WRITE_PIN;
	public final int MAX_DIGITAL_WRITE_PIN;

	public final int MIN_ANALOGUE_READ_PIN;
	public final int MAX_ANALOGUE_READ_PIN;
	public final int MIN_ANALOGUE_WRITE_PIN;
	public final int MAX_ANALOGUE_WRITE_PIN;

	// public technologies
	public final Technology DIGITAL_READ = Technology.DIGITAL_READ;
	public final Technology DIGITAL_WRITE = Technology.DIGITAL_WRITE;
	public final Technology ANALOGUE_READ = Technology.ANALOGUE_READ;
	public final Technology ANALOGUE_WRITE = Technology.ANALOGUE_WRITE;

	private Logger logger;
	private Synthesis synth;

	private ArduinoInterface ai;

	// max time to wait for serial response
	private final int SERIAL_TIMEOUT;

	public Arduino(Logger logger, Synthesis synth) {
		this.logger = logger;
		this.synth = synth;

		// apply configuration
		MIN_DIGITAL_READ_PIN = Config.getInt("config/arduino/min_digital_read_pin");
		MAX_DIGITAL_READ_PIN = Config.getInt("config/arduino/max_digital_read_pin");
		MIN_DIGITAL_WRITE_PIN = Config.getInt("config/arduino/min_digital_write_pin");
		MAX_DIGITAL_WRITE_PIN = Config.getInt("config/arduino/max_digital_write_pin");

		MIN_ANALOGUE_READ_PIN = Config.getInt("config/arduino/min_analogue_read_pin");
		MAX_ANALOGUE_READ_PIN = Config.getInt("config/arduino/max_analogue_read_pin");
		MIN_ANALOGUE_WRITE_PIN = Config.getInt("config/arduino/min_analogue_write_pin");
		MAX_ANALOGUE_WRITE_PIN = Config.getInt("config/arduino/max_analogue_write_pin");

		SERIAL_TIMEOUT = Config.getInt("config/arduino/serial_timeout");

		ai = new ArduinoInterface();

		LogicTimer timer = LogicTimer.getLogicTimer();
		timer.addObserver(this);
	}

	/*
	 * This is the one command to control arduino devices.
	 * tech: the method of interaction
	 * pin:	 the physical pin number of the arduino to interact with
	 * interaction: what should happen (if this is a READ, interaction should be -1
	 * 
	 * response: if this is a read, the current value of a pin (returns last set value if it's a WRITE pin)
	 */
	public int interact(Technology tech, int pin, int interaction) {
		logger.log("Interacting with Arduino:", LogSource.ARDUINO, 1);
		logger.log("    Technology: " + tech.name(), LogSource.ARDUINO, 2);
		logger.log("    Pin: " + pin, LogSource.ARDUINO, 2);
		logger.log("    Interaction: " + interaction, LogSource.ARDUINO, 2);

		int response = -1;

		// Verify what was requested is possible
		if (!isPossible(tech, pin, interaction, true)) {
			String error = "It is impossible to use set " + interaction + " using " + tech.name();
			logger.log(error, LogSource.ERROR, LogSource.ARDUINO, 1);
		}

		switch (tech) {
			case DIGITAL_READ:
				ai.sendString("dr-" + String.format("%02d", pin));

				// wait for response
				response = waitForSerialValue(pin);

				break;

			case DIGITAL_WRITE:
				ai.sendString("dw-" + String.format("%02d", pin) + "-" + interaction);

				// wait for confirmation
				if (waitForSerialConfirm(pin, interaction)) {
					String reply = null;

					if (interaction == 1)
						reply = DynamicResponder.reply(Response.ACTIVATED) + " pin " + pin;
					else if (interaction == 0)
						reply = DynamicResponder.reply(Response.DEACTIVATED) + " pin " + pin;

					synth.speak(reply);
				} else {
					logger.log("Unable to confirm digital write", LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				break;

			case ANALOGUE_READ:
				ai.sendString("ar-" + String.format("%02d", pin));

				// wait for response
				response = waitForSerialValue(pin);

				break;

			case ANALOGUE_WRITE:
				ai.sendString("aw-" + String.format("%02d", pin) + "-" + interaction);

				// wait for confirmation
				if (waitForSerialConfirm(pin, interaction)) {
					String reply = "set pin " + pin + " to " + interaction;
					synth.speak(reply);
				} else {
					logger.log("Unable to confirm digital write", LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				break;
		}

		/*
		 * All done!
		 */

		logger.log("Completed interaction with Arduino", LogSource.ARDUINO, 1);
		return response;
	}
	// checks if it is possible to have a specific pin, and then if the interaction to said pin is possible
	public Boolean isPossible(Technology tech, int pin, int interaction, Boolean checkInteraction) {

		Boolean possible = true;
		switch (tech) {
			case DIGITAL_READ:
				if (pin < MIN_DIGITAL_READ_PIN || pin > MAX_DIGITAL_READ_PIN)
					possible = false;

				if (checkInteraction && interaction != -1)
					possible = false;
				break;

			case DIGITAL_WRITE:
				if (pin < MIN_DIGITAL_WRITE_PIN || pin > MAX_DIGITAL_WRITE_PIN)
					possible = false;

				if (checkInteraction && interaction != 0 && interaction != 1)
					possible = false;
				break;

			case ANALOGUE_READ:
				if (pin < MIN_ANALOGUE_READ_PIN || pin > MAX_ANALOGUE_READ_PIN)
					possible = false;

				if (checkInteraction && interaction != -1)
					possible = false;
				break;

			case ANALOGUE_WRITE:
				if (pin < MIN_ANALOGUE_WRITE_PIN || pin > MAX_ANALOGUE_WRITE_PIN)
					possible = false;

				if (checkInteraction && (interaction < 0 || interaction > 255))
					possible = false;
				break;
		}

		return possible;
	}

	// used by digital and analogue read
	private int waitForSerialValue(int pin) {
		int response = -1;

		long startTime = System.currentTimeMillis();
		while (true) {

			String input = ai.getInput();
			if (input != null) {

				if (!isSerialAcceptable(input)) {
					logger.log("Recieved invalid response from arduino", LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				String replyMode = input.substring(0, 2);
				Integer replyPin = Integer.parseInt(input.substring(3, 5));
				Integer replyData = Integer.parseInt(input.substring(6));

				// ensure it's the info we requested
				if (replyMode.equals("va") && replyPin == pin) {
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
	private boolean waitForSerialConfirm(int pin, int interaction) {
		boolean response = false;

		long startTime = System.currentTimeMillis();
		while (true) {

			String input = ai.getInput();
			if (input != null) {

				if (!isSerialAcceptable(input)) {
					logger.log("Recieved invalid response from arduino", LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				String replyMode = input.substring(0, 2);
				Integer replyPin = Integer.parseInt(input.substring(3, 5));
				Integer replyData = Integer.parseInt(input.substring(6));

				// ensure it's the info we requested
				if (replyMode.equals("cn") && replyPin == pin && replyData == interaction) {
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

	// called if an unexpected serial string is received
	private void serialUpdate(String msg) {

		logger.log("Recieved unexpected serial message: " + msg, LogSource.WARNING, LogSource.ARDUINO, 1);

		if (!isSerialAcceptable(msg)) {
			logger.log("Received invalid message from arduino", LogSource.ERROR, LogSource.ARDUINO, 1);
			return;
		}

		String mode = msg.substring(0, 2);
		String data = msg.substring(6);

		switch (mode) {
			case "xx":
				synth.speak("Arduino encountered a critical error");
				logger.log("Arduino encountered a critical error", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;

			case "db":
				logger.log("Arduino debug: " + data, LogSource.ARDUINO, 2);
				break;
		}
	}

	boolean isSerialAcceptable(String msg) {
		// initial sanity check
		if (msg.length() < 4) {
			return false;
		}

		String a = msg.substring(0, 2);
		char b = msg.charAt(2);

		// should be a mode
		if (!a.equals("cn") && !a.equals("va") && !a.equals("xx") && !a.equals("db")) {
			return false;
		}

		// should be a dash
		if (b != '-') {
			return false;
		}

		return true;
		// after index 5 can be anything
	}

	// different technologies used for real-word interaction by the arduino
	public enum Technology {
		DIGITAL_READ,
		DIGITAL_WRITE,
		ANALOGUE_READ,
		ANALOGUE_WRITE;
	}

	@Override
	public void advanceLogic() {
		String input = ai.getInput();
		if (input != null) {
			serialUpdate(input);
		}
	}
}