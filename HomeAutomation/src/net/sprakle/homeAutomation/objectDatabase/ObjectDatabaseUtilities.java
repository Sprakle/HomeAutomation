package net.sprakle.homeAutomation.objectDatabase;

import java.util.ArrayList;

import net.sprakle.homeAutomation.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;

public class ObjectDatabaseUtilities {

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
		}

		return list;
	}
}
