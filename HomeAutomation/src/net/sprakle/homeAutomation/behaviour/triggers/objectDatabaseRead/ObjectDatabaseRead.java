package net.sprakle.homeAutomation.behaviour.triggers.objectDatabaseRead;

import net.sprakle.homeAutomation.behaviour.triggers.Trigger;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase.QueryResponse;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class ObjectDatabaseRead implements Trigger {

	private DB_Node node;
	private Comparison comparison;

	private Element element;

	private final String NODE_KEY = "object_node";
	private final String COMPARISON_KEY = "comparison";

	public ObjectDatabaseRead(Logger logger, Element element, ObjectDatabase od) {
		this.element = element;

		String path = element.getUniquePath();

		String nodePath = element.elementText(NODE_KEY);
		String comparisonString = element.elementText(COMPARISON_KEY);

		if (nodePath == null || comparisonString == null)
			logger.log("Unable to read trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);

		node = getNode(logger, od, nodePath);
		comparison = getComparison(comparisonString);

		if (node == null || comparison == null)
			logger.log("Unable to read trigger: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
	}

	private DB_Node getNode(Logger logger, ObjectDatabase od, String nodePath) {

		// get and flip array - TODO: don't require flipped path when querying the database
		String nodePathArray[] = nodePath.split("/");
		for (int i = 0; i < nodePathArray.length / 2; i++)
		{
			String temp = nodePathArray[i];
			nodePathArray[i] = nodePathArray[nodePathArray.length - i - 1];
			nodePathArray[nodePathArray.length - i - 1] = temp;
		}
		QueryResponse qr = od.query(logger, nodePathArray);

		if (!qr.sucsess())
			return null;

		Component c = qr.component();
		if (!(c instanceof DB_Node))
			return null;

		return (DB_Node) c;
	}

	private Comparison getComparison(String comparisonString) {
		for (Comparison c : Comparison.values()) {
			if (c.getElementString().equals(comparisonString))
				return c;
		}

		return null;
	}

	@Override
	public boolean check() {
		return comparison.compare(node, element);
	}

}
