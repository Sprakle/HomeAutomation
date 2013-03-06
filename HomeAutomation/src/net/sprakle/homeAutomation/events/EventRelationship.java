/*
 * Defines callers and listeners for an event type
 */

package net.sprakle.homeAutomation.events;

import java.util.ArrayList;

public class EventRelationship {

	private EventType eventType;
	private ArrayList<EventListener> listeners;

	EventRelationship(EventType eventType) {
		this.eventType = eventType;
		listeners = new ArrayList<EventListener>();
	}

	void call(Event e) {
		for (EventListener el : listeners) {
			el.call(e);
		}
	}

	void addListener(EventListener el) {
		listeners.add(el);
	}

	void removeListener(EventListener el) {
		listeners.remove(el);
	}

	EventType getEventType() {
		return eventType;
	}
}
