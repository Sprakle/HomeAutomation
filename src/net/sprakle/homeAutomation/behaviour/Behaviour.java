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

	private final Logger logger;

	private final boolean persistent;
	private BehaviourState state;

	private final List<Trigger> triggers;

	// actions to be called once triggered
	private final List<Action> triggerStartActions;

	// actions to be called once triggers have ceased
	private final List<Action> triggerEndActions;

	private final String NAME;
	private final String DESCRIPTION;
	private final int UPDATE_PERIOD;

	// this behaviour will be deleted after n triggers. If == -1, the behaviour will stay forever
	private final int DELETE_AFTER_TRIGGERS;

	// number of times this behaviour has been triggered
	private int triggersTriggered;

	private final Element element;

	// must be set by manager if persistent;
	private String file;

	Behaviour(Logger logger, Element behaviourElement, ObjectDatabase od, ExternalSoftware exs, boolean persistent) {
		this.logger = logger;
		this.element = behaviourElement;
		this.persistent = persistent;

		state = BehaviourState.DORMANT;

		// mandatory elements
		NAME = behaviourElement.attributeValue(XMLKeys.NAME);
		DESCRIPTION = behaviourElement.elementText(XMLKeys.DESCRIPTION);

		// update period
		String UPString = behaviourElement.elementText(XMLKeys.UPDATE_PERIOD);

		// delete after triggers
		String datString = behaviourElement.elementText(XMLKeys.DELETE_AFTER_TRIGGERS);

		// confirm update period
		if (UPString != null && UPString.matches("\\d*"))
			UPDATE_PERIOD = Integer.parseInt(UPString);
		else
			UPDATE_PERIOD = Config.getInt("config/behaviours/minimum_update_period");

		// deleteAfterTriggers confirm. If it is not null, it must have an integer > 0
		if (datString != null) {
			if (datString.matches("\\d+")) {
				DELETE_AFTER_TRIGGERS = Integer.parseInt(datString);
			}else{
				DELETE_AFTER_TRIGGERS = -1;
				logger.log("Invalid delete_after_triggers_string: " + datString, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			}
		} else {
			DELETE_AFTER_TRIGGERS = -1;
		}

		// confirm name
		if (NAME == null) {
			String path = behaviourElement.getUniquePath();
			logger.log("Behaviour does not have a name: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}

		// confirm description
		if (DESCRIPTION == null) {
			String path = behaviourElement.getUniquePath();
			logger.log("Behaviour does not have a description: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}

		// make triggers
		Element triggerElements = behaviourElement.element(XMLKeys.TRIGGERS);
		triggers = makeTriggers(triggerElements, od);

		// make actions
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
		triggersTriggered++;

		for (Action a : triggerStartActions)
			a.execute();
	}

	void executeTriggerEnd() {
		triggersTriggered++;

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

	String getFile() {
		return file;
	}

	boolean isPersistent() {
		return persistent;
	}

	@Override
	public String toString() {
		return NAME;
	}

    boolean shouldRemove() {
		return DELETE_AFTER_TRIGGERS != -1 && triggersTriggered >= DELETE_AFTER_TRIGGERS;
	}

	void setState(BehaviourState state) {
		this.state = state;
	}

	void setFile(String file) {
		this.file = file;
	}

	private List<Trigger> makeTriggers(Element triggerElements, ObjectDatabase od) {
		List<Trigger> triggers = new ArrayList<>();

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
		List<Action> actions = new ArrayList<>();

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
