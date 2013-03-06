package net.sprakle.homeAutomation.objectDatabase;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sprakle.homeAutomation.interaction.arduino.Arduino;
import net.sprakle.homeAutomation.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Object;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Placeholder;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviourFactory;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour.NodeBehaviourType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class ObjectDatabaseUtilities {

	// db file argument maps
	private static final String DEFAULT_NODE_BINARY = "def_node_bin";
	private static final String DEFAULT_NODE_INTEGER = "def_node_int";
	private static final String DEFAULT_NODE_STRING = "def_node_str";

	// will an Object Database will named placeholder objects arranged in a tree structure
	public static ArrayList<Component> createPlaceholderObjects(Logger logger, List<String> lines) {

		ArrayList<Component> tree = new ArrayList<Component>();

		// create root component
		Component root = new DB_Placeholder(logger, null, 0, "root", null, "{Root} \"rooms\"");
		tree.add(root);

		// current component
		Component previousComponent = root;

		// depth of current and previous components
		int currentDepth = 1;
		int previousDepth = 0;

		// perform on each line
		for (String s : lines) {

			// get current depth
			currentDepth = getDepth(logger, s);

			// change in depth. 1:increase, 0:same, -1:decrease
			int depthChange = currentDepth - previousDepth;

			// name of component on THIS line
			String currentName = parseForComponentName(logger, s);

			// component arguments
			HashMap<String, String> args = parseForArgs(logger, s);

			// create a new component and stick it somewhere
			if (depthChange == 1) {
				Component component = new DB_Placeholder(logger, previousComponent, currentDepth, currentName, args, s);

				// add it to the most recently added component
				previousComponent.addChild(component);

				previousComponent = component;

			} else if (depthChange == 0) {
				// find the parent (by getting the parent of the previous
				// component
				Component parent = previousComponent.getParent();

				Component component = new DB_Placeholder(logger, parent, currentDepth, currentName, args, s);

				// add it next to the most recently added component
				previousComponent.getParent().addChild(component);
				previousComponent = component;

			} else if (depthChange < 0) {
				// find the parent (by the number of levels reversed), and add
				// the component
				Component parent = previousComponent.getParent(Math.abs(depthChange));

				Component component = new DB_Placeholder(logger, parent, currentDepth, currentName, args, s);

				parent.addChild(component);
				previousComponent = component;

			} else {
				logger.log("Object dadabase file formatted incorectly! Tab length out of range!", LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
				break;
			}

			// current depth (it will later be the previous depth)
			previousDepth = currentDepth;
		}
		return tree;
	}

	/*
	 * How the configuration works:
	 * 1) A reference to object from the given database is but in the index
	 * 2) Each reference in the index is looped through:
	 * 		1) The reference is checked to see which type it is
	 * 		2) A new object with the correct type (instead of placeholder) is created and given to the parent of the placeholder
	 * 		3) The new Component is given the children the placeholder had
	 * 		4) The children have their parent updated
	 * 		5) The old placeholder is removed from the parent
	 */
	public static ArrayList<Component> configureDatabase(Logger logger, Arduino arduino, ArrayList<Component> database) {
		// get all components recursively from root component
		ArrayList<Component> index = database.get(0).getChildrenRecursive();

		for (Component c : index) {

			// find out what type we have
			ComponentType type = parseForComponentType(logger, c.getOriginalDefinition());
			switch (type) {
				case DB_ROOT:
					// leave placeholder, as root is not a real component
					break;

				case DB_OBJECT:
					// create new Component, add to parent
					DB_Object cObject = new DB_Object(logger, c.getParent(), c.getDepth(), c.getIdentifier(), c.getArgs(), c.getOriginalDefinition());
					cObject.getParent().addChild(cObject);
					// add placeholder's children and update their parent
					for (Component child : c.getChildren()) {
						cObject.addChild(child);
						child.changeParent(cObject);
					}

					// delete placeholder. If we remove the reference from the parent, it will effectively be removed from the OD
					c.getParent().removeChild(c);
					break;

				case DB_NODE:
					// create new Component, add to parent
					DB_Node cNode = new DB_Node(logger, c.getParent(), c.getDepth(), c.getIdentifier(), c.getArgs(), c.getOriginalDefinition());
					cNode.getParent().addChild(cNode);
					// add placeholder's children and update their parent
					for (Component child : c.getChildren()) {
						cNode.addChild(child);
						child.changeParent(cNode);
					}

					// the parent object may have specified default nodes. check to see if so, and then if this node is it
					DB_Object parent = (DB_Object) cNode.getParent();
					applyDefaultNode(parent, cNode);

					// delete placeholder. If we remove the reference from the parent, it will effectively be removed from the OD
					c.getParent().removeChild(c);
					break;

				case DB_NODE_BEHAVIOUR:
					// we can assume this is a nodeBehaviour, and thus it's parent is a node
					DB_Node node = null;
					try {
						node = (DB_Node) c.getParent();
					} catch (ClassCastException e) {
						logger.log("The NodeBehaviour '" + c.getIdentifier() + "' did not have a node as a parent in the database organization file. instead, it had '" + c.getParent().getIdentifier() + "'", LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
					}

					// instead of adding the nodeBehaviour as a child to the node, we will add it directly to the node as a behaviour 
					NodeBehaviour behaviour = NodeBehaviourFactory.createBehaviour(logger, arduino, node, c.getArgs(), c.getOriginalDefinition());
					node.setBehaviour(behaviour);

					// delete placeholder. If we remove the reference from the parent, it will effectively be removed from the OD
					c.getParent().removeChild(c);
					break;

				default:
					logger.log("Bad logic in database configuration method. Get a programmer's attention!", LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
			}
		}

		return database;
	}

	// Checks for default nodes and applies them.
	// the the node is not intended to be the default node, nothing will happen
	private static void applyDefaultNode(DB_Object parent, DB_Node node) {

		String defaultNodeBin = parent.getArgs().get(DEFAULT_NODE_BINARY);
		String defaultNodeInt = parent.getArgs().get(DEFAULT_NODE_INTEGER);
		String defaultNodeStr = parent.getArgs().get(DEFAULT_NODE_STRING);

		// perform checking process for Binary, Integer and String

		if (defaultNodeBin != null) {
			// check if the given node matches the wanted node
			if (defaultNodeBin.equals(node.getIdentifier())) {
				parent.setDefaultNode(NodeType.BINARY, node);
			}
		}

		if (defaultNodeInt != null) {
			// check if the given node matches the wanted node
			if (defaultNodeInt.equals(node.getIdentifier())) {
				parent.setDefaultNode(NodeType.INTEGER, node);
			}
		}

		if (defaultNodeStr != null) {
			// check if the given node matches the wanted node
			if (defaultNodeStr.equals(node.getIdentifier())) {
				parent.setDefaultNode(NodeType.STRING, node);
			}
		}
	}

	// PARSE FOR TPE FROM LINE OF DB ORGINIZATION FILE
	public static ComponentType parseForComponentType(Logger logger, String line) {
		ComponentType result = null;

		// isolate identifier ( +1 to get identifier without '{' )
		int CTbegin = line.indexOf('{') + 1;
		int CTend = line.indexOf('}');

		// make sure the formatting is correct
		if (CTbegin == -1 || CTend == -1) {
			logger.log("Error parsing containing curly brackets for type definition on line: " + line, LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
		}

		String readType = line.substring(CTbegin, CTend);

		switch (readType) {
			case "Root":
				result = ComponentType.DB_ROOT;
				break;

			case "Object":
				result = ComponentType.DB_OBJECT;
				break;

			case "Node":
				result = ComponentType.DB_NODE;
				break;

			case "NodeBehaviour":
				result = ComponentType.DB_NODE_BEHAVIOUR;
				break;

			default:
				logger.log("Invalid component type on line: '" + line.trim() + "'", LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
		}

		return result;
	}

	// PARSE FOR BEHAVIOUR TYPE OF DB ORGINIZATION FILE
	public static NodeBehaviourType parseForBehaviourType(Logger logger, String line) {
		NodeBehaviourType type = null;

		// isolate type ( +1 to get identifier without '"' )
		int begin = line.indexOf('"') + 1;
		int end = line.indexOf('"', begin);

		// make sure the formatting is correct
		if (begin == -1 || end == -1) {
			logger.log("Error parsing containing quotes for node behaviour type definition on line: " + line, LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
		}

		String readType = line.substring(begin, end);

		// convert to BehaviourType
		for (NodeBehaviourType t : NodeBehaviourType.values()) {
			if (t.name().equals(readType)) {
				type = t;
			}
		}

		if (type == null) {
			logger.log("Could not find BehaviourType matching '" + readType + "'", LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
		}

		return type;
	}

	// Arguments are defined by a flag and a value. EX: -flag value
	public static HashMap<String, String> parseForArgs(Logger logger, String line) {
		HashMap<String, String> args = new HashMap<String, String>();

		/*
		 * first get the line of arguments that is separated by commas
		 */
		// arguments come after the name is defined, so get the index of it
		int nameBegin = line.indexOf('"') + 1;
		int nameEnd = line.indexOf('"', nameBegin);

		// make sure the formatting is correct
		if (nameBegin == -1 || nameEnd == -1) {
			logger.log("Database file formatted incorectly!", LogSource.ERROR, LogSource.OD_BASE_INFO, 1);
			return null;
		}

		// separate arguments and flags
		String argsAndFlagsString = line.substring(nameEnd + 1, line.length());
		String[] argsAndFlags = argsAndFlagsString.split(" ");
		for (int i = 0; i < argsAndFlags.length; i++) {
			// if it's a flag
			if (argsAndFlags[i].startsWith("-")) {
				// get the flag's value
				String value = argsAndFlags[i + 1];

				// remove the tack from the flag
				String flag = argsAndFlags[i].substring(1, argsAndFlags[i].length());

				// make the new argument
				args.put(flag, value);
			}
		}

		return args;
	}

	// PARSE FOR NAME FROM LINE OF DB ORGINIZATION FILE
	public static String parseForComponentName(Logger logger, String line) {
		String name = null;
		// isolate name ( +1 to get name without '"' )
		int CNbegin = line.indexOf('"') + 1;
		int CNend = line.indexOf('"', CNbegin); // since chars are similar, we must start from the previous

		// make sure the formatting is correct
		if (CNbegin == -1 || CNend == -1) {
			logger.log("Error parsing containing quotes for name definition on line: " + line, LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
		}

		name = line.substring(CNbegin, CNend);
		return name;
	}

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

			// add arguments if any
			String arguments = "";
			if (c.getArgs() != null) {
				for (String s : c.getArgs().keySet()) {
					arguments += " -" + s + " " + c.getArgs().get(s);
				}
			}

			list.add(c.getDepth() + " " + indent + "{" + c.getClass().getSimpleName() + "} " + c.getIdentifier() + arguments);

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
