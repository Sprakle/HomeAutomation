/*
 * Used to control physical devices connected via arduino

 * 
 * Pin Constraints:
 * 		Digital Read:	0-15
 * 		Digital Write:	0-7
 * 		Analogue Read / Write:	0-5
 */

package net.sprakle.homeAutomation.interaction.arduino;

import net.sprakle.arduinoInterface.ArduinoInterface;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.speech.synthesis.Synthesis;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.DynamicResponder;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.ResponseType;

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
				break;

			case DIGITAL_WRITE:
				ai.sendString("dw-" + String.format("%02d", pin) + "-" + interaction);
				break;

			case ANALOGUE_READ:
				ai.sendString("ar-" + String.format("%02d", pin));
				break;

			case ANALOGUE_WRITE:
				ai.sendString("aw-" + String.format("%02d", pin) + "-" + interaction);
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

	private void serialUpdate(String msg) {

		logger.log("Recieved serial data from ardiono: '" + msg + "'", LogSource.ARDUINO, 2);

		if (isSerialAcceptable(msg) && msg.length() < 4) {
			logger.log("Received invalid message from arduino", LogSource.ERROR, LogSource.ARDUINO, 1);
			return;
		}

		String mode = msg.substring(0, 2);
		String pin = msg.substring(3, 5);
		String data = msg.substring(6);

		switch (mode) {
			case "cn":
				String reply = null;

				if (data.equals("1"))
					reply = DynamicResponder.reply(ResponseType.ACTIVATED) + " pin " + pin;
				else if (data.equals("0"))
					reply = DynamicResponder.reply(ResponseType.DEACTIVATED) + " pin " + pin;

				synth.speak(reply);
				break;

			case "va":
				synth.speak(data);
				break;

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
		char c = msg.charAt(5);
		String d = msg.substring(6);

		// should be a mode
		if (a != "cn" && a != "va" && a != "xx" && a != "db") {
			return false;
		}

		// should be a dash
		if (b != '-') {
			return false;
		}

		// should be dash if data follows
		if (d != "" && c != '-') {
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