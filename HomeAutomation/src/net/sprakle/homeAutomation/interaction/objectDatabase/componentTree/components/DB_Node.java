package net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components;

import net.sprakle.homeAutomation.interaction.objectDatabase.ComponentType;
import net.sprakle.homeAutomation.interaction.objectDatabase.NodeType;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour.NodeBehaviour;
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

		} else {

			// complain!
			logger.log("behaviour already set! Node: " + this.getIdentifier(), LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
		}
	}

	public NodeBehaviour getBehaviour() {
		return behaviour;
	}

	// a node can only have one of these work
	public <T> T readValue(NodeType type) {
		if (this.behaviour != null) {
			return behaviour.readValue(type);
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
