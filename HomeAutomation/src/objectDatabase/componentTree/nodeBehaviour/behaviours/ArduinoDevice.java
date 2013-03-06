/*
 * Initialization arguments: 0: pin number
 */

package objectDatabase.componentTree.nodeBehaviour.behaviours;

import interaction.arduino.Arduino;
import interaction.arduino.Arduino.Technology;

import java.util.HashMap;

import objectDatabase.NodeType;
import objectDatabase.componentTree.components.DB_Node;
import objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import utilities.logger.LogSource;
import utilities.logger.Logger;

public class ArduinoDevice extends NodeBehaviour {
	Arduino arduino;

	int pin;

	Technology technology;

	public ArduinoDevice(Logger logger, Arduino arduino, DB_Node parent, HashMap<String, String> args) {
		super(logger, parent, args);
		this.arduino = arduino;

		//get technology
		String techString = args.get(Arduino.ArgumentMappings.TECHNOLOGY);
		switch (techString) {
			case "dr":
				technology = Arduino.DIGITAL_READ;
				break;

			case "dw":
				technology = Arduino.DIGITAL_WRITE;
				break;

			case "ar":
				technology = Arduino.ANALOGUE_READ;
				break;

			case "aw":
				technology = Arduino.ANALOGUE_WRITE;
				break;

			default:
				logger.log("Invalid arguments in database orginization for node behaviour of arduino device '" + parent.getParent().getIdentifier() + "'", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
				break;
		}

		// get pin number
		try {
			pin = Integer.parseInt(args.get(Arduino.ArgumentMappings.PIN));
		} catch (NumberFormatException e) {
			logger.log("Invalid arguments in database orginization for node behaviour of arduino device '" + parent.getParent().getIdentifier() + "'", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		}

	}

	// integer reads will always be analogue
	@Override
	public Integer readInteger() {
		return arduino.interact(Arduino.ANALOGUE_READ, pin, -1);
	}

	// integer writes will always be analogue
	@Override
	public void writeInteger(Integer write) {
		Technology tech = Arduino.ANALOGUE_WRITE;
		arduino.interact(tech, pin, write);
	}

	@Override
	public void writeBinary(Boolean write) {
		int binary;
		if (write)
			binary = 1;
		else
			binary = 0;

		arduino.interact(Arduino.DIGITAL_WRITE, pin, binary);
	}

	@Override
	public Boolean readBinary() {
		return arduino.interact(Arduino.DIGITAL_READ, pin, -1) == 1; // returns true if 1, false if 0
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
		String techArg = args.get(Arduino.ArgumentMappings.TECHNOLOGY);
		switch (techArg) {
			case "dr":
				return NodeType.BINARY;

			case "dw":
				return NodeType.BINARY;

			case "ar":
				return NodeType.INTEGER;

			case "aw":
				return NodeType.INTEGER;
		}

		return null;
	}

}
