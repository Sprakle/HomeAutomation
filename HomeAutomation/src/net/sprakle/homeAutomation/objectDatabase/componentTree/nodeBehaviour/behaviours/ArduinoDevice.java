/*
 * Initialization arguments: 0: pin number
 */

package net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.behaviours;

import java.util.HashMap;

import net.sprakle.homeAutomation.interaction.arduino.Arduino;
import net.sprakle.homeAutomation.interaction.arduino.Arduino.Technology;
import net.sprakle.homeAutomation.objectDatabase.NodeType;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ArduinoDevice extends NodeBehaviour {
	Arduino arduino;

	int pin;

	Technology technology;

	public ArduinoDevice(Logger logger, Arduino arduino, DB_Node parent, HashMap<String, String> args) {
		super(logger, parent, args);
		this.arduino = arduino;

		System.out.println("set arduino");

		//get technology
		String techString = args.get(arduino.ARG_TECHNOLOGY);
		switch (techString) {
			case "digital_read":
				technology = arduino.DIGITAL_READ;
				break;

			case "digital_write":
				technology = arduino.DIGITAL_WRITE;
				break;

			case "analogue_read":
				technology = arduino.ANALOGUE_READ;
				break;

			case "analogue_write":
				technology = arduino.ANALOGUE_WRITE;
				break;

			default:
				logger.log("Invalid arguments in database orginization for node behaviour of arduino device '" + parent.getParent().getIdentifier() + "'", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
				break;
		}

		// get pin number
		try {
			pin = Integer.parseInt(args.get(arduino.ARG_PIN));
		} catch (NumberFormatException e) {
			logger.log("Invalid arguments in database orginization for node behaviour of arduino device '" + parent.getParent().getIdentifier() + "'", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		}

	}

	// integer reads will always be analogue
	@Override
	public Integer readInteger() {
		return arduino.interact(arduino.ANALOGUE_READ, pin, -1);
	}

	// integer writes will always be analogue
	@Override
	public void writeInteger(Integer write) {
		Technology tech = arduino.ANALOGUE_WRITE;
		arduino.interact(tech, pin, write);
	}

	@Override
	public void writeBinary(Boolean write) {
		int binary;
		if (write)
			binary = 1;
		else
			binary = 0;

		arduino.interact(arduino.DIGITAL_WRITE, pin, binary);
	}

	@Override
	public Boolean readBinary() {
		return arduino.interact(arduino.DIGITAL_READ, pin, -1) == 1; // returns true if 1, false if 0
	}

	@Override
	protected String readString() {
		return null;
	}

	@Override
	protected void writeString(String write) {
	}

	public Technology getTech() {
		return technology;
	}

	public int getPin() {
		return pin;
	}

	@Override
	protected NodeType getNodeType() {
		// find out what we accepts based on the args from the database file
		String techArg = args.get(Arduino.ARG_TECHNOLOGY);
		switch (techArg) {
			case "digital_read":
				return NodeType.BINARY;

			case "digital_write":
				return NodeType.BINARY;

			case "analogue_read":
				return NodeType.INTEGER;

			case "analogue_write":
				return NodeType.INTEGER;
		}

		return null;
	}
}
