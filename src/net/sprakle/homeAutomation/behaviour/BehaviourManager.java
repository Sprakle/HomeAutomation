package net.sprakle.homeAutomation.behaviour;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Each behaviour has one or more triggers, and one or more actions. Behaviours
 * are defined in config/behaviours.xml.
 * 
 * @author ben
 * 
 */
public class BehaviourManager implements LogicTimerObserver {

	private final Logger logger;
	private final ObjectDatabase od;
	private final ExternalSoftware exs;

	private final long UPDATE_PERIOD;
	private final String USER_BEHAVIOURS_PATH;
	private final String SYSTEM_BEHAVIOURS_PATH;

	private long lastUpdate;

	// behaviour, time in ms of last update
	private final HashMap<Behaviour, Long> behaviours;

	public BehaviourManager(Logger logger, ObjectDatabase od, ExternalSoftware exs) {
		this.logger = logger;
		this.od = od;
		this.exs = exs;

		UPDATE_PERIOD = Config.getInt("config/behaviours/minimum_update_period");
		USER_BEHAVIOURS_PATH = Config.getString("config/behaviours/user_behaviours_file");
		SYSTEM_BEHAVIOURS_PATH = Config.getString("config/behaviours/system_behaviours_file");

		LogicTimer timer = LogicTimer.getLogicTimer();
		timer.addObserver(this);

		lastUpdate = System.currentTimeMillis();

		behaviours = new HashMap<>();
		behaviours.putAll(readBehaviours(USER_BEHAVIOURS_PATH));
		behaviours.putAll(readBehaviours(SYSTEM_BEHAVIOURS_PATH));

		logger.log("Loaded " + behaviours.size() + " behaviours", LogSource.BEHAVIOUR, 2);
	}

	private Map<Behaviour, Long> readBehaviours(String path) {
		Map<Behaviour, Long> behaviours = new HashMap<>();

		Element root = readXML(path).getRootElement();

		// create and add each behaviour in the behaviours XML file
		for (Iterator<?> i = root.elementIterator(XMLKeys.BEHAVIOUR); i.hasNext();) {
			Element behaviourElement = (Element) i.next();
			Behaviour behaviour = createBehaviour(behaviourElement, true);
			behaviours.put(behaviour, System.currentTimeMillis());

			behaviour.setFile(USER_BEHAVIOURS_PATH);
		}

		return behaviours;
	}

	@Override
	public void advanceLogic() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastUpdate >= UPDATE_PERIOD) {
			update();
			lastUpdate = System.currentTimeMillis();
		}
	}

	/*
	 * Behaviours are updated if their state has changed from false to true
	 * This way, behaviours are only triggered once
	 */
	private void update() {
		for (Entry<Behaviour, Long> e : behaviours.entrySet()) {
			Behaviour b = e.getKey();
			BehaviourState state = b.getState();

			long timeSinceLastUpdate = System.currentTimeMillis() - e.getValue();
			if (timeSinceLastUpdate < b.getUpdatePeriod())
				// don't update
				continue;

			switch (state) {
				case DORMANT:
					if (b.check()) {
						b.executeTriggerStart();
						logger.log("Behaviour start triggered: " + b.getName(), LogSource.BEHAVIOUR, 3);
						state = BehaviourState.TRIGGERED;
					}
					break;

				case TRIGGERED:
					state = BehaviourState.ACTIVE;
					break;

				case ACTIVE:
					if (!b.check()) {
						state = BehaviourState.DORMANT;
						b.executeTriggerEnd();
						logger.log("Behaviour end triggered: " + b.getName(), LogSource.BEHAVIOUR, 3);
					}
					break;
			}

			// update state if it has been changed
			b.setState(state);

			// remove if wanted
			if (b.shouldDelete()) {
				behaviours.remove(b);

				// remove from file as well if necessary
				if (b.isPersistent()) {
					String file = b.getFile();
					Element element = b.getElement();
					removeFileElement(file, element);
				}
			}

			// update time of last update
			behaviours.put(b, System.currentTimeMillis());
		}
	}
	private Document readXML(String path) {
		File file = new File(path);

		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(file);
		} catch (DocumentException e) {
			logger.log("Unable to read behaviours XML file: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}
		return document;
	}

	public void addBehaviour(Behaviour behaviour) {
		behaviours.put(behaviour, System.currentTimeMillis());
		logger.log("Added behaviour: '" + behaviour.getName() + "'", LogSource.BEHAVIOUR, 3);

		if (behaviour.isPersistent())
			return;

		Document systemDoc = readXML(SYSTEM_BEHAVIOURS_PATH);

		// copy the original system file, make a new copy of it to overwrite the old one
		Element root = systemDoc.getRootElement().createCopy();

		Element behaviourElement = behaviour.getElement();
		root.add(behaviourElement.detach());

		// create a new file based on the original, with the new element added
		Document newDoc = DocumentFactory.getInstance().createDocument();
		newDoc.setRootElement(root);

		try {
			FileOutputStream fos = new FileOutputStream(SYSTEM_BEHAVIOURS_PATH);

			XMLWriter writer = new XMLWriter(fos, OutputFormat.createPrettyPrint());

			writer.write(newDoc);
			writer.close();
		} catch (IOException e) {
			logger.log("Unable to write to system defined behaviours file", LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}

		behaviour.setFile(SYSTEM_BEHAVIOURS_PATH);
		logger.log("Behaviour saved to system behaviours file", LogSource.BEHAVIOUR, 3);
	}
	/**
	 * Used to create a behaviour based on an element in an XML file
	 * 
	 * @param element
	 * @return
	 */
    Behaviour createBehaviour(Element element, boolean persistent) {
		Behaviour behaviour = new Behaviour(logger, element, od, exs, persistent);
		return behaviour;
	}

	/**
	 * Used to create a behaviour based on a definition instead of a file
	 * 
	 * @param def
	 * @return
	 */
	public Behaviour createBehaviour(BehaviourDefinition def, boolean persistent) {

		// create an element based on the definition
		Document doc = DocumentFactory.getInstance().createDocument();
		Element root = doc.addElement(XMLKeys.ROOT);
		doc.setRootElement(root);
		Element behaviourElement = root.addElement(XMLKeys.BEHAVIOUR);

		def.copyDataToElement(behaviourElement);

		Behaviour behaviour = createBehaviour(behaviourElement, persistent);

		return behaviour;
	}

	/**
	 * Removes an element from a given XML file
	 * 
	 * @param filePath
	 * @param element
	 */
	private void removeFileElement(String filePath, Element element) {

		// get
		Document doc = readXML(filePath);

		// remove
		doc.remove(element);

		// save
		try {
			FileOutputStream fos = new FileOutputStream(filePath);

			XMLWriter writer = new XMLWriter(fos, OutputFormat.createPrettyPrint());

			writer.write(doc);
			writer.close();
		} catch (IOException e) {
			String path = element.getUniquePath();
			logger.log("Unable to remove element '" + path + "' from XML: '" + filePath + "'", LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}
	}
}
