/*
 * Initialization arguments: 0: pin number
 */

package net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.behaviours;

import java.util.ArrayList;
import java.util.HashMap;

import net.sprakle.homeAutomation.interaction.arduino.Arduino;
import net.sprakle.homeAutomation.interaction.arduino.Arduino.Technology;
import net.sprakle.homeAutomation.interaction.objectDatabase.NodeType;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ArduinoDevice extends NodeBehaviour {
	Arduino arduino;

	int pin;

	Technology technology;

	public ArduinoDevice(Logger logger, Arduino arduino, DB_Node parent, HashMap<String, String> args) {
		super(logger, parent, args);
		this.arduino = arduino;

		//get technology
		String techString = args.get(Arduino.ARG_TECHNOLOGY);
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
			pin = Integer.parseInt(args.get(Arduino.ARG_PIN));
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

		if (technology == Technology.DIGITAL_WRITE)
			arduino.interact(arduino.DIGITAL_WRITE, pin, binary);

		else if (technology == Technology.ANALOGUE_WRITE) // if a binary signal is sent to an analogue device, it will go full on (255) of full off (0)
			arduino.interact(arduino.ANALOGUE_WRITE, pin, binary * 255);
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
	protected ArrayList<NodeType> getAcceptedNodeReadTypes() {
		ArrayList<NodeType> acceptedTypes = new ArrayList<NodeType>();

		// find out what we accept based on the args from the database file
		String techArg = args.get(Arduino.ARG_TECHNOLOGY);
		switch (techArg) {
			case "digital_read":
				acceptedTypes.add(NodeType.BINARY);
				break;

			case "digital_write":
				break;

			case "analogue_read":
				acceptedTypes.add(NodeType.INTEGER);
				break;

			case "analogue_write":
				break;
		}

		return acceptedTypes;
	}

	@Override
	protected ArrayList<NodeType> getAcceptedNodeWriteTypes() {
		ArrayList<NodeType> acceptedTypes = new ArrayList<NodeType>();

		// find out what we accept based on the args from the database file
		String techArg = args.get(Arduino.ARG_TECHNOLOGY);
		switch (techArg) {
			case "digital_read":
				break;

			case "digital_write":
				acceptedTypes.add(NodeType.BINARY);
				break;

			case "analogue_read":
				break;

			case "analogue_write":
				acceptedTypes.add(NodeType.INTEGER);
				acceptedTypes.add(NodeType.BINARY); // analogue can also be set to 1 or 0
				break;
		}

		return acceptedTypes;
	}
}