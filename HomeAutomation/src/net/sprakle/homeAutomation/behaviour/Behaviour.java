package net.sprakle.homeAutomation.behaviour;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sprakle.homeAutomation.behaviour.actions.Action;
import net.sprakle.homeAutomation.behaviour.actions.ActionFactory;
import net.sprakle.homeAutomation.behaviour.triggers.Trigger;
import net.sprakle.homeAutomation.behaviour.triggers.TriggerFactory;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public final class Behaviour {

	private Logger logger;

	private BehaviourState state;

	private List<Trigger> triggers;

	// actions to be called once triggered
	private List<Action> triggerStartActions;

	// actions to be called once triggers have ceased
	private List<Action> triggerEndActions;

	private final String NAME;
	private final String DESCRIPTION;
	private final int UPDATE_PERIOD;

	private final Element element;

	/**
	 * Create behaviour based on element from and XML file
	 * 
	 * @param logger
	 * @param behaviourElement
	 * @param od
	 * @param exs
	 */
	Behaviour(Logger logger, Element behaviourElement, ObjectDatabase od, ExternalSoftware exs) {
		this.logger = logger;
		this.element = behaviourElement;

		state = BehaviourState.DORMANT;

		NAME = behaviourElement.attributeValue(XMLKeys.NAME);
		DESCRIPTION = behaviourElement.elementText(XMLKeys.DESCRIPTION);

		String UPString = behaviourElement.elementText(XMLKeys.UPDATE_PERIOD);
		if (UPString != null && UPString.matches("\\d*"))
			UPDATE_PERIOD = Integer.parseInt(UPString);
		else
			UPDATE_PERIOD = Config.getInt("config/behaviours/minimum_update_period");

		if (NAME == null) {
			String path = behaviourElement.getUniquePath();
			logger.log("Behaviour does not have a name: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}

		if (DESCRIPTION == null) {
			String path = behaviourElement.getUniquePath();
			logger.log("Behaviour does not have a description: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}

		Element triggerElements = behaviourElement.element(XMLKeys.TRIGGERS);
		triggers = makeTriggers(triggerElements, od);

		Element triggerStartActionElements = behaviourElement.element(XMLKeys.TRIGGER_START_ACTIONS);
		Element triggerEndActionElements = behaviourElement.element(XMLKeys.TRIGGER_END_ACTION);
		triggerStartActions = makeActions(triggerStartActionElements, exs);
		triggerEndActions = makeActions(triggerEndActionElements, exs);
	}

	boolean check() {
		for (Trigger t : triggers)
			if (t.check())
				return true;

		return false;
	}
	void executeTriggerStart() {
		for (Action a : triggerStartActions)
			a.execute();
	}

	void executeTriggerEnd() {
		for (Action a : triggerEndActions)
			a.execute();
	}

	String getName() {
		return NAME;
	}

	String getDescription() {
		return DESCRIPTION;
	}

	Element getElement() {
		return element;
	}

	int getUpdatePeriod() {
		return UPDATE_PERIOD;
	}

	BehaviourState getState() {
		return state;
	}

	void setState(BehaviourState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return NAME;
	}

	private List<Trigger> makeTriggers(Element triggerElements, ObjectDatabase od) {
		List<Trigger> triggers = new ArrayList<Trigger>();

		for (Iterator<?> i = triggerElements.elementIterator(XMLKeys.TRIGGER); i.hasNext();) {
			Element triggerElement = (Element) i.next();

			Trigger trigger = TriggerFactory.makeTrigger(logger, triggerElement, od);

			if (trigger == null)
				logger.log("Unable to make trigger from behaviours file: " + triggerElement.getUniquePath(), LogSource.ERROR, LogSource.BEHAVIOUR, 1);

			triggers.add(trigger);
		}

		return triggers;
	}

	private List<Action> makeActions(Element actionElements, ExternalSoftware exs) {
		List<Action> actions = new ArrayList<Action>();

		if (actionElements == null || actionElements.nodeCount() == 0)
			return actions;

		for (Iterator<?> i = actionElements.elementIterator(XMLKeys.ACTION); i.hasNext();) {
			Element actionElement = (Element) i.next();

			Action action = ActionFactory.makeAction(logger, actionElement, exs);

			if (action == null)
				logger.log("Unable to make action from behaviours file: " + actionElement.getUniquePath(), LogSource.ERROR, LogSource.BEHAVIOUR, 1);

			actions.add(action);
		}

		return actions;
	}
}
