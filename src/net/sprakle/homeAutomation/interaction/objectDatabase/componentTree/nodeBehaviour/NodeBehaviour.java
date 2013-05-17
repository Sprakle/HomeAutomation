package net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.nodeBehaviour;

import java.util.ArrayList;
import java.util.HashMap;

import net.sprakle.homeAutomation.interaction.objectDatabase.NodeType;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

@SuppressWarnings({"SameReturnValue", "UnusedParameters"})
public abstract class NodeBehaviour {
	private final Logger logger;

    // these must be set by the extending class
    private final ArrayList<NodeType> acceptedReadTypes;
	private final ArrayList<NodeType> acceptedWriteTypes;

	private final DB_Node parent;

	protected final HashMap<String, String> args;

	// only the factory should be able to initiate this
	protected NodeBehaviour(Logger logger, DB_Node parent, HashMap<String, String> args) {
		this.logger = logger;
		this.parent = parent;
		this.args = args;

		acceptedReadTypes = getAcceptedNodeReadTypes();
		acceptedWriteTypes = getAcceptedNodeWriteTypes();
	}

	// extending classes must give the type they accept
	protected abstract ArrayList<NodeType> getAcceptedNodeReadTypes();
	protected abstract ArrayList<NodeType> getAcceptedNodeWriteTypes();

	@SuppressWarnings("unchecked")
	public final <T> T readValue(NodeType readType) {
		// ensure this behaviour supports the requested type
		if (!acceptedReadTypes.contains(readType)) {
			logger.log("This node bahaviour cannot give the requested type of data", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
			return null;
		}

		switch (readType) {
			case STRING:
				return (T) readString();

			case INTEGER:
				return (T) readInteger();

			case BINARY:
				return (T) readBinary();

			default:
				// not applicable
				break;
		}

		return null;
	}

	public final <T> void writeValue(NodeType writeType, T value) {

		// ensure this behaviour supports the given type
		if (!acceptedWriteTypes.contains(writeType)) {
			String error = "Node Behaviour '" + getClass().getSimpleName() + " does not accept the given generic type";
			logger.log(error, LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		}

		try {
			switch (writeType) {
				case STRING:
					writeString((String) value);
					logger.log("Nodebehaviour '" + getClass().getSimpleName() + "' recieved String write request", LogSource.OD_NODE_BEHAVIOUR, 3);
					break;

				case INTEGER:
					writeInteger((Integer) value);
					logger.log("Nodebehaviour '" + getClass().getSimpleName() + "' recieved Integer write request", LogSource.OD_NODE_BEHAVIOUR, 3);
					break;

				case BINARY:
					writeBinary((Boolean) value);
					logger.log("Nodebehaviour '" + getClass().getSimpleName() + "' recieved Binary write request", LogSource.OD_NODE_BEHAVIOUR, 3);
					break;
				default:
					// not appicable
					break;
			}
		} catch (ClassCastException e) {
			logger.log("Given nodetype doe not match the given value", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
			e.printStackTrace();
		}
	}

	// one of these should be overridden by the concrete behaviour
	protected String readString() {
		logger.log("readString in nodeBehavoir " + parent.getIdentifier() + " not overriden", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		return null;
	}
	protected Boolean readBinary() {
		logger.log("readBinary in nodeBehavoir " + parent.getIdentifier() + " not overriden", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		return null;
	}
	protected Integer readInteger() {
		logger.log("readInteger in nodeBehavoir " + parent.getIdentifier() + " not overriden", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		return null;
	}

	// one of these should be overridden by the concrete behaviour
    protected void writeString(String write) {
		logger.log("writeInteger in nodeBehavoir " + parent.getIdentifier() + " not overriden", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
	}
	protected void writeBinary(Boolean write) {
		logger.log("writeInteger in nodeBehavoir " + parent.getIdentifier() + " not overriden", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
	}
	protected void writeInteger(Integer write) {
		logger.log("writeInteger in nodeBehavoir " + parent.getIdentifier() + " not overriden", LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
	}

	public HashMap<String, String> getArgs() {
		return args;
	}

	public DB_Node getParent() {
		return parent;
	}

	public enum NodeBehaviourType {
		ARDUINO_DEVICE,
		WEATHER,
	}
}
