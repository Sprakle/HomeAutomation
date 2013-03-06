/*
 * Used to control physical devices connected via arduino

 * 
 * Pin Constraints:
 * 		Digital Read:	0-15
 * 		Digital Write:	0-7
 * 		Analogue Read / Write:	0-5
 */

package net.sprakle.homeAutomation.interaction.arduino;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import net.sprakle.homeAutomation.events.Event;
import net.sprakle.homeAutomation.events.EventListener;
import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.main.Constants;
import net.sprakle.homeAutomation.objectDatabase.ComponentType;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabaseUtilities;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour.NodeBehaviourType;
import net.sprakle.homeAutomation.speech.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.fileAccess.read.LineByLine;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.DynamicResponder;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.ResponseType;


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

	// assignments for each pin
	private int[] digitalReadAssignments;
	private int[] digitalWriteAssignments;
	private int[] analogueReadAssignments;
	private int[] analogueWriteAssignments;

	// cache line from db_org file
	private List<String> lines;

	public Arduino(Logger logger, Synthesis synth) {
		this.logger = logger;
		this.synth = synth;

		// event listener for updates to the database file
		EventManager em = EventManager.getInstance(logger);
		em.addListener(EventType.DB_FILE_UPDATED, this);

		logger.log("Assigning arduino pins", LogSource.ARDUINO, 2);
		lines = LineByLine.read(logger, Paths.get(Constants.odOrginizationFile));
		initializeAssignments();
	}

	@Override
	public void call(Event e) {
		lines = LineByLine.read(logger, Paths.get(Constants.odOrginizationFile));
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

			// is it that it doesn't exist, or it's a Tech problem
			if (isAssigned(tech, pin)) {
				String error = "It is impossible to use set " + interaction + " using " + tech.name();
				logger.log(error, LogSource.ERROR, LogSource.ARDUINO, 1);
			} else {

				// since this error can be user-generated and is not fatal, just warn the user
				String error = "Pin " + pin + " is not assigned for Technology " + tech.name();
				synth.speak(error);
			}
		}

		switch (tech) {
			case DIGITAL_READ:
				break;

			case DIGITAL_WRITE:
				if (interaction == 1)
					synth.speak(DynamicResponder.reply(ResponseType.ACTIVATED) + " the " + pinToDevice(tech, pin) + " " + pinToNode(tech, pin));
				else
					synth.speak(DynamicResponder.reply(ResponseType.DEACTIVATED) + " the " + pinToDevice(tech, pin) + " " + pinToNode(tech, pin));
				break;

			case ANALOGUE_READ:
				break;

			case ANALOGUE_WRITE:
				synth.speak("Set " + pinToDevice(tech, pin) + " " + pinToNode(tech, pin) + " to " + interaction);
				break;
		}

		/*
		 * All done!
		 */

		logger.log("Completed interaction with Arduino", LogSource.ARDUINO, 1);
		return response;
	}

	public Boolean isAssigned(Technology tech, int pin) {
		Boolean result = true;

		int[] targetAssignments = null;

		switch (tech) {
			case DIGITAL_READ:
				targetAssignments = digitalReadAssignments;
				break;

			case DIGITAL_WRITE:
				targetAssignments = digitalWriteAssignments;
				break;

			case ANALOGUE_READ:
				targetAssignments = analogueReadAssignments;
				break;

			case ANALOGUE_WRITE:
				targetAssignments = analogueWriteAssignments;
				break;
		}

		// make sure we wont go out of bounds
		if (pin < targetAssignments.length) {
			// now see if there is even a pin assigned there
			if (targetAssignments[pin] == 0) {
				result = false;
			}
		} else {
			result = false;
		}

		return result;
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

	// sets arduino pin assignments for internal reference. will later prepare the physical arduino device itself
	private void initializeAssignments() {
		/* 
		 * initialize assignments. Each behaviour has a pin and a technology. use that to initialize the assignments
		 */

		// initialize assignment arrays
		digitalReadAssignments = new int[MAX_DIGITAL_READ_PIN];
		digitalWriteAssignments = new int[MAX_DIGITAL_WRITE_PIN];
		analogueReadAssignments = new int[MAX_ANALOGUE_READ_PIN];
		analogueWriteAssignments = new int[MAX_ANALOGUE_WRITE_PIN];

		// get all lines from the dbOrg file
		List<String> lines = LineByLine.read(logger, Paths.get(Constants.odOrginizationFile));

		// for every node...
		for (String s : lines) {

			Boolean isNodeBehaviour = ObjectDatabaseUtilities.parseForComponentType(logger, s) == ComponentType.DB_NODE_BEHAVIOUR; // First see if it is a node behaviour before checking what kind
			if (isNodeBehaviour && ObjectDatabaseUtilities.parseForBehaviourType(logger, s) == NodeBehaviourType.ARDUINO_DEVICE) { // now check what kind it is

				int pin = parseForPin(s);
				Technology tech = parseForTech(s);

				// check if possible
				if (!isPossible(tech, pin, -1, false)) {
					logger.log("Impossible pin assignment by device: " + ObjectDatabaseUtilities.parseForComponentName(logger, s) + " for tech: " + tech + " pin: " + pin, LogSource.ERROR, LogSource.ARDUINO, 1);
				}

				// what technology is it?

				switch (tech) {
					case DIGITAL_READ:
						digitalReadAssignments[pin] = 1;
						break;

					case DIGITAL_WRITE:
						digitalWriteAssignments[pin] = 1;
						break;

					case ANALOGUE_READ:
						analogueReadAssignments[pin] = 1;
						break;

					case ANALOGUE_WRITE:
						analogueWriteAssignments[pin] = 1;
						break;
				}
			}
		}

		// report
		logger.log("Assigned pins:", LogSource.ARDUINO, 3);
		logger.log("    DigitalRead Assignments: " + listAssignments(digitalReadAssignments), LogSource.ARDUINO, 2);
		logger.log("    DigitalWrite Assignments: " + listAssignments(digitalWriteAssignments), LogSource.ARDUINO, 2);
		logger.log("    AnalogueRead Assignments: " + listAssignments(analogueReadAssignments), LogSource.ARDUINO, 2);
		logger.log("    AnaloguelWrite Assignments: " + listAssignments(analogueWriteAssignments), LogSource.ARDUINO, 2);

	}

	// parses ARDUINO_DEVICE line of DB ORG file for pin
	private int parseForPin(String line) {
		int pin = -1;

		HashMap<String, String> args = ObjectDatabaseUtilities.parseForArgs(logger, line);

		if (args.get(ArgumentMappings.PIN) == null) {
			logger.log("Invalid arguments in database orginization for node behaviour of arduino device: '" + line.trim() + "'", LogSource.ERROR, LogSource.ARDUINO, 1);
		}

		try {
			pin = Integer.parseInt(args.get(ArgumentMappings.PIN));
		} catch (NumberFormatException e) {
			logger.log("Invalid arguments in database orginization for node behaviour of arduino device: '" + line.trim() + "'", LogSource.ERROR, LogSource.ARDUINO, 1);
		}

		return pin;
	}

	// parses ARDUINO_DEVICE line of DB ORG file for Technology used
	public Technology parseForTech(String line) {
		Technology result = null;

		HashMap<String, String> args = ObjectDatabaseUtilities.parseForArgs(logger, line);

		String techString = args.get(ArgumentMappings.TECHNOLOGY);
		switch (techString) {
			case "dr":
				result = Arduino.DIGITAL_READ;
				break;

			case "dw":
				result = Arduino.DIGITAL_WRITE;
				break;

			case "ar":
				result = Arduino.ANALOGUE_READ;
				break;

			case "aw":
				result = Arduino.ANALOGUE_WRITE;
				break;

			default:
				logger.log("Invalid arguments in database orginization for node behaviour of arduino device: '" + line.trim() + "'", LogSource.ERROR, LogSource.ARDUINO, 1);
				break;
		}

		return result;
	}

	// return all arduino pin assignments in human-readable format
	private String listAssignments(int[] assignments) {
		String result = "";

		for (int i = 0; i < assignments.length; i++) {
			result += i + ":" + assignments[i] + " ";
		}

		return result;
	}

	// used the database organisation file to get the name of a device with a specific pin and tech used
	private String pinToDevice(Technology tech, int pin) {
		String device = null;

		// line number used to get parent of NodeBehaviour
		int lineNum = 0;
		int deviceLineNum = -1;

		// for every node...
		for (String s : lines) {

			Boolean isNodeBehaviour = ObjectDatabaseUtilities.parseForComponentType(logger, s) == ComponentType.DB_NODE_BEHAVIOUR; // First see if it is a node behaviour before checking what kind
			if (isNodeBehaviour && ObjectDatabaseUtilities.parseForBehaviourType(logger, s) == NodeBehaviourType.ARDUINO_DEVICE) { // now check what kind it is

				int linePin = parseForPin(s);
				Technology lineTech = parseForTech(s);

				if (linePin == pin && lineTech == tech) {
					// found the right NodeBehaviour line! Now back up until we find an Object (Object>Node>Behaviour)

					Boolean searching = true;
					int backTracked = 1; // how many lines we have backed up
					while (searching) {

						// what line are we one now?
						String thisLine = lines.get(lineNum - backTracked);

						if (ObjectDatabaseUtilities.parseForComponentType(logger, thisLine) == ComponentType.DB_OBJECT) {
							// found the right Object line! lets go!
							deviceLineNum = lineNum - backTracked;
							searching = false;
						}

						backTracked++;
					}
				}
			}

			lineNum++;
		}

		// make sure we found one before proceeding
		if (deviceLineNum != -1) {
			String deviceLine = lines.get(deviceLineNum);
			device = ObjectDatabaseUtilities.parseForComponentName(logger, deviceLine);
		}

		return device;
	}

	// used the database organisation file to get the node of a device with a specific pin and tech used
	private String pinToNode(Technology tech, int pin) {
		String node = null;

		// line number used to get parent of NodeBehaviour
		int lineNum = 0;
		int deviceLineNum = -1;

		// for every node...
		for (String s : lines) {

			Boolean isNodeBehaviour = ObjectDatabaseUtilities.parseForComponentType(logger, s) == ComponentType.DB_NODE_BEHAVIOUR; // First see if it is a node behaviour before checking what kind
			if (isNodeBehaviour && ObjectDatabaseUtilities.parseForBehaviourType(logger, s) == NodeBehaviourType.ARDUINO_DEVICE) { // now check what kind it is

				int linePin = parseForPin(s);
				Technology lineTech = parseForTech(s);

				if (linePin == pin && lineTech == tech) {
					// found the right NodeBehaviour line! Now back up until we find an Object (Object>Node>Behaviour)

					Boolean searching = true;
					int backTracked = 1; // how many lines we have backed up
					while (searching) {

						// what line are we one now?
						String thisLine = lines.get(lineNum - backTracked);

						if (ObjectDatabaseUtilities.parseForComponentType(logger, thisLine) == ComponentType.DB_NODE) {
							// found the right Node line! lets go!
							deviceLineNum = lineNum - backTracked;
							searching = false;
						}

						backTracked++;
					}
				}
			}

			lineNum++;
		}

		// make sure we found one before proceeding
		if (deviceLineNum != -1) {
			String deviceLine = lines.get(deviceLineNum);
			node = ObjectDatabaseUtilities.parseForComponentName(logger, deviceLine);
		}

		return node;
	}
}
