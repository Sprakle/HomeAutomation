package net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour;

import java.util.HashMap;

import net.sprakle.homeAutomation.objectDatabase.NodeType;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public abstract class NodeBehaviour {
	protected Logger logger;

	// FIXME: next: allow turning on and off analog nodes

	public static final NodeBehaviourType ARDUINO_DEVICE = NodeBehaviourType.ARDUINO_DEVICE;
	public static final NodeBehaviourType WEATHER = NodeBehaviourType.WEATHER;

	// these must be set by the extending class
	protected NodeType nodeType;

	protected DB_Node parent;

	protected HashMap<String, String> args;

	// only the factory should be able to initiate this
	protected NodeBehaviour(Logger logger, DB_Node parent, HashMap<String, String> args) {
		this.logger = logger;
		this.parent = parent;
		this.args = args;

		nodeType = getNodeType();
	}

	// extending classes must give the type they accept
	protected abstract NodeType getNodeType();

	@SuppressWarnings("unchecked")
	public final <T> T readValue() {
		switch (nodeType) {
			case STRING:
				return (T) readString();

			case INTEGER:
				return (T) readInteger();

			case BINARY:
				return (T) readBinary();
		}

		return null;
	}

	public final <T> void writeValue(NodeType type, T value) {

		if (type != this.nodeType) {
			String error = "Node Behaviour '" + getClass().getSimpleName() + " does not accept the given generic type";
			logger.log(error, LogSource.ERROR, LogSource.OD_NODE_BEHAVIOUR, 1);
		}

		try {
			switch (this.nodeType) {
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
