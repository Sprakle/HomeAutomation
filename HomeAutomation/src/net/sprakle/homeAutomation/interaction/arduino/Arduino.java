/*
 * Used to control physical devices connected via arduino

 * 
 * Pin Constraints:
 * 		Digital Read:	0-15
 * 		Digital Write:	0-7
 * 		Analogue Read / Write:	0-5
 */

package net.sprakle.homeAutomation.interaction.arduino;

import net.sprakle.homeAutomation.events.Event;
import net.sprakle.homeAutomation.events.EventListener;
import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.speech.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Arduino implements EventListener {

	// public db file argument mappings
	public static class ArgumentMappings {
		public static final String TECHNOLOGY = "tech";
		public static final String PIN = "pin";
	}

	// public constraints (MIN SHOULD ALWAYS BE ZERO)
	public static final int MIN_DIGITAL_READ_PIN = 0;
	public static final int MAX_DIGITAL_READ_PIN = 7;
	public static final int MIN_DIGITAL_WRITE_PIN = 0;
	public static final int MAX_DIGITAL_WRITE_PIN = 15;

	public static final int MIN_ANALOGUE_READ_PIN = 0;
	public static final int MAX_ANALOGUE_READ_PIN = 5;
	public static final int MIN_ANALOGUE_WRITE_PIN = 0;
	public static final int MAX_ANALOGUE_WRITE_PIN = 5;

	// public technologies
	public static final Technology DIGITAL_READ = Technology.DIGITAL_READ;
	public static final Technology DIGITAL_WRITE = Technology.DIGITAL_WRITE;
	public static final Technology ANALOGUE_READ = Technology.ANALOGUE_READ;
	public static final Technology ANALOGUE_WRITE = Technology.ANALOGUE_WRITE;

	private Logger logger;
	private Synthesis synth;

	public Arduino(Logger logger, Synthesis synth) {
		this.logger = logger;
		this.synth = synth;

		// event listener for updates to the database file
		EventManager em = EventManager.getInstance(logger);
		em.addListener(EventType.DB_FILE_UPDATED, this);
	}

	@Override
	public void call(Event e) {
		// database file updated
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
				synth.speak("Digital read from pin " + pin);
				break;

			case DIGITAL_WRITE:
				synth.speak("Digital write " + interaction + " to pin " + pin);
				break;

			case ANALOGUE_READ:
				synth.speak("Digital read from pin " + pin);
				break;

			case ANALOGUE_WRITE:
				synth.speak("Analogue write " + interaction + " to pin " + pin);
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

	// different technologies used for real-word interaction by the arduino
	public enum Technology {
		DIGITAL_READ,
		DIGITAL_WRITE,
		ANALOGUE_READ,
		ANALOGUE_WRITE;
	}
}