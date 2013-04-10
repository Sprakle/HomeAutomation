package net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.behaviours;

import java.util.ArrayList;
import java.util.HashMap;

import net.sprakle.homeAutomation.interaction.objectDatabase.NodeType;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Weather extends NodeBehaviour {

	public Weather(Logger logger, DB_Node parent, HashMap<String, String> args) {
		super(logger, parent, args);
	}

	@Override
	protected ArrayList<NodeType> getAcceptedNodeReadTypes() {
		return null;
	}

	@Override
	protected ArrayList<NodeType> getAcceptedNodeWriteTypes() {
		return null;
	}
}
