package objectDatabase.componentTree.nodeBehaviour;

import interaction.arduino.Arduino;

import java.util.HashMap;

import objectDatabase.ObjectDatabaseUtilities;
import objectDatabase.componentTree.components.DB_Node;
import objectDatabase.componentTree.nodeBehaviour.NodeBehaviour.NodeBehaviourType;
import objectDatabase.componentTree.nodeBehaviour.behaviours.ArduinoDevice;
import objectDatabase.componentTree.nodeBehaviour.behaviours.Weather;
import utilities.logger.LogSource;
import utilities.logger.Logger;

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
