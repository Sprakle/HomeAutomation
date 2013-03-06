package net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour;


import java.util.HashMap;

import net.sprakle.homeAutomation.interaction.arduino.Arduino;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabaseUtilities;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour.NodeBehaviourType;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.behaviours.ArduinoDevice;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.behaviours.Weather;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class NodeBehaviourFactory {
	public static NodeBehaviour createBehaviour(Logger logger, Arduino arduino, DB_Node parent, HashMap<String, String> args, String originalDefinition) {
		logger.log("Creating new behaviour with definition: " + originalDefinition.trim(), LogSource.OD_NODE_BEHAVIOUR, 2);

		NodeBehaviour behaviour = null;

		NodeBehaviourType type;

		// parse for behaviour type
		type = ObjectDatabaseUtilities.parseForBehaviourType(logger, originalDefinition);

		// which type of behaviour should we create?
		switch (type) {
			case ARDUINO_DEVICE:
				logger.log("behaviour was of type ARDUINO_DEVICE. Creating...", LogSource.OD_NODE_BEHAVIOUR, 3);
				behaviour = new ArduinoDevice(logger, arduino, parent, args);
				break;

			case WEATHER:
				logger.log("behaviour was of type WEATHER. Creating...", LogSource.OD_NODE_BEHAVIOUR, 3);
				behaviour = new Weather(logger, parent, args);
				break;

			default:
				logger.log("No logic defined for BehaviourType '" + type + "'. Get the programmer to add that logic.", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
				break;
		}

		logger.log("Finished creating new behaviour", LogSource.OD_NODE_BEHAVIOUR, 2);
		return behaviour;
	}
}
