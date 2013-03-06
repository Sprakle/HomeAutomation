package objectDatabase.componentTree.components;

import java.util.HashMap;

import objectDatabase.ComponentType;
import objectDatabase.componentTree.Component;
import utilities.logger.Logger;

public class DB_Placeholder extends Component {

	public DB_Placeholder(Logger logger, Component parent, int depth, String identifier, HashMap<String, String> args, String originalDefinition) {
		super(logger, parent, depth, identifier, args, originalDefinition);

		this.componentType = ComponentType.DB_PLACEHOLDER;
	}

}
