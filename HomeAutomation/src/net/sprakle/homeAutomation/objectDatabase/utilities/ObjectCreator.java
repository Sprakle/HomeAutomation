package net.sprakle.homeAutomation.objectDatabase.utilities;

import net.sprakle.homeAutomation.interaction.arduino.Arduino;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Placeholder;
import net.sprakle.homeAutomation.objectDatabase.componentTree.nodeBehaviour.NodeBehaviourFactory;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class ObjectCreator {

	private Logger logger;
	private NodeBehaviourFactory nbf;

	Document doc;

	// create root component
	private Component root;

	// current component
	private Component previousComponent;

	// depth of current and previous components
	private int currentDepth = 1;
	private int previousDepth = 0;

	public ObjectCreator(Logger logger, Arduino arduino) {
		this.logger = logger;

		nbf = new NodeBehaviourFactory(arduino);

		reloadDatabase();
	}

	public void reloadDatabase() {
		SAXReader reader = new SAXReader();
		try {
			doc = reader.read(Config.getString("config/files/object_database"));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	public Component createObjectTree() {
		root = new DB_Placeholder(logger, null, "root");

		previousComponent = root;

		treeWalk(doc.getRootElement());

		return root;
	}

	// iterates through each element in the XML file, creating a component based on each element
	private void treeWalk(Element element) {
		for (int i = 0, size = element.nodeCount(); i < size; i++) {
			Node node = element.node(i);
			if (node instanceof Element) {
				Element e = (Element) node;
				// only create object of its and OBJECT or NODE
				if (e.getName().equals("OBJECT") || e.getName().equals("NODE")) {
					createObject((Element) node);
					treeWalk((Element) node);
				}
			}
		}
	}

	private void createObject(Element element) {

		// get current depth
		currentDepth = getElementDepth(element);

		// change in depth. 1:increase, 0:same, -1:decrease
		int depthChange = currentDepth - previousDepth;

		// name of component on THIS line
		String currentName = element.attributeValue("name");

		// create a new component and stick it somewhere
		if (depthChange == 1) {
			//Component component = new DB_Placeholder(logger, previousComponent, currentName, null, null);
			Component component = ComponentFactory.getComponent(logger, nbf, previousComponent, currentName, element);
			// add it to the most recently added component
			previousComponent.addChild(component);

			previousComponent = component;

		} else if (depthChange == 0) {
			// find the parent (by getting the parent of the previous
			// component
			Component parent = previousComponent.getParent();

			Component component = ComponentFactory.getComponent(logger, nbf, parent, currentName, element);

			// add it next to the most recently added component
			previousComponent.getParent().addChild(component);
			previousComponent = component;

		} else if (depthChange < 0) {
			// find the parent (by the number of levels reversed), and add
			// the component
			Component parent = previousComponent.getParent(Math.abs(depthChange));

			Component component = ComponentFactory.getComponent(logger, nbf, parent, currentName, element);

			parent.addChild(component);
			previousComponent = component;

		} else {
			logger.log("Object dadabase file formatted incorectly! Tab length out of range!", LogSource.ERROR, LogSource.OD_OBJECT_CREATION_INFO, 1);
			System.exit(1);
		}

		// current depth (it will later be the previous depth)
		previousDepth = currentDepth;
	}

	int getElementDepth(Element e) {
		String path = e.getPath();
		int count = 0;
		for (int i = 0; i < path.length(); i++)
		{
			if (path.charAt(i) == '/')
			{
				count++;
			}
		}

		return count - 1;
	}
}