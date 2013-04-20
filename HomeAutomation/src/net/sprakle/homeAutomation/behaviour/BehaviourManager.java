package net.sprakle.homeAutomation.behaviour;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Each behaviour has one or more triggers, and one or more actions. Behaviours
 * are defined in config/behaviours.xml.
 * 
 * @author ben
 * 
 */
public class BehaviourManager implements LogicTimerObserver {

	private Logger logger;

	private long updatePeriod;
	private long lastUpdate;

	// behaviour, time in ms of last update
	private HashMap<Behaviour, Long> behaviours;

	public BehaviourManager(Logger logger, ObjectDatabase od, ExternalSoftware exs) {
		this.logger = logger;

		LogicTimer timer = LogicTimer.getLogicTimer();
		timer.addObserver(this);

		updatePeriod = Config.getInt("config/behaviours/minimum_update_period");
		lastUpdate = System.currentTimeMillis();

		String filePath = Config.getString("config/behaviours/behaviours_file");
		File file = new File(filePath);
		Element root = readXML(file);

		behaviours = new HashMap<Behaviour, Long>();

		// create and add each behaviour in the behaviours XML file
		for (Iterator<?> i = root.elementIterator("behaviour"); i.hasNext();) {
			Element behaviourElement = (Element) i.next();
			Behaviour behaviour = new Behaviour(logger, behaviourElement, od, exs);
			behaviours.put(behaviour, System.currentTimeMillis());
		}

		logger.log("Loaded " + behaviours.size() + " behaviours", LogSource.BEHAVIOUR, 2);
	}

	@Override
	public void advanceLogic() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastUpdate >= updatePeriod) {
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

			// update time of last update
			behaviours.put(b, System.currentTimeMillis());
		}
	}
	private Element readXML(File file) {
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(file);
		} catch (DocumentException e) {
			logger.log("Unable to read behaviours XML file", LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}
		return document.getRootElement();
	}
}
