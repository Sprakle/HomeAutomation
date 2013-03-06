package objectDatabase.componentTree.nodeBehaviour.behaviours;

import java.util.HashMap;

import objectDatabase.NodeType;
import objectDatabase.componentTree.components.DB_Node;
import objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import utilities.logger.Logger;

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
