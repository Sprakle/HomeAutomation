package net.sprakle.homeAutomation.behaviour;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
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

	private List<Behaviour> behaviours;

	public BehaviourManager(Logger logger, ObjectDatabase od, ExternalSoftware exs) {
		this.logger = logger;

		LogicTimer timer = LogicTimer.getLogicTimer();
		timer.addObserver(this);

		updatePeriod = Config.getInt("config/behaviours/update_period");
		lastUpdate = System.currentTimeMillis();

		String filePath = Config.getString("config/behaviours/behaviours_file");
		File file = new File(filePath);
		Element root = readXML(file);

		behaviours = new ArrayList<Behaviour>();

		// create and add each behaviour in the behaviours XML file
		for (Iterator<?> i = root.elementIterator("behaviour"); i.hasNext();) {
			Element behaviourElement = (Element) i.next();
			Behaviour behaviour = new Behaviour(logger, behaviourElement, od, exs);
			behaviours.add(behaviour);
		}

		logger.log("Loaded " + behaviours.size() + " behaviours", LogSource.BEHAVIOUR, 2);
	}

	@Override
	public void advanceLogic() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastUpdate >= updatePeriod) {
			update();
		}
	}

	public void update() {
		for (Behaviour b : behaviours) {
			if (b.check()) {
				logger.log("Behaviour triggered: " + b.getName(), LogSource.BEHAVIOUR, 3);
				b.execute();
			}
		}

		lastUpdate = System.currentTimeMillis();
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
