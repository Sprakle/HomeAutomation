/*
 * The Object Database is filled with these constructed in a tree structure.
 * DB_Placeholder: used only in the database creation. the database is filled with placeholders, and then each placeholder is replaced with the correct component
 * DB_Object: a physical or virtual object. EX: bedroom, stereo, weather, time
 * DB_Node: a node of a DB_Object
 * DB_NodeBehaviour: defines what a node should do when it's value is changed
 */

package net.sprakle.homeAutomation.interaction.objectDatabase.componentTree;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interaction.objectDatabase.ComponentType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public abstract class Component {
	protected Logger logger;

	protected ComponentType componentType;

	// name of component on OD
	protected String identifier;

	protected ArrayList<Component> children;
	protected Component parent;

	public Component(Logger logger, Component parent, String identifier) {
		this.logger = logger;
		this.parent = parent;
		this.identifier = identifier;

		children = new ArrayList<Component>();
	}

	public void addChild(Component child) {
		children.add(child);
	}

	public void removeChild(Component child) {
		children.remove(child);
	}

	// used when switching from placeholders to real components.
	public void changeParent(Component newParent) {
		if (this.parent.getComponentType() == ComponentType.DB_PLACEHOLDER) {
			this.parent = newParent;
		} else {
			String error = "Someone is trying to set parent '" + newParent.getIdentifier() + "' on child '" + this.getIdentifier() + "' but it already has the parent '" + parent.getIdentifier() + "'";
			logger.log(error, LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
		}
	}

	public Component getParent() {
		return parent;
	}

	public ComponentType getComponentType() {
		if (componentType == null) {
			logger.log("No component type set for component '" + this.getIdentifier() + "'. This is a problem in the programminh", LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
		}

		return componentType;
	}

	// used to find a specific child by name
	public Component getChild(Logger logger, String name) {
		Component found = null;
		logger.log("Searching child '" + identifier + "' for object '" + name + "'", LogSource.OD_COMPONENT_INFO, 3);

		for (Component c : children) {
			if (c.getIdentifier().equals(name)) {
				logger.log("Found matching child: '" + c.getIdentifier() + "'", LogSource.OD_COMPONENT_INFO, 3);
				found = c;
			}
		}

		// did we find a child?
		if (found == null) {
			logger.log("No matching node found", LogSource.OD_COMPONENT_INFO, 3);
		}

		return found;
	}

	// can be used to get parents, grandparents, and so on. 0 is parent
	public Component getParent(int levels) {
		Component parent = getParent();

		// for every level, get the parent of the previous
		for (int i = 0; i < levels; i++) {
			parent = parent.getParent();
		}

		return parent;
	}

	// similar to getParent, but 0 is self
	public Component traverseUp(int levels) {
		Component compoent = this;

		// for every level, get the parent of the previous
		for (int i = 0; i < levels; i++) {
			compoent = compoent.getParent();
		}

		return compoent;
	}

	public ArrayList<Component> getChildrenRecursive() {
		ArrayList<Component> childList = new ArrayList<Component>();

		// add self to list
		childList.add(this);

		for (Component c : children) {
			// get the child to list children
			ArrayList<Component> recursiveChildren = c.getChildrenRecursive();

			// add each of the children's children (recursive) to the list
			for (Component b : recursiveChildren) {
				childList.add(b);
			}
		}

		return childList;
	}

	public ArrayList<Component> getChildren() {
		return children;
	}

	public String getIdentifier() {
		return identifier;
	}
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public int getDepth() {
		int count = 0;

		Component parent = this.getParent();
		while (parent != null) {
			count++;
			parent = parent.getParent();
		}

		return count;
	}

	// return pull path on object database
	public String getAbsolutePath() {
		String path = "";

		for (int i = getDepth(); i >= 0; i--) {
			path += "/" + traverseUp(i).getIdentifier();
		}

		return path;
	}
}