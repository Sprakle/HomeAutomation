/*
 * The central hub for events.
 * 
 * How event listening works:
 * 		1) initialization:
 * 			Event listeners are added by calling EventManager.addEventListener(EventType, EventListener).
 * 			When a listener or caller is added, an event relationship is created (if not created already by a previous caller/listener).
 * 			An event relationship contains a list of callers and listeners, and an event type
 * 
 * 			Notes:
 * 				There can be any number of callers / listeners for each event type.
 * 				There will only be one of each event type. if there are 5 event types defines in the EventType enum, there will only be 5 possible application wide events.
 * 					Because of this, there can only one event relationship per event type
 * 
 * 		2) calling:
 * 			When a event is called on the Event Manager, the EM looks for the event relationship corresponding to the call.
 * 			Once the correct relationship is found, the relationship calls all of it's listeners.
 * 			
 * 			Notes:
 * 				A caller can pass an Event object that will be sent to the listeners
 */

package net.sprakle.homeAutomation.events;

import java.util.ArrayList;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class EventManager {
	private static EventManager instance = null;
	public static EventManager getInstance(Logger logger) {
		if (instance == null) {
			instance = new EventManager(logger);
		}
		return instance;
	}

	private Logger logger;

	private ArrayList<EventRelationship> relationships;

	private EventManager(Logger logger) {
		this.logger = logger;

		relationships = new ArrayList<EventRelationship>();
	}

	public void addListener(EventType eType, EventListener eListener) {

		EventRelationship targetRelationship = null;

		boolean alreadyExists = false;
		for (EventRelationship er : relationships) {
			if (er.getEventType().equals(eType))
			{
				alreadyExists = true;
				targetRelationship = er;
				break;
			}
		}

		if (!alreadyExists) {
			targetRelationship = new EventRelationship(eType);
			relationships.add(targetRelationship);
		}

		// no matter what, targetRelationship now can be used
		targetRelationship.addListener(eListener);
	}

	public void call(EventType eType, Event e) {
		logger.log("Event called: " + eType, LogSource.EVENT_INFO, 1);

		for (EventRelationship er : relationships) {
			if (er.getEventType().equals(eType)) {
				er.call(e);
				return;
			}
		}
	}
}
