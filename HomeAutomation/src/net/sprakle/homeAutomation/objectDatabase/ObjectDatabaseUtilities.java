package net.sprakle.homeAutomation.objectDatabase;

import java.util.ArrayList;

import net.sprakle.homeAutomation.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ObjectDatabaseUtilities {

	// gets the depth of a component in the database organization file
	private static int getDepth(Logger logger, String line) {

		float depth = 0;

		float whitespace = line.indexOf('{'); // amount of whitespace before
												// start
		depth = whitespace / 4; // there are 4 spaces in each tab

		// if there was a tab that wasn'texactly 4 spaces, this will discover it
		if (Math.round(depth) != depth) {
			logger.log("Object dadabase file formatted incorectly! Incorrect tab length: " + line, LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
		}

		// add one, because root is already 0
		return Math.round(depth + 1);
	}

	// returns an unsorted list containing the identifiers and types of all objects in the tree
	public static ArrayList<String> listObjects(ArrayList<Component> database) {
		ArrayList<String> list = new ArrayList<String>();

		for (Component c : database.get(0).getChildrenRecursive()) {
			// indent by depth
			String indent = "";
			for (int i = 0; i < c.getDepth(); i++) {
				indent += "    ";
			}

			list.add(c.getDepth() + " " + indent + "{" + c.getClass().getSimpleName() + "} " + c.getIdentifier());

			// if it's a node and it has a behaviour, list it too
			if (c.getComponentType() == ComponentType.DB_NODE) {
				DB_Node node = (DB_Node) c;

				// add arguments if any
				String behaviourArgs = "";
				if (node.getBehaviour() != null && node.getBehaviour().getArgs() != null) {
					for (String s : node.getBehaviour().getArgs().keySet()) {
						behaviourArgs += " -" + s + " " + node.getBehaviour().getArgs().get(s);
					}
				}

				if (node.getBehaviour() != null) {
					list.add(c.getDepth() + " " + indent + "    BEHAVIOUR: " + node.getBehaviour().getClass().getSimpleName() + behaviourArgs);
				}
			}

			/*															// ALREADY ACOMPLISHED BY LISTING ARGUMENTS
			// if it's a DB_Object and has a default node, list it
			if (c.getComponentType() == ComponentType.DB_OBJECT) {
				DB_Object object = (DB_Object) c;

				// for each node type...
				for (NodeType nt : NodeType.values()) {
					if (object.getDefaultNode(nt) != null) {
						System.out.println(">>>> getting default node");
						list.add(c.getDepth() + " " + indent + "    DEFAULT NODE: " + object.getDefaultNode(nt).getIdentifier());
					}
				}
			}
			*/
		}

		return list;
	}
}
