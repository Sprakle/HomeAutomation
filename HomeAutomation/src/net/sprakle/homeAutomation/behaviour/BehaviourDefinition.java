package net.sprakle.homeAutomation.behaviour;

import java.util.ArrayList;
import java.util.List;

import net.sprakle.homeAutomation.behaviour.actions.ActionDefinition;
import net.sprakle.homeAutomation.behaviour.triggers.TriggerDefinition;

import org.dom4j.Element;

/**
 * Used to define a behaviour not based off an XML element
 * 
 * @author ben
 * 
 */
public class BehaviourDefinition {

	public String name;
	public String description;
	public int updatePeriod;

	public List<TriggerDefinition> triggers;
	public List<ActionDefinition> triggerStartActions;
	public List<ActionDefinition> triggerEndActions;

	/**
	 * 
	 * @param name
	 * @param description
	 * @param updatePeriod
	 *            update period in milliseconds. Us -1 for the default
	 */
	public BehaviourDefinition(String name, String description, int updatePeriod) {
		this.name = name;
		this.description = description;
		this.updatePeriod = updatePeriod;

		triggers = new ArrayList<TriggerDefinition>();
		triggerStartActions = new ArrayList<ActionDefinition>();
		triggerEndActions = new ArrayList<ActionDefinition>();
	}

	void copyDataToElement(Element behaviourElement) {

		// header info
		behaviourElement.addAttribute(XMLKeys.NAME, name);
		behaviourElement.addElement(XMLKeys.DESCRIPTION).setText(description);
		behaviourElement.addElement(XMLKeys.UPDATE_PERIOD).setText(new Integer(updatePeriod).toString());

		// triggers
		Element triggersElement = behaviourElement.addElement(XMLKeys.TRIGGERS);
		for (TriggerDefinition def : triggers) {
			Element triggerElement = triggersElement.addElement(XMLKeys.TRIGGER);
			def.copyDataToElement(triggerElement);
		}

		// trigger start actions
		Element TSAElement = behaviourElement.addElement(XMLKeys.TRIGGER_START_ACTIONS);
		for (ActionDefinition def : triggerStartActions) {
			Element actionElement = TSAElement.addElement(XMLKeys.ACTION);
			def.copyDataToElement(actionElement);
		}

		// trigger end actions
		Element TEAElement = behaviourElement.addElement(XMLKeys.TRIGGER_END_ACTION);
		for (ActionDefinition def : triggerEndActions) {
			Element actionElement = TEAElement.addElement(XMLKeys.ACTION);
			def.copyDataToElement(actionElement);
		}
	}
}
