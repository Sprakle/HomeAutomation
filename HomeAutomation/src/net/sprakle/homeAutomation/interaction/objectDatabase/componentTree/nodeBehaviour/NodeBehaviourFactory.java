package net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour;

import java.util.HashMap;
import java.util.Iterator;

import net.sprakle.homeAutomation.externalSoftware.software.arduino.Arduino;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour.NodeBehaviourType;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.behaviours.ArduinoDevice;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.behaviours.Weather;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class NodeBehaviourFactory {

	private Arduino arduino;

	// node behaviour factory will have the dependencies of each behaviour, so it should be easy to pass them around
	public NodeBehaviourFactory(Arduino arduino) {
		this.arduino = arduino;
	}

	public NodeBehaviour createBehaviour(Logger logger, DB_Node parent, Element behaviourElement) {

		// ensure we are parsing a  node behaviour
		if (!behaviourElement.getName().equals("NODE_BEHAVIOUR")) {
			logger.log("Can only parse NODE_BEHAVIOUR for types.", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		}

		NodeBehaviour behaviour = null;

		NodeBehaviourType type = parseElementForType(logger, behaviourElement);
		HashMap<String, String> args = parseElementForArgs(behaviourElement);

		// which type of behaviour should we create?
		switch (type) {
			case ARDUINO_DEVICE:
				behaviour = new ArduinoDevice(logger, arduino, parent, args);
				break;

			case WEATHER:
				behaviour = new Weather(logger, parent, args);
				break;

			default:
				logger.log("No logic defined for BehaviourType '" + type + "'. Get the programmer to add that logic.", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
				break;
		}
		return behaviour;
	}

	private NodeBehaviourType parseElementForType(Logger logger, Element e) {
		NodeBehaviourType result = null;

		String type = e.attributeValue("type");

		// make sure it's a real type
		boolean isRealType = false;
		for (NodeBehaviourType nbt : NodeBehaviourType.values()) {
			if (type.equals(nbt.name())) {
				isRealType = true;
				result = nbt;
				break;
			}
		}

		if (!isRealType) {
			logger.log("database XML file has a non real Node Behaviour Type", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		}

		return result;
	}

	private HashMap<String, String> parseElementForArgs(Element e) {
		HashMap<String, String> result = new HashMap<String, String>();

		// iterate through child elements of root
		for (Iterator<?> i = e.elementIterator(); i.hasNext();) {
			Element element = (Element) i.next();

			String flag = element.getName();
			String value = element.getText();
			result.put(flag, value);
		}

		return result;
	}
}
