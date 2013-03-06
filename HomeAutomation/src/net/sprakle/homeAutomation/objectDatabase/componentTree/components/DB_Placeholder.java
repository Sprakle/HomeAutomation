package net.sprakle.homeAutomation.objectDatabase.componentTree.components;

import java.util.HashMap;

import net.sprakle.homeAutomation.objectDatabase.ComponentType;
import net.sprakle.homeAutomation.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class DB_Placeholder extends Component {

	public DB_Placeholder(Logger logger, Component parent, int depth, String identifier, HashMap<String, String> args, String originalDefinition) {
		super(logger, parent, depth, identifier, args, originalDefinition);

		this.componentType = ComponentType.DB_PLACEHOLDER;
	}

}
