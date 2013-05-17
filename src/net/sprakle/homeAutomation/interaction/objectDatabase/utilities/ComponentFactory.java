package net.sprakle.homeAutomation.interaction.objectDatabase.utilities;

import net.sprakle.homeAutomation.interaction.objectDatabase.NodeType;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components.DB_Object;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.NodeBehaviourFactory;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

class ComponentFactory {
	static Component getComponent(Logger logger, NodeBehaviourFactory nbf, Component parent, String currentName, Element element) {
		Component component = null;

		if (element.getName().equals("OBJECT")) {
			component = new DB_Object(logger, parent, currentName);

		} else if (element.getName().equals("NODE")) {

			DB_Node node = new DB_Node(logger, parent, currentName);

			// nodes can have node behaviours, so check for those
			if (element.elements().size() == 1) {

				// get node behaviour
				Element behaviourElement = element.element("NODE_BEHAVIOUR");

				// ensure the node had a behaviour
				if (behaviourElement == null) {
					logger.log("Node did not have a node behaviour in database XML", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
				}

				// add node behaviour
				NodeBehaviour behaviour = nbf.createBehaviour(logger, node, behaviourElement);
				node.setBehaviour(behaviour);
			}

			// check if it is a default node
			String defaultDec = element.attributeValue("defaultOf");
			if (defaultDec != null)
				setAsDefaultNode(logger, node, defaultDec);

			component = node;
		}

		return component;
	}
	private static void setAsDefaultNode(Logger logger, DB_Node node, String defaultDec) {

		// ensure parent is an object
		Component parent = node.getParent();
		if (!(parent instanceof DB_Object)) {
			logger.log("Child node did not have a parent of DB_Object, unable to set behaviour", LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
		}

		DB_Object objParent = (DB_Object) parent;

		switch (defaultDec) {
			case "INTEGER":
				objParent.setDefaultNode(NodeType.INTEGER, node);
				break;

			case "BINARY":
				objParent.setDefaultNode(NodeType.BINARY, node);
				break;

			case "STRING":
				objParent.setDefaultNode(NodeType.STRING, node);
				break;

			case "DEFAULT":
				objParent.setDefaultNode(NodeType.DEFAULT, node);
				break;

			default:
				logger.log("That default node setting '" + defaultDec + "' is invalid", LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
		}
	}
}
