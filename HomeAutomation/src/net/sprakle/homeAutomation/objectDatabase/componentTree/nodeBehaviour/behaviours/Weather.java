package net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.behaviours;

import java.util.HashMap;

import net.sprakle.homeAutomation.objectDatabase.NodeType;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class Weather extends NodeBehaviour {

	public Weather(Logger logger, DB_Node parent, HashMap<String, String> args) {
		super(logger, parent, args);
	}

	@Override
	protected NodeType getNodeType() {
		// find out what part of the weather system we are at (temp, condition, etc)
		return null;
	}
}
