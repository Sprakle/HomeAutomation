package net.sprakle.homeAutomation.behaviour.triggers.objectDatabaseRead;

import net.sprakle.homeAutomation.interaction.objectDatabase.NodeType;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components.DB_Node;

import org.dom4j.Element;

public enum Comparison {
	INTEGER_GREATER_THAN {
		@Override
		public String getElementString() {
			return "integer_greater_than";
		}

		@Override
		public boolean compare(DB_Node node, Element element) {
			int i = node.readValue(NodeType.INTEGER);
			int lesser = Integer.parseInt(element.elementText(getElementString()));
			return i > lesser;
		}
	},

	INTEGER_LESS_THAN {

		@Override
		public String getElementString() {
			return "integer_less_than";
		}

		@Override
		public boolean compare(DB_Node node, Element element) {
			int i = node.readValue(NodeType.INTEGER);
			int greater = Integer.parseInt(element.elementText(getElementString()));
			return i < greater;
		}
	},

	INTEGER_EQUALS {

		@Override
		public String getElementString() {
			return "integer_equals";
		}

		@Override
		public boolean compare(DB_Node node, Element element) {
			int i = node.readValue(NodeType.INTEGER);
			int equal = Integer.parseInt(element.elementText(getElementString()));
			return i == equal;
		}
	},

	BINARY_EQUALS {

		@Override
		public String getElementString() {
			return "binary_equals";
		}

		@Override
		public boolean compare(DB_Node node, Element element) {
			boolean b = node.readValue(NodeType.BINARY);
			boolean equal = Boolean.parseBoolean(element.elementText(getElementString()));
			return b == equal;
		}
	},

	STRING_EQUALS {

		@Override
		public String getElementString() {
			return "string_equals";
		}

		@Override
		public boolean compare(DB_Node node, Element element) {
			String s = node.readValue(NodeType.STRING);
			String equal = element.elementText(getElementString());
			return s.equals(equal);
		}
	};

	public abstract String getElementString();
	public abstract boolean compare(DB_Node node, Element element);
}
