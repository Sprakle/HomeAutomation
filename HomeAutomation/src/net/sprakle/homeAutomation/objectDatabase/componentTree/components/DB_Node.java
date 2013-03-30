package net.sprakle.homeAutomation.objectDatabase.componentTree.components;

import net.sprakle.homeAutomation.objectDatabase.ComponentType;
import net.sprakle.homeAutomation.objectDatabase.NodeType;
import net.sprakle.homeAutomation.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class DB_Node extends Component {

	NodeBehaviour behaviour;

	public DB_Node(Logger logger, Component parent, String identifier) {
		super(logger, parent, identifier);

		this.componentType = ComponentType.DB_NODE;
	}

	public void setBehaviour(NodeBehaviour behaviour) {

		// make sure we don't have one already
		if (this.behaviour == null) {

			// set the new one
			this.behaviour = behaviour;
			logger.log("Added behaviour '" + behaviour.getClass().getSimpleName() + "' to Node '" + this.getIdentifier() + "' Arguments:", LogSource.OD_COMPONENT_INFO, 3);

			for (String s : behaviour.getArgs().keySet()) {
				String flag = s;
				String value = behaviour.getArgs().get(s);

				logger.log("    -" + flag + " " + value, LogSource.OD_COMPONENT_INFO, 3);
			}

		} else {

			// complain!
			logger.log("behaviour already set! Node: " + this.getIdentifier(), LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
		}
	}

	public NodeBehaviour getBehaviour() {
		return behaviour;
	}

	// a node can only have one of these work
	public <T> T readValue() {
		if (this.behaviour != null) {
			return behaviour.readValue();
		} else {
			logger.log("No behaviour set! Node: " + this.getIdentifier(), LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
		}
		return null;
	}

	// a node can only have one of these work
	public <T> void writeValue(NodeType type, T value) {
		if (this.behaviour != null) {
			behaviour.writeValue(type, value);
		} else {
			logger.log("No behaviour set! Node: " + this.getIdentifier(), LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
		}
	}
}
